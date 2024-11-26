package io.ola.crud.query;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ReflectUtil;
import com.mybatisflex.core.query.QueryWrapper;
import io.ola.common.utils.SpringUtils;
import io.ola.crud.CRUD;
import io.ola.crud.enums.Clauses;
import io.ola.crud.model.EntityMeta;
import io.ola.crud.model.FieldColumnInfo;
import io.ola.crud.model.QueryFieldMeta;
import io.ola.crud.query.annotation.QueryField;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.MergedAnnotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author yiuman
 * @date 2023/7/26
 */
public final class QueryHelper {

    private static final Map<Class<?>, List<QueryFieldMeta>> CLASS_QUERY_FIELD_META_MAP = new ConcurrentHashMap<>();
    private static final ConditionHandler DEFAULT_CONDITION_HANDLER = DefaultConditionHandler.INSTANCE;

    private QueryHelper() {
    }

    public static QueryWrapper build(Object any) {
        return build(any, null);
    }

    public static QueryWrapper build(Object any, Class<?> queryTargetClass) {
        if (Objects.isNull(any)) {
            return QueryWrapper.create();
        }
        Class<?> objectClass = any.getClass();
        List<QueryFieldMeta> queryFieldMetas = getQueryFieldMetas(objectClass, queryTargetClass);
        if (CollUtil.isEmpty(queryFieldMetas)) {
            return QueryWrapper.create();
        }
        QueryWrapper wrapper = QueryWrapper.create();
        for (QueryFieldMeta queryFieldMeta : queryFieldMetas) {
            boolean require = BooleanUtil.isTrue(queryFieldMeta.getRequire());
            Clauses clauses = queryFieldMeta.getClauses();
            Class<? extends ConditionHandler> handleClass = queryFieldMeta.getHandleClass();
            Consumer<QueryWrapper> queryWrapperConsumer;
            Object fieldValue = ReflectUtil.getFieldValue(any, queryFieldMeta.getField());
            if (Objects.isNull(fieldValue)) {
                continue;
            }
            if (Objects.nonNull(handleClass)) {
                ConditionHandler conditionHandler = SpringUtils.getBean(handleClass, true);
                queryWrapperConsumer = conditionHandler.handle(queryFieldMeta, fieldValue);
            } else {
                queryWrapperConsumer = DEFAULT_CONDITION_HANDLER.handle(queryFieldMeta, fieldValue);
            }
            if (Clauses.AND == clauses) {
                wrapper.and(queryWrapperConsumer);
            } else {
                wrapper.or(queryWrapperConsumer, require);
            }

        }
        return wrapper;
    }

    public static List<QueryFieldMeta> getQueryFieldMetas(Class<?> queryClass, Class<?> entityClass) {
        synchronized (queryClass) {
            List<QueryFieldMeta> queryFieldMetas = CLASS_QUERY_FIELD_META_MAP.get(queryClass);
            if (Objects.nonNull(queryFieldMetas)) {
                return queryFieldMetas;
            }
            queryFieldMetas = new ArrayList<>();
            Field[] fieldsDirectly = ReflectUtil.getFieldsDirectly(queryClass, true);
            for (Field field : fieldsDirectly) {
                field.setAccessible(true);
                List<QueryFieldMeta> queryParamMeta = getQueryFieldMetas(queryClass, field, entityClass);
                if (CollUtil.isNotEmpty(queryParamMeta)) {
                    queryFieldMetas.addAll(queryParamMeta);
                }
            }
            CLASS_QUERY_FIELD_META_MAP.put(queryClass, queryFieldMetas);
            return queryFieldMetas;
        }

    }

    private static List<QueryFieldMeta> getQueryFieldMetas(Class<?> queryClass, Field field, Class<?> entityClass) {
        Set<QueryField> allMergedAnnotations = AnnotatedElementUtils.getAllMergedAnnotations(field, QueryField.class);
        if (CollUtil.isEmpty(allMergedAnnotations)) {
            return null;
        }
        List<QueryFieldMeta> queryFieldMetas = new ArrayList<>();
        for (QueryField queryField : allMergedAnnotations) {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(queryField);
            try {
                Field source = invocationHandler.getClass().getDeclaredField("annotation");
                source.setAccessible(true);
                MergedAnnotation<?> mergedAnnotation = (MergedAnnotation<?>) source.get(invocationHandler);
                Annotation sourceAnnotation = mergedAnnotation.getRoot().synthesize();
                FieldColumnInfo columnInfo = null;
                if (Objects.nonNull(entityClass)) {
                    EntityMeta<?> entityMeta = CRUD.getEntityMeta(entityClass);
                    columnInfo = CollUtil.findOne(
                            entityMeta.getFieldColumnInfos(),
                            columnInfoItem -> Objects.equals(field.getName(), columnInfoItem.getColumnInfo().getColumn())
                                    || Objects.equals(queryField.mapping(), columnInfoItem.getColumnInfo().getColumn())
                                    || Objects.equals(field.getName(), columnInfoItem.getField().getName())
                    );

                }
                queryFieldMetas.add(new QueryFieldMeta(queryClass, field, queryField, sourceAnnotation, columnInfo));
            } catch (IllegalAccessException | NoSuchFieldException ignore) {
            }

        }
        return queryFieldMetas;
    }

}
