package io.ola.crud.service;

/**
 * @param <ENTITY> 实体类型
 * @author yiuman
 * @date 2023/8/2
 */
public interface CRUDService<ENTITY>
        extends SelectService<ENTITY>,
        EditableService<ENTITY>,
        DeletableService<ENTITY> {
}