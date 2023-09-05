package io.ola.crud.inject;

import com.mybatisflex.core.query.QueryWrapper;

/**
 * @author yiuman
 * @date 2023/9/5
 */
public interface ConditionInjector {
    void inject(QueryWrapper queryWrapper, Class<?> entityClass);
}