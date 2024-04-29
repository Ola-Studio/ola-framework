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

    ENTITY get(Serializable id);

    ENTITY get(QueryWrapper queryWrapper);

    List<ENTITY> list();

    List<ENTITY> list(Iterable<Serializable> ids);

    List<ENTITY> list(QueryWrapper queryWrapper);

    Page<ENTITY> page(Page<ENTITY> page, QueryWrapper queryWrapper);

    long count(QueryWrapper queryWrapper);
}