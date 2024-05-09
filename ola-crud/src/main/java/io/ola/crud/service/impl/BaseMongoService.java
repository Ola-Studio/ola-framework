package io.ola.crud.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import com.mybatisflex.core.constant.SqlConnector;
import com.mybatisflex.core.constant.SqlConsts;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.CPI;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.table.ColumnInfo;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import io.ola.common.utils.SpringUtils;
import io.ola.crud.CRUD;
import io.ola.crud.inject.InjectUtils;
import io.ola.crud.model.EntityMeta;
import io.ola.crud.service.CrudService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @param <ENTITY> 实体类型
 * @author yiuman
 * @date 2024/4/28
 */
@SuppressWarnings("unchecked")
public abstract class BaseMongoService<ENTITY> implements CrudService<ENTITY> {

    private final Class<ENTITY> entityClass = (Class<ENTITY>) TypeUtil.getTypeArgument(getClass(), 0);

    protected MongoTemplate getMongoTemplate() {
        return SpringUtils.getBean(MongoTemplate.class);
    }

    @Override
    public <ID extends Serializable> ID getId(ENTITY entity) {
        return CRUD.getId(entity);
    }

    @Override
    public void delete(Serializable id) {
        EntityMeta<ENTITY> entityMeta = CRUD.getEntityMeta(entityClass);
        Field idField = CollUtil.getFirst(entityMeta.getIdFields());
        getMongoTemplate().remove(Query.query(Criteria.where(idField.getName()).is(id)));
    }

    @Override
    public void delete(ENTITY entity) {
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(entityClass);
        EntityMeta<ENTITY> entityMeta = CRUD.getEntityMeta(entityClass);
        Map<String, Field> fieldMap = entityMeta.getIdFields().stream()
                .collect(Collectors.toMap(Field::getName, field -> field));
        Map<String, Object> idConditionMap = tableInfo.getPrimaryKeyList()
                .stream()
                .collect(Collectors.toMap(ColumnInfo::getColumn,
                        id -> ReflectUtil.getFieldValue(entity, fieldMap.get(id.getProperty()))
                ));
        deleteByQuery(QueryWrapper.create().where(idConditionMap));
    }

    @Override
    public void deleteByIds(Iterable<Serializable> ids) {
        Set<Serializable> idSet = StreamSupport
                .stream(ids.spliterator(), false)
                .collect(Collectors.toSet());
        EntityMeta<ENTITY> entityMeta = CRUD.getEntityMeta(entityClass);
        Field idField = CollUtil.getFirst(entityMeta.getIdFields());
        getMongoTemplate().remove(Query.query(Criteria.where(idField.getName()).in(idSet)));
    }

    @Override
    public void deleteByQuery(QueryWrapper queryWrapper) {
        getMongoTemplate().remove(Query.query(toCriteria(queryWrapper)));
    }

    private Criteria toCriteria(QueryWrapper queryWrapper) {
        QueryCondition whereQueryCondition = CPI.getWhereQueryCondition(queryWrapper);
        if (Objects.isNull(whereQueryCondition)) {
            return new Criteria();
        }
        Criteria criteria;
        if (Objects.isNull(whereQueryCondition.getValue())) {
            List<QueryWrapper> childSelect = CPI.getChildSelect(queryWrapper);
            criteria = new Criteria();
            boolean isAndConnector = Objects.equals(SqlConnector.AND, ReflectUtil.getFieldValue(whereQueryCondition, "connector"));
            for (QueryWrapper wrapper : childSelect) {
                Criteria childCriteria = toCriteria(wrapper);
                if (isAndConnector) {
                    criteria.andOperator(childCriteria);
                } else {
                    criteria.orOperator(childCriteria);
                }
            }
        } else {
            criteria = Criteria.where(whereQueryCondition.getColumn().getName());
            switch (whereQueryCondition.getLogic()) {
                case SqlConsts.GT -> criteria.gt(whereQueryCondition.getValue());
                case SqlConsts.GE -> criteria.gte(whereQueryCondition.getValue());
                case SqlConsts.LT -> criteria.lt(whereQueryCondition.getValue());
                case SqlConsts.LE -> criteria.lte(whereQueryCondition.getValue());
                case SqlConsts.LIKE -> {
                    String value = StrUtil.toString(whereQueryCondition.getValue());
                    value = StrUtil.replaceFirst(value, "%", "");
                    value = StrUtil.replaceLast(value, "%", "");
                    criteria.regex(value, "i");
                }
                default -> criteria.is(whereQueryCondition.getValue());
            }
        }
        return criteria;
    }

