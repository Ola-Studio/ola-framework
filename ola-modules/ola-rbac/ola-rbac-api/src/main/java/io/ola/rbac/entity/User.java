package io.ola.rbac.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.RelationManyToMany;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import io.ola.crud.annotation.Sensitive;
import io.ola.crud.enums.SensitiveStrategy;
import io.ola.crud.model.BaseEntity;
import io.ola.rbac.constants.TableNames;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author yiuman
 * @date 2023/8/3
 */
@EqualsAndHashCode(callSuper = true)
@Table("sys_user")
@Data
public class User extends BaseEntity<String> {
    @Id(keyType = KeyType.Generator, value = KeyGenerators.uuid)
    private String id;
    @NotBlank
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @Sensitive(strategy = SensitiveStrategy.PHONE)
    private String mobile;
    private String avatar;
    private Integer status = BigDecimal.ONE.intValue();

    @RelationManyToMany(
            joinTable = TableNames.USER_ROLE,
            joinSelfColumn = "user_id",
            joinTargetColumn = "role_id"
    )
    private List<Role> roles;

    @RelationManyToMany(
            joinTable = TableNames.USER_ORGAN,
            joinSelfColumn = "user_id",
            joinTargetColumn = "organ_id"
    )
    private List<Organ> organs;
}
