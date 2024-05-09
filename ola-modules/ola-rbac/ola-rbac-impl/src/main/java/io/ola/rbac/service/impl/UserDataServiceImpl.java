package io.ola.rbac.service.impl;

import io.ola.crud.query.QueryHelper;
import io.ola.crud.service.impl.BaseCrudService;
import io.ola.crud.service.impl.BaseService;
import io.ola.rbac.entity.UserData;
import io.ola.rbac.query.UserDataQuery;
import io.ola.rbac.service.UserDataService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yiuman
 * @date 2023/9/4
 */
@Service
public class UserDataServiceImpl extends BaseService<UserData> implements UserDataService {

    @Override
    public List<UserData> findListByQuery(UserDataQuery userDataQuery) {
        return list(QueryHelper.build(userDataQuery));
    }
}
