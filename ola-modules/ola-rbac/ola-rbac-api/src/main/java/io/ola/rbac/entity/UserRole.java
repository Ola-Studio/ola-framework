package io.ola.rbac.entity;

import com.mybatisflex.annotation.Table;
import io.ola.crud.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yiuman
 * @date 2023/8/4
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table("sys_user_role")
public class UserRole extends BaseEntity<String> {
  private String userId;
  private String roleId;
}
