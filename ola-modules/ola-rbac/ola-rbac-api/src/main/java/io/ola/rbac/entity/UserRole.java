package io.ola.rbac.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * @author yiuman
 * @date 2023/8/4
 */
@Data
@Table("sys_user_role")
public class UserRole {
    @Id
    private String userId;
    @Id
    private String roleId;
}
