package io.ola.crud.utils;

import cn.hutool.core.collection.CollUtil;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;

import java.sql.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author yiuman
 * @date 2024/11/22
 */
public class ListTypeHandler extends org.apache.ibatis.type.ArrayTypeHandler {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        if (parameter instanceof Array) {
            // it's the user's responsibility to properly free() the Array instance
            ps.setArray(i, (Array) parameter);
        } else if (parameter instanceof Collection<?> collection) {
            Object first = CollUtil.getFirst(collection);
            String arrayTypeName = resolveTypeName(first.getClass());
            Array array = ps.getConnection().createArrayOf(arrayTypeName, collection.toArray());
            ps.setArray(i, array);
            array.free();
        } else {
            if (!parameter.getClass().isArray()) {
                throw new TypeException(
                        "ArrayType Handler requires SQL array or java array parameter and does not support type "
                                + parameter.getClass());
            }
            Class<?> componentType = parameter.getClass().getComponentType();
            String arrayTypeName = resolveTypeName(componentType);
            Array array = ps.getConnection().createArrayOf(arrayTypeName, (Object[]) parameter);
            ps.setArray(i, array);
            array.free();
        }
    }

    @Override
    public List<Object> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toList(rs.getArray(columnIndex));
    }

    @Override
    public List<Object> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toList(rs.getArray(columnName));
    }

    @Override
    public List<Object> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toList(cs.getArray(columnIndex));
    }

    private List<Object> toList(Array array) {
        if (array == null) {
            return null;
        }
        try {
            return Arrays.asList((Object[]) array.getArray());
        } catch (Exception ignore) {
        }
        return null;
    }
}
