package io.ola.crud.service;

import com.mybatisflex.core.query.QueryWrapper;

import java.io.Serializable;

/**
 * @param <ENTITY> 实体类型
 * @author yiuman
 * @date 2023/8/2
 */
public interface DeletableService<ENTITY> {

    default void beforeDelete(ENTITY entity) {
    }

    <ID extends Serializable> void delete(ID id);

    void delete(ENTITY entity);

    <ID extends Serializable> void deleteByIds(Iterable<ID> ids);

    void deleteByQuery(QueryWrapper queryWrapper);
}