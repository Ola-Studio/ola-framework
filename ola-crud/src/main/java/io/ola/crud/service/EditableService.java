package io.ola.crud.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import io.ola.crud.CRUD;
import io.ola.crud.inject.InjectUtils;
import io.ola.crud.model.EntityMeta;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

/**
 * @param <ENTITY> 实体类型
 * @author yiuman
 * @date 2023/8/2
 */
@SuppressWarnings("unchecked")
public interface EditableService<ENTITY> {

    default <T extends ENTITY> void beforeSave(T entity) {
        if (isNew(entity)) {
            InjectUtils.doBeforeSaveInject(entity);
        } else {
            InjectUtils.doBeforeUpdateInject(entity);
        }
    }

    <T extends ENTITY> T save(T entity);

    default <T extends ENTITY> void afterSave(T entity) {
    }

    <T extends ENTITY> Iterable<T> saveAll(Iterable<T> entities);

    default <T extends ENTITY> boolean isNew(T entity) {
        EntityMeta<ENTITY> entityMeta = (EntityMeta<ENTITY>) CRUD.getEntityMeta(entity.getClass());
        List<Field> idFields = entityMeta.getIdFields();
        if (CollUtil.isEmpty(idFields)) {
            return true;
        }

        return idFields.stream().allMatch(field -> Objects.isNull(ReflectUtil.getFieldValue(entity, field)));
    }

}