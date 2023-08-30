package io.ola.rbac.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import io.ola.crud.model.BaseAudit;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 资源拥有者
 *
 * @author yiuman
 * @date 2023/8/4
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table("sys_resource_owner")
public class ResourceOwner extends BaseAudit {
    @Id
    private String id;
    /**
     * 拥有者ID
     */
    private String objectId;
    /**
     * 资源ID
     */
    private String resourceId;
    /**
     * 拥有者类型（可能是用户、角色）
     */
    private String ownerType;
}
