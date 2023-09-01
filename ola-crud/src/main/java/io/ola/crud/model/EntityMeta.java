package io.ola.crud.model;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ReflectUtil;
import com.mybatisflex.core.table.ColumnInfo;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 实体元信息
 *
 * @param <ENTITY> 实体类型
 * @author yiuman
 * @date 2023/8/2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityMeta<ENTITY> {
    private Class<ENTITY> entityClass;
    private List<Field> idFields;
    private List<InjectFieldMeta> beforeSaveInjectMetas;
    private List<InjectFieldMeta> beforeUpdateInjectMetas;
    private Field deleteTagField;
    private Field sortTagField;
    private Map<Field, ColumnInfo> fieldColumnInfoMap;
    private Map<String, ColumnInfo> fieldNameColumnInfoMap;

    public TableInfo getTableInfo() {
        return TableInfoFactory.ofEntityClass(entityClass);
    }

    public Map<Field, ColumnInfo> getFieldColumnInfoMap() {
        if (MapUtil.isEmpty(fieldColumnInfoMap)) {
            fieldColumnInfoMap = getTableInfo().getColumnInfoList()
                    .stream().collect(Collectors.toMap(columnInfo -> {
                        String property = columnInfo.getProperty();
                        Field field = ReflectUtil.getField(entityClass, property);
                        field.setAccessible(true);
                        return field;
                    }, columnInfo -> columnInfo));
        }
        return fieldColumnInfoMap;
    }

    public Map<String, ColumnInfo> getFieldNameColumnInfoMap() {
        if (MapUtil.isEmpty(fieldNameColumnInfoMap)) {
            fieldNameColumnInfoMap = getTableInfo().getColumnInfoList()
                    .stream().collect(Collectors.toMap(ColumnInfo::getProperty, columnInfo -> columnInfo));
        }
        return fieldNameColumnInfoMap;
    }
}