    @Override
    public <T extends ENTITY> void beforeSave(T entity) {
    }

    @Override
    public <T extends ENTITY> T save(T entity) {
        beforeSave(entity);
        if (isNew(entity)) {
            InjectUtils.doBeforeSaveInject(entity);
        } else {
            InjectUtils.doBeforeUpdateInject(entity);
        }
        getMongoTemplate().save(entity);
        afterSave(entity);
        return entity;
    }

    @Override
    public <T extends ENTITY> Iterable<T> saveAll(Iterable<T> entities) {
        if (CollUtil.isEmpty(entities)) {
            return entities;
        }
        List<T> updates = new ArrayList<>();
        List<T> inserts = new ArrayList<>();
        for (T entity : entities) {
            if (isNew(entity)) {
                InjectUtils.doBeforeSaveInject(entity);
                inserts.add(entity);
            } else {
                InjectUtils.doBeforeUpdateInject(entity);
                updates.add(entity);
            }
        }

        BulkOperations bulkOperations = getMongoTemplate().bulkOps(BulkOperations.BulkMode.ORDERED, entityClass);
        if (CollUtil.isNotEmpty(inserts)) {
            bulkOperations.insert(inserts);
        }

        if (CollUtil.isNotEmpty(updates)) {
            EntityMeta<ENTITY> entityMeta = CRUD.getEntityMeta(entityClass);
            Map<Field, ColumnInfo> fieldColumnInfoMap = entityMeta.getFieldColumnInfoMap();
            Field idField = CollUtil.getFirst(entityMeta.getIdFields());
            updates.forEach(updateEntity -> {
                Update update = new Update();
                for (Map.Entry<Field, ColumnInfo> fieldColumnInfoEntry : fieldColumnInfoMap.entrySet()) {
                    Field field = fieldColumnInfoEntry.getKey();
                    ColumnInfo value = fieldColumnInfoEntry.getValue();
                    update.set(value.getColumn(), ReflectUtil.getFieldValue(updateEntity, field));
                }

                bulkOperations.updateOne(Query.query(Criteria.where(idField.getName())
                        .is(ReflectUtil.getFieldValue(updateEntity, idField))), update);
            });
        }
        bulkOperations.execute();
        return entities;
    }

    @Override
    public ENTITY get(Serializable id) {
        return getMongoTemplate().findById(id, entityClass);
    }

    @Override
    public ENTITY get(QueryWrapper queryWrapper) {
        return getMongoTemplate().findOne(Query.query(toCriteria(queryWrapper)), entityClass);
    }

    @Override
    public List<ENTITY> list() {
        return list(QueryWrapper.create());
    }

    @Override
    public List<ENTITY> list(Iterable<Serializable> ids) {
        Set<Serializable> idSet = StreamSupport
                .stream(ids.spliterator(), false)
                .collect(Collectors.toSet());
        EntityMeta<ENTITY> entityMeta = CRUD.getEntityMeta(entityClass);
        List<Field> idFields = entityMeta.getIdFields();
        Field idField = CollUtil.getFirst(idFields);
        return getMongoTemplate().find(Query.query(Criteria.where(idField.getName()).in(idSet)), entityClass);
    }

    @Override
    public List<ENTITY> list(QueryWrapper queryWrapper) {
        return getMongoTemplate().find(Query.query(toCriteria(queryWrapper)), entityClass);
    }

    @Override
    public Page<ENTITY> page(Page<ENTITY> page, QueryWrapper queryWrapper) {
        List<ENTITY> entities = getMongoTemplate().find(Query.query(toCriteria(queryWrapper))
                        .with(PageRequest.of((int) page.getPageNumber() - 1, (int) page.getPageSize())),
                entityClass
        );
        Page<ENTITY> objectPage = new Page<>();
        objectPage.setPageNumber(page.getPageNumber());
        objectPage.setPageSize(page.getPageSize());
        objectPage.setTotalRow(count(queryWrapper));
        objectPage.setRecords(entities);
        return objectPage;
    }

    @Override
    public long count(QueryWrapper queryWrapper) {
        return getMongoTemplate().count(Query.query(toCriteria(queryWrapper)), entityClass);
    }
}
