package io.ola.crud.service;

import com.mybatisflex.core.query.QueryWrapper;

import java.io.Serializable;

/**
 * @author yiuman
 * @date 2023/8/2
 */
public interface DeletableService<ENTITY> {

    default void beforeDelete(ENTITY entity){};

    void delete(Serializable id);

    void delete(ENTITY entity);

    void deleteByIds(Iterable<Serializable > ids);

    void deleteByQuery(QueryWrapper queryWrapper);
}