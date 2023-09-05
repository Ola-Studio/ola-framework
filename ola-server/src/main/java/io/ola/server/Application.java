package io.ola.server;

import com.mybatisflex.core.audit.AuditManager;
import io.ola.common.constants.OLA;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


/**
 * @author yiuman
 * @date 2023/8/2
 */
@SpringBootApplication
@ComponentScan(basePackages = OLA.BASE_PACKAGE)
@MapperScan(basePackages = "io.ola.rbac.mapper")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Configuration
    @Slf4j
    public static class MyBatisFlexConfiguration {
        public MyBatisFlexConfiguration() {
            //开启审计功能
            AuditManager.setAuditEnable(true);
            //设置 SQL 审计收集器
            AuditManager.setMessageCollector(auditMessage ->
                    log.info("{},{}ms", auditMessage.getFullSql(), auditMessage.getElapsedTime())
            );
        }
    }
}
