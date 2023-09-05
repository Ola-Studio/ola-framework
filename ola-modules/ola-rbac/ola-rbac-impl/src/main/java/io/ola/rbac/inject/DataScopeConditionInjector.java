package io.ola.rbac.inject;

import cn.hutool.core.collection.CollUtil;
import com.mybatisflex.core.constant.SqlConsts;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.table.ColumnInfo;
import io.ola.crud.CRUD;
import io.ola.crud.inject.ConditionInjector;
import io.ola.crud.model.EntityMeta;
import io.ola.rbac.mode.UserOnlineInfo;
import io.ola.rbac.model.DataScopeModel;
import io.ola.rbac.service.impl.DataScopeServiceImpl;
import io.ola.rbac.utils.RbacUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author yiuman
 * @date 2023/9/5
 */
@RequiredArgsConstructor
@Component
public class DataScopeConditionInjector implements ConditionInjector {

    private final DataScopeServiceImpl dataScopeService;

    @Override
    public void inject(QueryWrapper queryWrapper, Class<?> entityClass) {
        UserOnlineInfo userOnlineInfo = RbacUtils.getUserOnlineInfo();
        DataScopeModel readDataScope = dataScopeService.getReadDataScope(
                userOnlineInfo.getUser().getId(),
                userOnlineInfo.getCurrentResource().getId()
        );
        EntityMeta<?> entityMeta = CRUD.getEntityMeta(entityClass);
        Map<Field, ColumnInfo> fieldColumnInfoMap = entityMeta.getFieldColumnInfoMap();
        Consumer<QueryWrapper> queryWrapperConsumer = wrapper -> {
            //数据ID
            addCondition(
                    queryWrapper,
                    fieldColumnInfoMap.get(CollUtil.getFirst(entityMeta.getIdFields())),
                    readDataScope.getUserIds()
            );
            //用户ID
            addCondition(
                    queryWrapper,
                    fieldColumnInfoMap.get(entityMeta.getCreatorField()),
                    readDataScope.getUserIds()
            );
            //机构ID
            addCondition(
                    queryWrapper,
                    fieldColumnInfoMap.get(entityMeta.getOrgField()),
                    readDataScope.getOrganIds()
            );
        };
        queryWrapper.and(queryWrapperConsumer);
    }

    private void addCondition(QueryWrapper queryWrapper, ColumnInfo columnInfo, Collection<String> ids) {
        if (Objects.isNull(queryWrapper) || Objects.isNull(columnInfo) || Objects.isNull(ids)) {
            return;
        }
        QueryCondition queryCondition = new QueryCondition();
        queryCondition.setColumn(new QueryColumn(columnInfo.getColumn()));
        queryCondition.setLogic(SqlConsts.IN);
        queryCondition.setValue(ids);
        queryWrapper.or(queryCondition);
    }
}
