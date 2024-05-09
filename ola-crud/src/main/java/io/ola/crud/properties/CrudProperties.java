package io.ola.crud.properties;

import io.ola.crud.enums.DbType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author yiuman
 * @date 2024/5/9
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ola.crud")
public class CrudProperties {
    private DbType defaultDbType = DbType.MYSQL;
}
