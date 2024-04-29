package io.ola.crud.model;

import io.ola.crud.enums.DbType;
import lombok.Setter;

import java.util.HashMap;

/**
 * @author yiuman
 * @date 2024/4/29
 */
public class EntityDbTypeMap extends HashMap<Class<?>, DbType> {

    @Setter
    private DbType defaultDbType = DbType.MYSQL;

    public DbType getDefault() {
        return defaultDbType;
    }
}
