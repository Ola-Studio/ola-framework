package io.ola.crud.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 实体元信息
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
}
