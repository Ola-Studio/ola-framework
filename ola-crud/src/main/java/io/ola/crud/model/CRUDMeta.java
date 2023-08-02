package io.ola.crud.model;

import io.ola.crud.rest.BaseRESTAPI;
import io.ola.crud.service.CRUDService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CRUD元信息
 *
 * @author yiuman
 * @date 2023/8/2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CRUDMeta<ENTITY> {
    private Class<? extends BaseRESTAPI<ENTITY>> apiClass;
    private Class<ENTITY> entityClass;
    private CRUDService<ENTITY> service;
    private EntityMeta<ENTITY> entityMeta;
    private Class<?> queryClass;
}
