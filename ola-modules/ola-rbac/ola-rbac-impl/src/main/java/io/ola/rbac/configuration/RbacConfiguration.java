package io.ola.rbac.configuration;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author yiuman
 * @date 2024/5/9
 */
@Configuration
@MapperScan(basePackages = "io.ola.rbac.mapper")
public class RbacConfiguration {
}
