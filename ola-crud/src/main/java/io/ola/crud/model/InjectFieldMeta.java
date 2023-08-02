package io.ola.crud.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author yiuman
 * @date 2023/8/2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InjectFieldMeta {
    private Class<?> entityClass;
    private Field field;
    private Annotation annotation;
    private Class<?> injectorClass;
    private Boolean force;
}
