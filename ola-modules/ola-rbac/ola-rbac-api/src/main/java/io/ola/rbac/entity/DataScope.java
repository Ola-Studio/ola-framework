package io.ola.rbac.entity;

import com.mybatisflex.annotation.Table;
import io.ola.crud.model.BaseEntity;
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

}
