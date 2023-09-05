package io.ola.crud.model;

import io.ola.crud.inject.ConditionInjector;
import io.ola.crud.service.CrudService;
import io.ola.crud.service.QueryService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CRUD元信息
 *
 * @param <ENTITY> 实体类型
 * @author yiuman
 * @date 2023/8/2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrudMeta<ENTITY> {
    private Class<?> apiClass;
    private Class<ENTITY> entityClass;
    private CrudService<ENTITY> crudService;
    private QueryService<ENTITY> queryService;
    private EntityMeta<ENTITY> entityMeta;
    private Class<?> queryClass;
    private ConditionInjector conditionInjector;
}
