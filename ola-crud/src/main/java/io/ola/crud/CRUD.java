package io.ola.crud;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.TypeUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import io.ola.common.utils.SpringUtils;
import io.ola.crud.annotation.BeforeSave;
import io.ola.crud.annotation.BeforeUpdate;
import io.ola.crud.annotation.DeleteTag;
import io.ola.crud.annotation.Sort;
import io.ola.crud.inject.Injector;
import io.ola.crud.model.CRUDMeta;
import io.ola.crud.model.EntityMeta;
import io.ola.crud.model.InjectFieldMeta;
import io.ola.crud.query.annotation.Query;
import io.ola.crud.rest.BaseQueryAPI;
import io.ola.crud.rest.BaseRESTAPI;
import io.ola.crud.service.CRUDService;
import io.ola.crud.service.QueryService;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.core.ResolvableType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * CRUD元信息管理
 *
 * @author yiuman
 * @date 2023/8/2
 */
@SuppressWarnings("unchecked")
public final class CRUD {

    private static final Map<Class<?>, CRUDMeta<?>> CRUD_META_MAP = new ConcurrentHashMap<>();
    private static final Map<Class<?>, EntityMeta<?>> ENTITY_META_MAP = new ConcurrentHashMap<>();
    private static final Map<Class<?>, BaseMapper<?>> ENTITY_CLASS_MAPPER_MAP = new ConcurrentHashMap<>();

    public static <ENTITY> CRUDMeta<ENTITY> getCRUDMeta(Class<?> apiClass) {
        CRUDMeta<ENTITY> crudMeta = (CRUDMeta<ENTITY>) CRUD_META_MAP.get(apiClass);
        if (Objects.isNull(crudMeta)) {
            crudMeta = initCRUDMeta(apiClass);
            CRUD_META_MAP.put(apiClass, crudMeta);
        }
        return crudMeta;
    }

    public static <ENTITY, API extends BaseRESTAPI<ENTITY>> CRUDService<ENTITY> getService(Class<API> crudClass) {
        return (CRUDService<ENTITY>) getCRUDMeta(crudClass).getCrudService();
    }

    public static <ENTITY, API extends BaseQueryAPI<ENTITY>> QueryService<ENTITY> getQueryService(Class<API> queryClass) {
        return (QueryService<ENTITY>) getCRUDMeta(queryClass).getQueryService();
    }

    public static <ENTITY, M extends BaseMapper<ENTITY>> M getMapper(Class<ENTITY> entityClass) {
        BaseMapper<?> baseMapper = ENTITY_CLASS_MAPPER_MAP.get(entityClass);
        if (Objects.nonNull(baseMapper)) {
            return (M) baseMapper;
        }
        SqlSessionTemplate sqlSessionTemplate = SpringUtils.getBean(SqlSessionTemplate.class);
        Collection<Class<?>> mappers = sqlSessionTemplate.getConfiguration().getMapperRegistry().getMappers();
        Class<?> matchMapperClass = mappers.stream().filter(mapperClass -> {
                    return Objects.equals(TypeUtil.getTypeArgument(mapperClass, 0), entityClass);
                })
                .findFirst()
                .orElseThrow(
                        () -> new RuntimeException(String.format("can not found mapper for entity %s", entityClass))
                );

        baseMapper = (BaseMapper<ENTITY>) sqlSessionTemplate.getMapper(matchMapperClass);
        ENTITY_CLASS_MAPPER_MAP.put(entityClass, baseMapper);
        return (M) baseMapper;
    }

    public static <ENTITY> Class<?> getQueryClass(Class<? extends BaseRESTAPI<ENTITY>> crudClass) {
        return getCRUDMeta(crudClass).getQueryClass();
    }

    private static <ENTITY> CRUDMeta<ENTITY> initCRUDMeta(Class<?> apiClass) {
        synchronized (apiClass) {
            Type typeArgument = TypeUtil.getTypeArgument(apiClass, 0);
            Class<ENTITY> entityClass = (Class<ENTITY>) typeArgument;
            Query query = AnnotationUtil.getAnnotation(apiClass, Query.class);
            Class<?> queryClass = Objects.isNull(query) ? null : query.value();
            EntityMeta<ENTITY> entityEntityMeta = getEntityMeta((Class<ENTITY>) typeArgument);
            CRUDService<ENTITY> crudService = getCrudService(entityClass);
            QueryService<ENTITY> queryService = getSelectService(entityClass);
            return new CRUDMeta<>(apiClass, entityClass, crudService, queryService, entityEntityMeta, queryClass);
        }
    }

