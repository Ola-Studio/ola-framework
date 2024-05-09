package io.ola.rbac.configuration;

import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author yiuman
 * @date 2024/5/9
 */
@Configuration
@ConditionalOnClass(SqlSessionTemplate.class)
@ConditionalOnBean(DataSource.class)
@MapperScan(basePackages = "io.ola.rbac.mapper")
public class RbacConfiguration {
}
