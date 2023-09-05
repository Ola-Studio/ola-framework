package io.ola.rbac.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import io.ola.crud.model.BaseEntity;
import io.ola.rbac.enums.DataOperation;
import io.ola.rbac.enums.DataScopeMode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据范围
 *
 * @author yiuman
 * @date 2023/8/4
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Table("sys_data_scope")
public class DataScope extends BaseEntity<String> {
    @Id(keyType = KeyType.Generator, value = KeyGenerators.uuid)
    private String id;
    /**
     * 所属角色
     */
    private String roleId;
    /**
     * 资源ID
     */
    private String resourceId;
    /**
     * 数据范围模式
     */
    private DataScopeMode mode;
    private String organId;
    private DataOperation operation;
}
