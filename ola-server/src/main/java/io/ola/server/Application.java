package io.ola.server;

import io.ola.common.constants.OLA;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


/**
 * @author yiuman
 * @date 2023/8/2
 */
@SpringBootApplication
@ComponentScan(basePackages = OLA.BASE_PACKAGE)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
