package io.ola.crud;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import io.ola.common.utils.JavassistUtils;
import io.ola.common.utils.SpringUtils;
import io.ola.crud.annotation.BeforeSave;
import io.ola.crud.annotation.BeforeUpdate;
import io.ola.crud.annotation.DeleteTag;
import io.ola.crud.annotation.Sort;
import io.ola.crud.enums.DbType;
import io.ola.crud.groups.Modify;
import io.ola.crud.groups.Save;
import io.ola.crud.inject.ConditionInject;
import io.ola.crud.inject.ConditionInjector;
import io.ola.crud.inject.Injector;
import io.ola.crud.model.*;
import io.ola.crud.properties.CrudProperties;
import io.ola.crud.query.annotation.Query;
import io.ola.crud.rest.BaseQueryAPI;
import io.ola.crud.rest.BaseRESTAPI;
import io.ola.crud.service.CrudService;
import io.ola.crud.service.QueryService;
import io.ola.crud.service.impl.BaseCrudService;
import io.ola.crud.service.impl.BaseMongoService;
import jakarta.validation.groups.Default;
import javassist.CtClass;
import javassist.NotFoundException;
import org.apache.ibatis.type.JdbcType;
import org.springframework.core.ResolvableType;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.*;
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
    public static final Class<?>[] SAVE_GROUPS = new Class<?>[]{Default.class, Save.class};
    public static final Class<?>[] MODIFY_GROUPS = new Class<?>[]{Default.class, Modify.class};
    private static final String ENTITY_DB_TYPE_MAP = "ENTITY_DB_TYPE_MAP";

    private static final Map<Class<?>, CrudMeta<?>> CRUD_META_MAP = new ConcurrentHashMap<>();
    private static final Map<Class<?>, EntityMeta<?>> ENTITY_META_MAP = new ConcurrentHashMap<>();
    private static final CrudProperties CRUD_PROPERTIES = SpringUtils.getBean(CrudProperties.class);

    private static final ConcurrentHashMap<Class<?>, String> STANDARD_MAPPING;

    static {
        STANDARD_MAPPING = new ConcurrentHashMap<>();
        STANDARD_MAPPING.put(BigDecimal.class, JdbcType.NUMERIC.name());
        STANDARD_MAPPING.put(BigInteger.class, JdbcType.BIGINT.name());
        STANDARD_MAPPING.put(boolean.class, JdbcType.BOOLEAN.name());
        STANDARD_MAPPING.put(Boolean.class, JdbcType.BOOLEAN.name());
        STANDARD_MAPPING.put(byte[].class, JdbcType.VARBINARY.name());
        STANDARD_MAPPING.put(byte.class, JdbcType.TINYINT.name());
        STANDARD_MAPPING.put(Byte.class, JdbcType.TINYINT.name());
        STANDARD_MAPPING.put(Calendar.class, JdbcType.TIMESTAMP.name());
        STANDARD_MAPPING.put(java.sql.Date.class, JdbcType.DATE.name());
        STANDARD_MAPPING.put(java.util.Date.class, JdbcType.TIMESTAMP.name());
        STANDARD_MAPPING.put(double.class, JdbcType.DOUBLE.name());
        STANDARD_MAPPING.put(Double.class, JdbcType.DOUBLE.name());
        STANDARD_MAPPING.put(float.class, JdbcType.REAL.name());
        STANDARD_MAPPING.put(Float.class, JdbcType.REAL.name());
        STANDARD_MAPPING.put(int.class, JdbcType.INTEGER.name());
        STANDARD_MAPPING.put(Integer.class, JdbcType.INTEGER.name());
        STANDARD_MAPPING.put(LocalDate.class, JdbcType.DATE.name());
        STANDARD_MAPPING.put(LocalDateTime.class, JdbcType.TIMESTAMP.name());
        STANDARD_MAPPING.put(LocalTime.class, JdbcType.TIME.name());
        STANDARD_MAPPING.put(long.class, JdbcType.BIGINT.name());
        STANDARD_MAPPING.put(Long.class, JdbcType.BIGINT.name());
        STANDARD_MAPPING.put(OffsetDateTime.class, JdbcType.TIMESTAMP_WITH_TIMEZONE.name());
        STANDARD_MAPPING.put(OffsetTime.class, JdbcType.TIME_WITH_TIMEZONE.name());
        STANDARD_MAPPING.put(Short.class, JdbcType.SMALLINT.name());
        STANDARD_MAPPING.put(String.class, JdbcType.VARCHAR.name());
        STANDARD_MAPPING.put(Time.class, JdbcType.TIME.name());
        STANDARD_MAPPING.put(Timestamp.class, JdbcType.TIMESTAMP.name());
        STANDARD_MAPPING.put(URL.class, JdbcType.DATALINK.name());
    }

    public static String resolveJDBCType(Class<?> clazz) {
        return STANDARD_MAPPING.getOrDefault(clazz, JdbcType.VARCHAR.name());
    }


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
            EntityMeta<ENTITY> entityMeta = getEntityMeta((Class<ENTITY>) typeArgument);
            CrudService<ENTITY> crudService = getCrudService(entityClass, (Class<? extends CrudService<ENTITY>>)
                    (DbType.MONGODB == entityMeta.getDbType()
                            ? BaseMongoService.class
                            : BaseCrudService.class));
            QueryService<ENTITY> queryService = Optional
                    .ofNullable(getSelectService(entityClass))
                    .orElse(crudService);
            //条件注入器
            ConditionInject conditionInject = AnnotationUtil.getAnnotation(apiClass, ConditionInject.class);
            ConditionInjector conditionInjector = null;
            if (Objects.nonNull(conditionInject)) {
                conditionInjector = SpringUtils.getBean(ConditionInjector.class, true);
            }
            return new CrudMeta<>(apiClass, entityClass, crudService, queryService, entityMeta, queryClass, conditionInjector);
        }
    }

    public static <ENTITY> QueryService<ENTITY> getSelectService(Class<ENTITY> entityClass) {
        TypeReference<QueryService<ENTITY>> queryReference = new TypeReference<>() {
        };
        final ParameterizedType parameterizedType = (ParameterizedType) queryReference.getType();
        final Class<QueryService<ENTITY>> rawType = (Class<QueryService<ENTITY>>) parameterizedType.getRawType();
        final Class<?>[] genericTypes = new Class[]{entityClass};
        final String[] beanNames = SpringUtils.getBeanFactory().getBeanNamesForType(ResolvableType.forClassWithGenerics(rawType, genericTypes));
        if (ArrayUtil.isNotEmpty(beanNames)) {
            return SpringUtil.getBean(beanNames[0], rawType);
        }
        return null;
    }

    public static <ENTITY> CrudService<ENTITY> getCrudService(Class<ENTITY> entityClass) {
        return getCrudService(entityClass, BaseCrudService.class);
    }

    public static <ENTITY, SERVICE extends CrudService<ENTITY>> SERVICE getProxyCrudService(Class<ENTITY> entityClass) {
        EntityMeta<ENTITY> entityMeta = getEntityMeta(entityClass);
        Class<? extends CrudService<ENTITY>> serviceClass =
                (Class<? extends CrudService<ENTITY>>)
                        (DbType.MONGODB == entityMeta.getDbType()
                                ? BaseMongoService.class
                                : BaseCrudService.class);
        return (SERVICE) getProxyCrudService(entityClass, serviceClass);
    }

    public static <ENTITY, SERVICE extends CrudService<ENTITY>> SERVICE getProxyCrudService(Class<ENTITY> entityClass, Class<SERVICE> serviceClass) {
        Class<? extends CrudService<ENTITY>> makeServiceClazz = makeServiceClass(entityClass, serviceClass);
        SERVICE contextBean = (SERVICE) getContextCrudBean(makeServiceClazz);
        if (Objects.nonNull(contextBean)) {
            return contextBean;
        }

        return (SERVICE) SpringUtils.getBean(makeServiceClazz, true);
    }

    public static <ENTITY, SERVICE extends CrudService<ENTITY>> SERVICE getCrudService(Class<ENTITY> entityClass, Class<SERVICE> serviceClass) {
        SERVICE contextBean = (SERVICE) getContextCrudBean(entityClass);
        if (Objects.nonNull(contextBean)) {
            return contextBean;
        }
        Class<? extends CrudService<ENTITY>> makeService = makeServiceClass(entityClass, serviceClass);
        return (SERVICE) SpringUtils.getBean(makeService, true);
    }

    public static <ENTITY> CrudService<ENTITY> getContextCrudBean(Class<ENTITY> entityClass) {
        TypeReference<CrudService<ENTITY>> queryReference = new TypeReference<>() {
        };
        final ParameterizedType parameterizedType = (ParameterizedType) queryReference.getType();
        final Class<CrudService<ENTITY>> rawType = (Class<CrudService<ENTITY>>) parameterizedType.getRawType();
        final Class<?>[] genericTypes = new Class[]{entityClass};
        final String[] beanNames = SpringUtils.getBeanFactory()
                .getBeanNamesForType(ResolvableType.forClassWithGenerics(rawType, genericTypes));
        if (ArrayUtil.isNotEmpty(beanNames)) {
            return SpringUtils.getBean(beanNames[0], rawType);
        }
        return null;
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
        entityMeta.setAllFields(Arrays.stream(ReflectUtil.getFields(entityClass)).toList());
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(entityClass);
        if (Objects.nonNull(tableInfo)) {
            List<Field> idFields = tableInfo.getPrimaryKeyList().stream()
                    .map(idInfo -> ReflectUtil.getField(entityClass, idInfo.getProperty()))
                    .collect(Collectors.toList());
            entityMeta.setIdFields(idFields);

        }
        EntityDbTypeMap entityDbTypeMap;
        try {
            entityDbTypeMap = SpringUtils.getBean(ENTITY_DB_TYPE_MAP, EntityDbTypeMap.class);
        } catch (Throwable ignore) {
            entityDbTypeMap = new EntityDbTypeMap();
        }

        entityMeta.setDeleteTagField(getDeleteTagField(entityClass));
        entityMeta.setSortTagField(getSortField(entityClass));
        entityMeta.setDbType(Optional.ofNullable(entityDbTypeMap.get(entityClass)).orElse(CRUD_PROPERTIES.getDefaultDbType()));
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
            return (ID) ReflectUtil.getFieldValue(entity, CollUtil.getFirst(idFields));
        }

    }

    public static synchronized <ENTITY, SERVICE extends CrudService<ENTITY>> Class<SERVICE> makeServiceClass(
            Class<ENTITY> entityClass,
            Class<SERVICE> superServiceClass) {
        String entityClassName = entityClass.getName();
        String formatName = String.format("%sCrudService$$javassist", entityClassName);
        Class<?> serviceClass;
        try {
            serviceClass = JavassistUtils.defaultPool().getClassLoader().loadClass(formatName);
        } catch (ClassNotFoundException e) {
            CtClass ctClass;
            try {
                ctClass = JavassistUtils.defaultPool().getCtClass(formatName);
                serviceClass = ctClass.getClass();
            } catch (NotFoundException notFoundCtClass) {
                try {
                    ctClass = JavassistUtils.defaultPool().makeClass(formatName, JavassistUtils.getClass(superServiceClass));
                    JavassistUtils.addTypeArgument(ctClass, superServiceClass, new Class[]{entityClass}, null, null);
                    serviceClass = ctClass.toClass();
                } catch (Throwable throwable) {
                    throw new RuntimeException(
                            StrUtil.format("make class for entityType `{}` and serviceType `{}` happen error",
                                    entityClass,
                                    superServiceClass
                            ),
                            throwable
                    );
                }
            }
        }
        return (Class<SERVICE>) serviceClass;

    }

}
