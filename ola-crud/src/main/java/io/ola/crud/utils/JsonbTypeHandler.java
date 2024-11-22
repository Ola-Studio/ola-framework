package io.ola.crud.utils;

import com.mybatisflex.core.handler.JacksonTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author yiuman
 * @date 2024/11/22
 */
@MappedTypes({Object.class})
public class JsonbTypeHandler extends JacksonTypeHandler {
    public JsonbTypeHandler(Class<?> propertyType) {
        super(propertyType);
    }

    public JsonbTypeHandler(Class<?> propertyType, Class<?> genericType) {
        super(propertyType, genericType);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        if (ps != null) {
            PGobject jsonObject = new PGobject();
            jsonObject.setType("jsonb");
            jsonObject.setValue(toJson(parameter));
            ps.setObject(i, jsonObject);
        }
    }
}
