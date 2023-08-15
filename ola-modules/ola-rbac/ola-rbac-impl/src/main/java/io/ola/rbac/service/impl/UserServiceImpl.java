package io.ola.rbac.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import io.ola.crud.service.impl.BaseCRUDService;
import io.ola.rbac.entity.User;
import io.ola.rbac.entity.table.Tables;
import io.ola.rbac.service.PasswordEncoder;
import io.ola.rbac.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author yiuman
 * @date 2023/8/15
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends BaseCRUDService<User> implements UserService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public <T extends User> void beforeSave(T entity) {
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        super.beforeSave(entity);
    }

    @Override
    public User findByUsername(String username) {
        return get(QueryWrapper.create().where(Tables.USER.USERNAME.eq(username)));
    }

    @Override
    public User findByMobile(String mobile) {
        return get(QueryWrapper.create().where(Tables.USER.MOBILE.eq(mobile)));
    }
}
