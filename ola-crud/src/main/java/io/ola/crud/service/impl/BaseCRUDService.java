package io.ola.crud.service.impl;

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
import io.ola.crud.service.CRUDService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author yiuman
 * @date 2023/8/2
 */
@SuppressWarnings({"SpringJavaInjectionPointsAutowiringInspection", "unchecked"})
public abstract class BaseCRUDService<DAO extends BaseMapper<ENTITY>, ENTITY> implements CRUDService<ENTITY> {

    private final Class<ENTITY> entityClass = (Class<ENTITY>) TypeUtil.getTypeArgument(getClass(), 1);
    public int DEFAULT_BATCH_SIZE = 1000;
    @Autowired
    protected DAO dao;

    @Override
    public void delete(Serializable id) {
        dao.deleteById(id);
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
        dao.deleteBatchByIds(idSet);
    }

    @Override
    public void deleteByQuery(QueryWrapper queryWrapper) {
        dao.deleteByQuery(queryWrapper);
        throw new UnsupportedOperationException("Method not implemented.");
    }

    @Override
    public <T extends ENTITY> T save(T entity) {
        beforeSave(entity);
        dao.insert(entity);
        afterSave(entity);
        return entity;
    }

    @SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored"})
    <T extends ENTITY> void saveBatch(Collection<T> entities, int batchSize) {
        Class<BaseMapper<ENTITY>> usefulClass = (Class<BaseMapper<ENTITY>>) ClassUtil.getUsefulClass(dao.getClass());
        SqlUtil.toBool(Db.executeBatch(entities, batchSize, usefulClass, BaseMapper::insert));
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
        return dao.selectOneById(id);
    }

    @Override
    public ENTITY get(QueryWrapper queryWrapper) {
        return dao.selectOneByQuery(queryWrapper);
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
        return dao.selectListByIds(idSet);
    }

    @Override
    public List<ENTITY> list(QueryWrapper queryWrapper) {
        return dao.selectListByQueryAs(queryWrapper, entityClass);
    }

    @Override
    public Page<ENTITY> page(Page<ENTITY> page, QueryWrapper queryWrapper) {
        return dao.paginate(page, queryWrapper);
    }
}
