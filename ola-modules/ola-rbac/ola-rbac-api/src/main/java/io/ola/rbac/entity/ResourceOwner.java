package io.ola.rbac.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import io.ola.rbac.constants.TableNames;
import io.ola.rbac.enums.OwnerType;
import lombok.Data;

/**
 * 资源拥有者
 *
 * @author yiuman
 * @date 2023/8/4
 */
@Data
@Table(TableNames.RESOURCE_OWNER)
public class ResourceOwner {
    /**
     * 拥有者ID
     */
    @Id
    private String ownerId;

    /**
     * 资源ID
     */
    @Id
    private String resourceId;
    /**
     * 拥有者类型（可能是用户、角色）
     */
    private OwnerType ownerType;
}
