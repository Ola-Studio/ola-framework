package io.ola.rbac.runner;

import io.ola.rbac.entity.User;
import io.ola.rbac.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author yiuman
 * @date 2023/8/15
 */
@Component
@RequiredArgsConstructor
public class CreateAdminUserRunner implements CommandLineRunner {
    private final UserService userService;
    private static final String ADMIN_USERNAME = "admin";

    @Override
    public void run(String... args) {
        User admin = userService.findByUsername(ADMIN_USERNAME);
        if (Objects.isNull(admin)) {
            admin = new User();
            admin.setUsername(ADMIN_USERNAME);
            admin.setPassword("ola123456");
            userService.save(admin);
        }
    }
}
