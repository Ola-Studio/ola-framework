package io.ola.rbac.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import io.ola.crud.model.BaseAudit;
import io.ola.rbac.constants.TableNames;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yiuman
 * @date 2023/8/4
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table(TableNames.USER_ROLE)
public class UserRole extends BaseAudit {
    @Id
    private String userId;
    @Id
    private String roleId;
}
