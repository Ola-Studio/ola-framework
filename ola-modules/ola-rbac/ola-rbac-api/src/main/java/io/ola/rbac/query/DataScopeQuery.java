package io.ola.rbac.query;

import io.ola.crud.query.annotation.Equals;
import io.ola.crud.query.annotation.In;
import io.ola.rbac.enums.DataOperation;
import lombok.Builder;
import lombok.Data;

import java.util.Collection;

/**
 * @author yiuman
 * @date 2023/9/5
 */
@Data
@Builder
public class DataScopeQuery {
    @Equals
    private String resourceId;
    private String userId;
    @Equals
    private DataOperation operation;
    @In(mapping = "roleId")
    private Collection<String> roleIds;
}
