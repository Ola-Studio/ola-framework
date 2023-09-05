package io.ola.rbac.service;

import io.ola.crud.service.CrudService;
import io.ola.rbac.entity.UserData;
import io.ola.rbac.query.UserDataQuery;

import java.util.List;

/**
 * @author yiuman
 * @date 2023/9/4
 */
public interface UserDataService extends CrudService<UserData> {
    default List<UserData> findListByUserIdAndCode(String userId, String code) {
        return findListByQuery(UserDataQuery.builder().userId(userId).code(code).build());
    }

    List<UserData> findListByQuery(UserDataQuery userDataQuery);
}