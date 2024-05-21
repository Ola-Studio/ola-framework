package io.ola.crud.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;

import java.io.Serializable;
import java.util.List;

/**
 * @param <ENTITY> 实体类型
 * @author yiuman
 * @date 2023/8/2
 */
public interface QueryService<ENTITY> {

    <ID extends Serializable> ID getId(ENTITY entity);

    <T extends ENTITY, ID extends Serializable> T get(ID id);

    <T extends ENTITY> T get(QueryWrapper queryWrapper);

    <T extends ENTITY> List<T> list();

    <T extends ENTITY, ID extends Serializable> List<T> list(Iterable<ID> ids);

    <T extends ENTITY> List<T> list(QueryWrapper queryWrapper);

    <T extends ENTITY> Page<T> page(Page<ENTITY> page, QueryWrapper queryWrapper);

    long count(QueryWrapper queryWrapper);
}