package io.ola.rbac.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import io.ola.crud.model.BaseAudit;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yiuman
 * @date 2023/8/4
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table("sys_user_role")
public class UserRole extends BaseAudit {
    @Id
    private String id;
    private String userId;
    private String roleId;

    public String getId() {
        return String.format("%s_%s", userId, roleId);
    }
}
