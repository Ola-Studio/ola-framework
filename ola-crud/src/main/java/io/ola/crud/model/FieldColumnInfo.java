package io.ola.crud.model;

import com.mybatisflex.core.table.ColumnInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;

/**
 * @author yiuman
 * @date 2024/11/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldColumnInfo {
    private Field field;
    private ColumnInfo columnInfo;
}
