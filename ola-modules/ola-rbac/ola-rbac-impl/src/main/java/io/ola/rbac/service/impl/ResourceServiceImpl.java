package io.ola.rbac.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryChain;
import io.ola.crud.query.QueryHelper;
import io.ola.crud.service.impl.BaseCrudService;
import io.ola.rbac.entity.Resource;
import io.ola.rbac.entity.ResourceOwner;
import io.ola.rbac.entity.table.Tables;
import io.ola.rbac.enums.OwnerType;
import io.ola.rbac.mapper.ResourceOwnerMapper;
import io.ola.rbac.query.ResourceQuery;
import io.ola.rbac.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author yiuman
 * @date 2023/9/5
 */
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl extends BaseCrudService<Resource> implements ResourceService {
    private final ResourceOwnerMapper resourceOwnerMapper;

    @Override
    public List<Resource> findListByQuery(ResourceQuery resourceQuery) {
        handleResourceQuery(resourceQuery);
        return list(QueryHelper.build(resourceQuery));
    }

    @Override
    public Resource findOneByQuery(ResourceQuery resourceQuery) {
        handleResourceQuery(resourceQuery);
        return get(QueryHelper.build(resourceQuery));
    }

    private void handleResourceQuery(ResourceQuery resourceQuery) {
        if (CollUtil.isEmpty(resourceQuery.getIds())
                && StrUtil.isNotBlank(resourceQuery.getUserId())) {
            List<ResourceOwner> list = QueryChain.of(resourceOwnerMapper)
                    .select(Tables.RESOURCE_OWNER.ALL_COLUMNS)
                    .from(Tables.RESOURCE_OWNER)
                    .where(Tables.RESOURCE_OWNER.OWNER_ID.eq(resourceQuery.getUserId()))
                    .and(Tables.RESOURCE_OWNER.OWNER_TYPE.eq(OwnerType.USER))
                    .list();
            Set<String> resourceIds = list.stream().map(ResourceOwner::getResourceId).collect(Collectors.toSet());
            resourceQuery.setIds(resourceIds);
        }
    }
}
