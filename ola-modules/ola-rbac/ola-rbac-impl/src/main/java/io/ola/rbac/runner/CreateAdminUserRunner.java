package io.ola.rbac.runner;

import io.ola.rbac.entity.Role;
import io.ola.rbac.entity.User;
import io.ola.rbac.service.RoleService;
import io.ola.rbac.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * @author yiuman
 * @date 2023/8/15
 */
@Component
@RequiredArgsConstructor
public class CreateAdminUserRunner implements CommandLineRunner {
    private final UserService userService;
    private final RoleService roleService;
    private static final String ADMIN = "admin";

    @Transactional
    @Override
    public void run(String... args) {
        User admin = userService.findByUsername(ADMIN);
        if (Objects.isNull(admin)) {
            admin = new User();
            admin.setUsername(ADMIN);
            admin.setPassword("ola123456");
            userService.save(admin);
        }

        Role adminRole = new Role();
        adminRole.setId(ADMIN);
        adminRole.setName("超级管理员");
        roleService.save(adminRole);
        roleService.bindUserRole(admin, adminRole);

    }

}
