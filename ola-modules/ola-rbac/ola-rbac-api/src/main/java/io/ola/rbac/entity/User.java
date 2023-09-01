package io.ola.rbac.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import io.ola.crud.model.BaseAudit;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @author yiuman
 * @date 2023/8/3
 */
@EqualsAndHashCode(callSuper = true)
@Table("sys_user")
@Data
public class User extends BaseAudit {
    @Id
    private String id;
    @NotBlank
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String mobile;
    private String avatar;
    private Integer status = BigDecimal.ONE.intValue();
}
