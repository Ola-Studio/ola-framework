package io.ola.crud.inject;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import io.ola.common.utils.SpringUtils;
import io.ola.crud.CRUD;
import io.ola.crud.model.EntityMeta;
import io.ola.crud.model.InjectFieldMeta;

import java.util.List;
import java.util.Objects;

/**
 * @author yiuman
 * @date 2023/8/2
 */
public final class InjectUtils {

    public static void doBeforeSaveInject(Object object) {
        EntityMeta<?> entityMeta = CRUD.getEntityMeta(object.getClass());
        List<InjectFieldMeta> beforeSaveInjectMetas = entityMeta.getBeforeSaveInjectMetas();
        if (CollUtil.isEmpty(beforeSaveInjectMetas)) {
            return;
        }
        doInject(object, beforeSaveInjectMetas);
    }

    public static void doBeforeUpdateInject(Object object) {
        EntityMeta<?> entityMeta = CRUD.getEntityMeta(object.getClass());
        List<InjectFieldMeta> beforeUpdateInjectMetas = entityMeta.getBeforeUpdateInjectMetas();
        if (CollUtil.isEmpty(beforeUpdateInjectMetas)) {
            return;
        }
        doInject(object, beforeUpdateInjectMetas);
    }

    public static void doInject(Object object, List<InjectFieldMeta> injectMetas) {
        for (InjectFieldMeta beforeUpdateInjectMeta : injectMetas) {
            Injector injector = (Injector) SpringUtils.getBean(beforeUpdateInjectMeta.getInjectorClass(), true);
            Object injectValue = injector.getInjectValue(beforeUpdateInjectMeta.getField());
            if (Objects.nonNull(injectValue) || beforeUpdateInjectMeta.getForce()) {
                ReflectUtil.setFieldValue(object, beforeUpdateInjectMeta.getField(), injectValue);
            }
        }
    }
}
