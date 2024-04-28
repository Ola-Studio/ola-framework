package io.ola.crud.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.TypeUtil;
import com.mybatisflex.core.constant.SqlConnector;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

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
    private static final int DEFAULT_BATCH_SIZE = 1000;

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
        Criteria criteria = null;
        for (Field idField : entityMeta.getIdFields()) {
            if (Objects.isNull(criteria)) {
                criteria = new Criteria(idField.getName());
            } else {
                criteria.and(idField.getName());
            }
            criteria.is(id);
        }
        getMongoTemplate().remove(Query.query(criteria));
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
        //todo
        Criteria criteria = null;
        QueryCondition whereQueryCondition = CPI.getWhereQueryCondition(queryWrapper);
        List<QueryWrapper> childSelect = CPI.getChildSelect(queryWrapper);
        getMongoTemplate().remove(Query.query(criteria));
    }

    private Criteria toCriteria(QueryWrapper queryWrapper) {
        Field connector = ReflectUtil.getField(QueryCondition.class, "connector");
        connector.setAccessible(true);
        Criteria criteria = new Criteria();
        QueryCondition whereQueryCondition = CPI.getWhereQueryCondition(queryWrapper);
        boolean isAndConnector = SqlConnector.AND == ReflectUtil.getFieldValue(whereQueryCondition, connector);
        if (Objects.isNull(whereQueryCondition.getValue())) {
            List<QueryWrapper> childSelect = CPI.getChildSelect(queryWrapper);
            for (QueryWrapper wrapper : childSelect) {
                Criteria childCriteria = toCriteria(wrapper);
                if (isAndConnector) {
                    criteria.andOperator(childCriteria);
                } else {
                    criteria.orOperator(childCriteria);
                }

            }
        } else {
            //todo
            if (isAndConnector) {
                criteria.andOperator();
            } else {
                criteria.orOperator();
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
//            getDao().insert(entity);
        } else {
            InjectUtils.doBeforeUpdateInject(entity);
//            getDao().update(entity);
        }
        afterSave(entity);
        return entity;
    }

    @SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored", "SameParameterValue"})
    <T extends ENTITY> void saveBatch(Collection<T> entities, int batchSize) {
//        Class<BaseMapper<ENTITY>> usefulClass = (Class<BaseMapper<ENTITY>>) ClassUtil.getUsefulClass(getDao().getClass());
//        SqlUtil.toBool(Db.executeBatch(entities, batchSize, usefulClass, BaseMapper::insertOrUpdate));
    }

    @Override
    public <T extends ENTITY> Iterable<T> saveAll(Iterable<T> entities) {
        for (T entity : entities) {
            if (isNew(entity)) {
                InjectUtils.doBeforeSaveInject(entity);
            } else {
                InjectUtils.doBeforeUpdateInject(entity);
            }
        }
        List<T> entityList = StreamSupport
                .stream(entities.spliterator(), false)
                .collect(Collectors.toList());
        saveBatch(entityList, DEFAULT_BATCH_SIZE);
        return entityList;
    }

    @Override
    public ENTITY get(Serializable id) {
//        return getDao().selectOneWithRelationsById(id);
        return null;
    }

    @Override
    public ENTITY get(QueryWrapper queryWrapper) {
//        return getDao().selectOneByQuery(queryWrapper);
        return null;
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
//        return getDao().selectListByIds(idSet);
        return null;
    }

    @Override
    public List<ENTITY> list(QueryWrapper queryWrapper) {
//        return getDao().selectListWithRelationsByQuery(queryWrapper);
        return null;
    }

    @Override
    public Page<ENTITY> page(Page<ENTITY> page, QueryWrapper queryWrapper) {
//        return getDao().paginateWithRelations(page, queryWrapper);
        return null;
    }

}
