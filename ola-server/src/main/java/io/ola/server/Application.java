package io.ola.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import static io.ola.server.Application.BASE_PACKAGE;

/**
 * @author yiuman
 * @date 2023/8/2
 */
@SpringBootApplication
@ComponentScan(basePackages = BASE_PACKAGE)
public class Application {
    public static final String BASE_PACKAGE = "io.ola";

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
