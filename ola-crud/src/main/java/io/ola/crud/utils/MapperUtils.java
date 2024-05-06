package io.ola.crud.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.table.ColumnInfo;
import io.ola.common.utils.SpringUtils;
import io.ola.crud.CRUD;
import io.ola.crud.model.EntityMeta;
import io.ola.crud.model.FieldValue;
import io.ola.crud.model.IDs;
import org.mybatis.spring.SqlSessionTemplate;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author yiuman
 * @date 2024/5/6
 */
@SuppressWarnings("unchecked")
public final class MapperUtils {

    private static final Map<Class<?>, BaseMapper<?>> ENTITY_CLASS_MAPPER_MAP = new ConcurrentHashMap<>();

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

    public static <ENTITY> ENTITY queryByIds(IDs ids, Class<ENTITY> entityClass) {
        BaseMapper<?> baseMapper = getMapper(entityClass);
        EntityMeta<ENTITY> entityMeta = CRUD.getEntityMeta(entityClass);
        Map<Field, ColumnInfo> fieldColumnInfoMap = entityMeta.getFieldColumnInfoMap();
        Map<String, Object> idQuery = ids.getIds().stream().collect(Collectors.toMap(fieldValue -> {
            ColumnInfo columnInfo = fieldColumnInfoMap.get(fieldValue.getField());
            return columnInfo.getColumn();
        }, FieldValue::getValue));
        return (ENTITY) baseMapper.selectOneByMap(idQuery);
    }

    public static <RELATION_ENTITY> void insertIfNotExists(RELATION_ENTITY relation) {
        Class<RELATION_ENTITY> entityClass = (Class<RELATION_ENTITY>) relation.getClass();
        BaseMapper<RELATION_ENTITY> baseMapper = getMapper(entityClass);
        if (Objects.isNull(baseMapper)) {
            throw new RuntimeException(StrUtil.format("can not found mapper by class `{}`", entityClass));
        }
        Serializable id = CRUD.getId(relation);
        RELATION_ENTITY queryEntity = (id instanceof IDs)
                ? queryByIds((IDs) id, entityClass)
                : baseMapper.selectOneById(CRUD.getId(id));
        if (Objects.isNull(queryEntity)) {
            baseMapper.insert(relation);
        }

    }
}
