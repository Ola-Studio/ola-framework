package io.ola.rbac.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import io.ola.crud.query.QueryHelper;
import io.ola.crud.service.impl.BaseService;
import io.ola.rbac.entity.Resource;
import io.ola.rbac.entity.ResourceOwner;
import io.ola.rbac.query.ResourceQuery;
import io.ola.rbac.service.ResourceOwnerService;
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
public class ResourceServiceImpl extends BaseService<Resource> implements ResourceService {

    private final ResourceOwnerService resourceOwnerCrudService;

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
            List<ResourceOwner> resourceOwners = resourceOwnerCrudService.findListByUserId(resourceQuery.getUserId());
            Set<String> resourceIds = resourceOwners.stream().map(ResourceOwner::getResourceId).collect(Collectors.toSet());
            resourceQuery.setIds(resourceIds);
        }
    }
}
