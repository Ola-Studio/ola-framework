package io.ola.crud.service;

/**
 * @author yiuman
 * @date 2023/8/2
 */
public interface CRUDService<ENTITY>
        extends SelectService<ENTITY>,
        EditableService<ENTITY>,
        DeletableService<ENTITY> {
}