package io.ola.crud.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.TypeUtil;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.table.ColumnInfo;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.SqlUtil;
import io.ola.crud.CRUD;
import io.ola.crud.inject.InjectUtils;
import io.ola.crud.model.EntityMeta;
import io.ola.crud.model.IDs;
import io.ola.crud.service.CrudService;
import io.ola.crud.utils.MapperUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @param <ENTITY> 实体类型
 * @author yiuman
 * @date 2023/8/2
 */
@SuppressWarnings({"unchecked"})
public abstract class BaseCrudService<ENTITY> implements CrudService<ENTITY> {

    private static final int DEFAULT_BATCH_SIZE = 1000;
    private final Class<ENTITY> entityClass = (Class<ENTITY>) TypeUtil.getTypeArgument(getClass(), 0);

    protected <DAO extends BaseMapper<ENTITY>> DAO getDao() {
        return MapperUtils.getMapper(entityClass);
    }

    @Override
    public <ID extends Serializable> ID getId(ENTITY entity) {
        return CRUD.getId(entity);
    }

    @Override
    public <ID extends Serializable> void delete(ID id) {
        getDao().deleteById(id);
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
    public <ID extends Serializable> void deleteByIds(Iterable<ID> ids) {
        Set<Serializable> idSet = StreamSupport
                .stream(ids.spliterator(), false)
                .collect(Collectors.toSet());
        getDao().deleteBatchByIds(idSet);
    }

    @Override
    public void deleteByQuery(QueryWrapper queryWrapper) {
        getDao().deleteByQuery(queryWrapper);
    }

    @Override
    public <T extends ENTITY> void beforeSave(T entity) {
    }

    @Override
    public <T extends ENTITY> T save(T entity) {
        beforeSave(entity);
        if (isNew(entity)) {
            InjectUtils.doBeforeSaveInject(entity);
            getDao().insert(entity);
        } else {
            InjectUtils.doBeforeUpdateInject(entity);
            getDao().update(entity);
        }
        afterSave(entity);
        return entity;
    }

    @SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored", "SameParameterValue"})
    <T extends ENTITY> void saveBatch(Collection<T> entities, int batchSize) {
        Class<BaseMapper<ENTITY>> usefulClass = (Class<BaseMapper<ENTITY>>) ClassUtil.getUsefulClass(getDao().getClass());
        SqlUtil.toBool(Db.executeBatch(entities, batchSize, usefulClass, BaseMapper::insertOrUpdate));
    }

    @Override
    public <T extends ENTITY> Iterable<T> saveAll(Iterable<T> entities) {
        List<ENTITY> inserts = new ArrayList<>();
        List<ENTITY> updates = new ArrayList<>();
        for (T entity : entities) {
            if (isNew(entity)) {
                InjectUtils.doBeforeSaveInject(entity);
                inserts.add(entity);
            } else {
                InjectUtils.doBeforeUpdateInject(entity);
                updates.add(entity);
            }
        }
        if (CollUtil.isNotEmpty(inserts)) {
            getDao().insertBatch(inserts);
        }

        if (CollUtil.isNotEmpty(updates)) {
            saveBatch(updates, DEFAULT_BATCH_SIZE);
        }
        return StreamSupport.stream(entities.spliterator(), true)
                .collect(Collectors.toList());
    }

    @Override
    public <T extends ENTITY, ID extends Serializable> T get(ID id) {
        return (T) getDao().selectOneWithRelationsById(id);
    }

    @Override
    public <T extends ENTITY> T get(QueryWrapper queryWrapper) {
        return (T) getDao().selectOneByQuery(queryWrapper);
    }

    @Override
    public <T extends ENTITY> List<T> list() {
        return list(QueryWrapper.create());
    }

    @Override
    public <T extends ENTITY, ID extends Serializable> List<T> list(Iterable<ID> ids) {
        if (CollUtil.isEmpty(ids)) {
            return CollUtil.newArrayList();
        }
        Set<Serializable> idSet = StreamSupport
                .stream(ids.spliterator(), false)
                .collect(Collectors.toSet());
        return (List<T>) getDao().selectListByIds(idSet);
    }

    @Override
    public <T extends ENTITY> List<T> list(QueryWrapper queryWrapper) {
        return (List<T>) getDao().selectListWithRelationsByQuery(queryWrapper);
    }

    @Override
    public <T extends ENTITY> Page<T> page(Page<ENTITY> page, QueryWrapper queryWrapper) {
        return (Page<T>) getDao().paginateWithRelations(page, queryWrapper);
    }

    @Override
    public <T extends ENTITY> boolean isNew(T entity) {
        boolean isNew = CrudService.super.isNew(entity);
        if (isNew) {
            return true;
        }

        Serializable id = getId(entity);
        if (id instanceof IDs ids) {
            return Objects.isNull(MapperUtils.queryByIds(ids, entityClass));
        } else {
            return Objects.isNull(getDao().selectOneById(getId(entity)));
        }

    }

    @Override
    public long count(QueryWrapper queryWrapper) {
        return getDao().selectCountByQuery(queryWrapper);
    }
}
