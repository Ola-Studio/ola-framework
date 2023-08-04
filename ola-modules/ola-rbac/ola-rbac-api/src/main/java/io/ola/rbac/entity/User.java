package io.ola.rbac.entity;

import com.mybatisflex.annotation.Table;
import io.ola.crud.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yiuman
 * @date 2023/8/3
 */
@EqualsAndHashCode(callSuper = true)
@Table("sys_user")
@Data
public class User extends BaseEntity<String> {
    private String username;
    private String password;
    private String mobile;
    private String avatar;
    private Integer status;
}
