package io.ola.crud;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.table.ColumnInfo;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import io.ola.common.utils.JavassistUtils;
import io.ola.common.utils.SpringUtils;
import io.ola.crud.annotation.BeforeSave;
import io.ola.crud.annotation.BeforeUpdate;
import io.ola.crud.annotation.DeleteTag;
import io.ola.crud.annotation.Sort;
import io.ola.crud.enums.DbType;
import io.ola.crud.inject.ConditionInject;
import io.ola.crud.inject.ConditionInjector;
import io.ola.crud.inject.Injector;
import io.ola.crud.model.*;
import io.ola.crud.query.annotation.Query;
import io.ola.crud.rest.BaseQueryAPI;
import io.ola.crud.rest.BaseRESTAPI;
import io.ola.crud.service.CrudService;
import io.ola.crud.service.QueryService;
import io.ola.crud.service.impl.BaseCrudService;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.core.ResolvableType;

import java.io.Serializable;
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
    private static final String ENTITY_DB_TYPE_MAP = "ENTITY_DB_TYPE_MAP";

    private static final Map<Class<?>, CrudMeta<?>> CRUD_META_MAP = new ConcurrentHashMap<>();
    private static final Map<Class<?>, EntityMeta<?>> ENTITY_META_MAP = new ConcurrentHashMap<>();
    private static final Map<Class<?>, BaseMapper<?>> ENTITY_CLASS_MAPPER_MAP = new ConcurrentHashMap<>();

    public static <ENTITY> CrudMeta<ENTITY> getCRUDMeta(Class<?> apiClass) {
        CrudMeta<ENTITY> crudMeta = (CrudMeta<ENTITY>) CRUD_META_MAP.get(apiClass);
        if (Objects.isNull(crudMeta)) {
            crudMeta = initCRUDMeta(apiClass);
            CRUD_META_MAP.put(apiClass, crudMeta);
        }
        return crudMeta;
    }

    public static <ENTITY, API extends BaseRESTAPI<ENTITY>> CrudService<ENTITY> getService(Class<API> crudClass) {
        return (CrudService<ENTITY>) getCRUDMeta(crudClass).getCrudService();
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
        Class<?> matchMapperClass = mappers.stream()
                .filter(mapperClass -> Objects.equals(TypeUtil.getTypeArgument(mapperClass, 0), entityClass))
                .findFirst()
                .orElseThrow(
                        () -> new RuntimeException(String.format("can not found mapper for entity %s", entityClass))
                );

        baseMapper = (BaseMapper<ENTITY>) sqlSessionTemplate.getMapper(matchMapperClass);
        ENTITY_CLASS_MAPPER_MAP.put(entityClass, baseMapper);
        return (M) baseMapper;
    }

    public static Class<?> getQueryClass(Class<?> apiClass) {
        return getCRUDMeta(apiClass).getQueryClass();
    }

    private static <ENTITY> CrudMeta<ENTITY> initCRUDMeta(Class<?> apiClass) {
        synchronized (apiClass) {
            Type typeArgument = TypeUtil.getTypeArgument(apiClass, 0);
            Class<ENTITY> entityClass = (Class<ENTITY>) typeArgument;
            //查询类
            Query query = AnnotationUtil.getAnnotation(apiClass, Query.class);
            Class<?> queryClass = Objects.isNull(query) ? null : query.value();
            EntityMeta<ENTITY> entityEntityMeta = getEntityMeta((Class<ENTITY>) typeArgument);
            CrudService<ENTITY> crudService = getCrudService(entityClass);
            QueryService<ENTITY> queryService = getSelectService(entityClass);
            //条件注入器
            ConditionInject conditionInject = AnnotationUtil.getAnnotation(apiClass, ConditionInject.class);
            ConditionInjector conditionInjector = null;
            if (Objects.nonNull(conditionInject)) {
                conditionInjector = SpringUtils.getBean(ConditionInjector.class, true);
            }
            return new CrudMeta<>(apiClass, entityClass, crudService, queryService, entityEntityMeta, queryClass, conditionInjector);
        }
    }

    public static <ENTITY> QueryService<ENTITY> getSelectService(Class<ENTITY> entityClass) {
        TypeReference<QueryService<ENTITY>> queryReference = new TypeReference<>() {
        };
        final ParameterizedType parameterizedType = (ParameterizedType) queryReference.getType();
        final Class<QueryService<ENTITY>> rawType = (Class<QueryService<ENTITY>>) parameterizedType.getRawType();
        final Class<?>[] genericTypes = new Class[]{entityClass};
        final String[] beanNames = SpringUtils.getBeanFactory().getBeanNamesForType(ResolvableType.forClassWithGenerics(rawType, genericTypes));
        return SpringUtil.getBean(beanNames[0], rawType);
    }

    public static <ENTITY> CrudService<ENTITY> getCrudService(Class<ENTITY> entityClass) {
        return getCrudService(entityClass, BaseCrudService.class);
    }

    public static <ENTITY, SERVICE extends CrudService<ENTITY>> CrudService<ENTITY> getCrudService(Class<ENTITY> entityClass, Class<SERVICE> serviceClass) {
        TypeReference<CrudService<ENTITY>> queryReference = new TypeReference<>() {
        };
        final ParameterizedType parameterizedType = (ParameterizedType) queryReference.getType();
        final Class<CrudService<ENTITY>> rawType = (Class<CrudService<ENTITY>>) parameterizedType.getRawType();
        final Class<?>[] genericTypes = new Class[]{entityClass};
        final String[] beanNames = SpringUtils.getBeanFactory().getBeanNamesForType(ResolvableType.forClassWithGenerics(rawType, genericTypes));
        if (ArrayUtil.isNotEmpty(beanNames)) {
            return SpringUtils.getBean(beanNames[0], rawType);
        }

        Class<? extends CrudService<ENTITY>> makeService = makeServiceClass(entityClass, serviceClass);
        return SpringUtils.getBean(makeService, true);
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
        EntityDbTypeMap entityDbTypeMap = SpringUtils.getBean(ENTITY_DB_TYPE_MAP, EntityDbTypeMap.class);
        entityMeta.setDeleteTagField(getDeleteTagField(entityClass));
        entityMeta.setSortTagField(getSortField(entityClass));
        entityMeta.setDbType(Optional.ofNullable(entityDbTypeMap.get(entityClass)).orElse(DbType.MYSQL));
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

    public static <ID extends Serializable, ENTITY> ID getId(ENTITY entity) {
        EntityMeta<ENTITY> entityMeta = (EntityMeta<ENTITY>) CRUD.getEntityMeta(entity.getClass());
        try {
            List<Field> idFields = entityMeta.getIdFields();
            if (CollUtil.size(idFields) > 1) {
                return (ID) IDs.builder()
                        .ids(
                                idFields.stream()
                                        .map(field -> new FieldValue(field, ReflectUtil.getFieldValue(entity, field)))
                                        .collect(Collectors.toList())
                        )
                        .build();
            } else {
                return (ID) CollUtil.getFirst(idFields).get(entity);
            }

        } catch (IllegalAccessException illegalAccessException) {
            throw new RuntimeException("CRUD get entity id happen exception", illegalAccessException);
        }
    }

    public static <RELATION_ENTITY> void insertIfNotExists(RELATION_ENTITY relation) {
        Class<RELATION_ENTITY> entityClass = (Class<RELATION_ENTITY>) relation.getClass();
        BaseMapper<RELATION_ENTITY> baseMapper = getMapper(entityClass);
        if (Objects.isNull(baseMapper)) {
            throw new RuntimeException(StrUtil.format("can not found mapper by class `{}`", entityClass));
        }
        Serializable id = getId(relation);
        RELATION_ENTITY queryEntity = (id instanceof IDs)
                ? queryByIds((IDs) id, entityClass)
                : baseMapper.selectOneById(getId(id));
        if (Objects.isNull(queryEntity)) {
            baseMapper.insert(relation);
        }

    }

    public static <ENTITY> ENTITY queryByIds(IDs ids, Class<ENTITY> entityClass) {
        BaseMapper<?> baseMapper = getMapper(entityClass);
        EntityMeta<ENTITY> entityMeta = getEntityMeta(entityClass);
        Map<Field, ColumnInfo> fieldColumnInfoMap = entityMeta.getFieldColumnInfoMap();
        Map<String, Object> idQuery = ids.getIds().stream().collect(Collectors.toMap(fieldValue -> {
            ColumnInfo columnInfo = fieldColumnInfoMap.get(fieldValue.getField());
            return columnInfo.getColumn();
        }, FieldValue::getValue));
        return (ENTITY) baseMapper.selectOneByMap(idQuery);
    }

    public static synchronized <ENTITY, SERVICE extends CrudService<ENTITY>> Class<SERVICE> makeServiceClass(
            Class<ENTITY> entityClass,
            Class<SERVICE> superServiceClass) {
        String entityClassName = entityClass.getName();
        String formatName = String.format("%sCrudService$$javassist", entityClassName);
        Class<?> serviceClass;
        ClassPool classPool = JavassistUtils.defaultPool();
        try {
            serviceClass = classPool.getClassLoader().loadClass(formatName);
        } catch (ClassNotFoundException e) {
            CtClass ctClass;
            try {
                ctClass = classPool.getCtClass(formatName);
                serviceClass = ctClass.getClass();
            } catch (NotFoundException notFoundCtClass) {
                try {
                    ctClass = classPool.makeClass(formatName, classPool.get(superServiceClass.getName()));
                    JavassistUtils.addTypeArgument(ctClass, superServiceClass, new Class[]{entityClass}, null, null);
                    serviceClass = ctClass.toClass(entityClass);
                } catch (Throwable throwable) {
                    throw new RuntimeException(
                            StrUtil.format("make class for entityType `{}` and serviceType `{}` happen error",
                                    entityClass,
                                    superServiceClass
                            )
                    );
                }
            }
        }
        return (Class<SERVICE>) serviceClass;
    }

}
