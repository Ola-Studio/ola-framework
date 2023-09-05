package io.ola.rbac.inject;

import io.ola.crud.inject.UserIdInjector;
import io.ola.rbac.entity.User;
import io.ola.rbac.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * @author yiuman
 * @date 2023/8/30
 */
@Component
@RequiredArgsConstructor
public class UserIdInjectorImpl implements UserIdInjector {

    private final UserService userService;

    @Override
    public Object getInjectValue(Field field) {
        User currentUser = userService.getCurrentUser();
        if (Objects.isNull(currentUser)) {
            return null;
        }
        return currentUser.getId();
    }
}
