package io.ola.crud.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;

/**
 * @author yiuman
 * @date 2023/8/31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldValue {
    private Field field;
    private Object value;
}
