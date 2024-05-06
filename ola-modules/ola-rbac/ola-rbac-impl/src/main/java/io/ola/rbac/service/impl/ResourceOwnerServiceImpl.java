package io.ola.rbac.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import io.ola.crud.service.impl.BaseService;
import io.ola.rbac.entity.ResourceOwner;
import io.ola.rbac.entity.table.Tables;
import io.ola.rbac.enums.OwnerType;
import io.ola.rbac.service.ResourceOwnerService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yiuman
 * @date 2024/5/6
 */
@Service
public class ResourceOwnerServiceImpl extends BaseService<ResourceOwner> implements ResourceOwnerService {

    @Override
    public List<ResourceOwner> findListByUserId(String userId) {
        return list(
                QueryWrapper.create()
                        .where(Tables.RESOURCE_OWNER.OWNER_ID.eq(userId))
                        .and(Tables.RESOURCE_OWNER.OWNER_TYPE.eq(OwnerType.USER))
        );

    }
}
