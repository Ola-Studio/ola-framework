package io.ola.crud.service.impl;

import cn.hutool.core.util.TypeUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import io.ola.crud.CRUD;
import io.ola.crud.inject.InjectUtils;
import io.ola.crud.service.CrudService;

import java.io.Serializable;
import java.util.List;

/**
 * @param <ENTITY> 实体类型
 * @author yiuman
 * @date 2024/4/29
 */
@SuppressWarnings("unchecked")
public class BaseService<ENTITY> implements CrudService<ENTITY> {
    private final Class<ENTITY> entityClass = (Class<ENTITY>) TypeUtil.getTypeArgument(getClass(), 0);

    protected <PROXY extends CrudService<ENTITY>> PROXY getProxy() {
        return CRUD.getProxyCrudService(entityClass);
    }

    @Override
    public <ID extends Serializable> ID getId(ENTITY entity) {
        return getProxy().getId(entity);
    }

    @Override
    public <ID extends Serializable> void delete(ID id) {
        getProxy().delete(id);
    }

    @Override
    public void delete(ENTITY entity) {
        getProxy().delete(entity);
    }

    @Override
    public <ID extends Serializable> void deleteByIds(Iterable<ID> ids) {
        getProxy().deleteByIds(ids);
    }

    @Override
    public void deleteByQuery(QueryWrapper queryWrapper) {
        getProxy().deleteByQuery(queryWrapper);
    }

    @Override
    public <T extends ENTITY> void beforeSave(T entity) {
        getProxy().beforeSave(entity);
    }

    @Override
    public <T extends ENTITY> T save(T entity) {
        beforeSave(entity);
        if (isNew(entity)) {
            InjectUtils.doBeforeSaveInject(entity);
        } else {
            InjectUtils.doBeforeUpdateInject(entity);
        }
        getProxy().save(entity);
        afterSave(entity);
        return entity;
    }

    @Override
    public <T extends ENTITY> Iterable<T> saveAll(Iterable<T> entities) {
        return getProxy().saveAll(entities);
    }

    @Override
    public <T extends ENTITY, ID extends Serializable> T get(ID id) {
        return getProxy().get(id);
    }

    @Override
    public <T extends ENTITY> T get(QueryWrapper queryWrapper) {
        return getProxy().get(queryWrapper);
    }

    @Override
    public <T extends ENTITY> List<T> list() {
        return list(QueryWrapper.create());
    }

    @Override
    public <T extends ENTITY, ID extends Serializable> List<T> list(Iterable<ID> ids) {
        return getProxy().list(ids);
    }

    @Override
    public <T extends ENTITY> List<T> list(QueryWrapper queryWrapper) {
        return getProxy().list(queryWrapper);
    }

    @Override
    public <T extends ENTITY> Page<T> page(Page<ENTITY> page, QueryWrapper queryWrapper) {
        return getProxy().page(page, queryWrapper);
    }

    @Override
    public <T extends ENTITY> boolean isNew(T entity) {
        return getProxy().isNew(entity);

    }

    @Override
    public long count(QueryWrapper queryWrapper) {
        return getProxy().count(queryWrapper);
    }
}
