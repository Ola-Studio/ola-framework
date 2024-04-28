package io.ola.rbac.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import io.ola.crud.query.QueryHelper;
import io.ola.crud.service.impl.BaseCrudService;
import io.ola.rbac.entity.*;
import io.ola.rbac.enums.DataOperation;
import io.ola.rbac.model.DataScopeModel;
import io.ola.rbac.query.DataScopeQuery;
import io.ola.rbac.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author yiuman
 * @date 2023/9/4
 */
@RequiredArgsConstructor
@Service
public class DataScopeServiceImpl extends BaseCrudService<DataScope> implements DataScopeService {
    private final UserService userService;
    private final UserDataService userDataService;
    private final ResourceService resourceService;

    @Override
    public DataScopeModel getReadDataScope(String resourceId, String userId) {
        User user = userService.get(userId);
        Resource resource = resourceService.get(resourceId);
        return buildDataScopeModel(user, resource, DataOperation.READ);
    }


    @Override
    public DataScopeModel getWriteDataScope(String resourceId, String userId) {
        User user = userService.get(userId);
        Resource resource = resourceService.get(resourceId);
        return buildDataScopeModel(user, resource, DataOperation.WRITE);
    }

    private DataScopeModel buildDataScopeModel(User user, Resource resource, DataOperation dataOperation) {
        String userId = user.getId();
        List<Organ> organs = user.getOrgans();
        DataScopeQuery dataScopeQuery = DataScopeQuery.builder()
                .userId(userId)
                .resourceId(resource.getId())
                .operation(dataOperation)
                .build();
        List<DataScope> dataScopes = findListByQuery(dataScopeQuery);
        DataScopeModel dataScopeModel = new DataScopeModel();
        Set<String> dataIds = null;
        Set<String> organIds = new HashSet<>();
        for (DataScope dataScope : dataScopes) {
            switch (dataScope.getMode()) {
                case SELF -> {
                    List<UserData> userDataList = userDataService.findListByUserIdAndCode(userId, resource.getCode());
                    dataIds = userDataList.stream().map(UserData::getDataId).collect(Collectors.toSet());
                }
                case DEPT -> organIds.addAll(organs.stream().map(Organ::getId).collect(Collectors.toSet()));
                case SUPERIOR -> organIds.addAll(OrganService.fetchAllSubOrganIds(organs));
                case SUBORDINATE -> organIds.addAll(OrganService.fetchAllSupOrganIds(organs));
                default -> {
                }
            }
        }

        dataScopeModel.setDataIds(dataIds);
        dataScopeModel.setUserIds(CollUtil.newHashSet(userId));
        dataScopeModel.setOrganIds(organIds);
        return dataScopeModel;
    }

    @Override
    public List<DataScope> findListByQuery(DataScopeQuery dataScopeQuery) {
        if (StrUtil.isNotBlank(dataScopeQuery.getUserId())
                && CollUtil.isEmpty(dataScopeQuery.getRoleIds())) {
            User user = userService.get(dataScopeQuery.getUserId());
            dataScopeQuery.setRoleIds(user.getRoles().stream().map(Role::getId).collect(Collectors.toSet()));

        }
        return list(QueryHelper.build(dataScopeQuery));
    }

}