    public static <ENTITY> QueryService<ENTITY> getSelectService(Class<ENTITY> entityClass) {
        TypeReference<QueryService<ENTITY>> queryReference = new TypeReference<>() {
        };
        final ParameterizedType parameterizedType = (ParameterizedType) queryReference.getType();
        final Class<QueryService<ENTITY>> rawType = (Class<QueryService<ENTITY>>) parameterizedType.getRawType();
        final Class<?>[] genericTypes = new Class[]{entityClass};
        final String[] beanNames = SpringUtil.getBeanFactory().getBeanNamesForType(ResolvableType.forClassWithGenerics(rawType, genericTypes));
        return SpringUtil.getBean(beanNames[0], rawType);
    }

    public static <ENTITY> CRUDService<ENTITY> getCrudService(Class<ENTITY> entityClass) {
        TypeReference<CRUDService<ENTITY>> queryReference = new TypeReference<>() {
        };
        final ParameterizedType parameterizedType = (ParameterizedType) queryReference.getType();
        final Class<CRUDService<ENTITY>> rawType = (Class<CRUDService<ENTITY>>) parameterizedType.getRawType();
        final Class<?>[] genericTypes = new Class[]{entityClass};
        final String[] beanNames = SpringUtil.getBeanFactory().getBeanNamesForType(ResolvableType.forClassWithGenerics(rawType, genericTypes));
        return SpringUtil.getBean(beanNames[0], rawType);
    }

    public static <ENTITY> EntityMeta<ENTITY> getEntityMeta(Class<ENTITY> entityClass) {
        EntityMeta<ENTITY> entityMeta = (EntityMeta<ENTITY>) ENTITY_META_MAP.get(entityClass);
        if (Objects.nonNull(entityMeta)) {
            return entityMeta;
        }
        synchronized (entityClass) {
            entityMeta = initEntityMeta(entityClass);
            ENTITY_META_MAP.put(entityClass, entityMeta);
        }
        return entityMeta;
    }

    private static <ENTITY> EntityMeta<ENTITY> initEntityMeta(Class<ENTITY> entityClass) {
        EntityMeta<ENTITY> entityMeta = new EntityMeta<>();
        entityMeta.setEntityClass(entityClass);
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(entityClass);
        if (Objects.nonNull(tableInfo)) {
            List<Field> idFields = tableInfo.getPrimaryKeyList().stream()
                    .map(idInfo -> ReflectUtil.getField(entityClass, idInfo.getProperty()))
                    .collect(Collectors.toList());
            entityMeta.setIdFields(idFields);

        }
        entityMeta.setDeleteTagField(getDeleteTagField(entityClass));
        entityMeta.setSortTagField(getSortField(entityClass));
        entityMeta.setBeforeSaveInjectMetas(getInjectFieldMetas(entityClass, BeforeSave.class));
        entityMeta.setBeforeUpdateInjectMetas(getInjectFieldMetas(entityClass, BeforeUpdate.class));
        return entityMeta;
    }

    public static Field getSortField(Class<?> entityClass) {
        return getAnnotationField(entityClass, Sort.class).orElse(null);
    }

    public static Field getDeleteTagField(Class<?> entityClass) {
        return getAnnotationField(entityClass, DeleteTag.class).orElse(null);
    }

    private static Optional<Field> getAnnotationField(Class<?> entityClass, Class<? extends Annotation> annotationClass) {
        return getAnnotationFields(entityClass, annotationClass).stream().findFirst();
    }

    private static List<Field> getAnnotationFields(Class<?> entityClass, Class<? extends Annotation> annotationClass) {
        Field[] fields = ReflectUtil.getFieldsDirectly(entityClass, true);
        return Arrays.stream(fields).filter(field -> {
            field.setAccessible(true);
            return Objects.nonNull(AnnotationUtil.getAnnotation(field, annotationClass));
        }).collect(Collectors.toList());
    }

    private static List<InjectFieldMeta> getInjectFieldMetas(Class<?> entityClass, Class<? extends Annotation> annotationClass) {
        Field[] fields = ReflectUtil.getFieldsDirectly(entityClass, true);
        return Arrays.stream(fields).map(field -> {
            field.setAccessible(true);
            Annotation annotation = AnnotationUtil.getAnnotation(field, annotationClass);
            if (Objects.isNull(annotation)) {
                return null;
            }
            InjectFieldMeta injectFieldMeta = new InjectFieldMeta();
            injectFieldMeta.setEntityClass(entityClass);
            injectFieldMeta.setField(field);
            injectFieldMeta.setAnnotation(annotation);
            Map<String, ?> memberValues = AnnotationUtil.getAnnotationValueMap(field, annotationClass);
            injectFieldMeta.setInjectorClass((Class<? extends Injector>) memberValues.get("value"));
            injectFieldMeta.setForce((Boolean) memberValues.get("force"));
            return injectFieldMeta;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
