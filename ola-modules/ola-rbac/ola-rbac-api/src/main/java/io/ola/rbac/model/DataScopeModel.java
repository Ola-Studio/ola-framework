package io.ola.rbac.model;

import lombok.Data;

import java.util.Set;

/**
 * 数据范围模型
 *
 * @author yiuman
 * @date 2023/8/4
 */
@Data
public class DataScopeModel {
    /**
     * 数据的ID
     */
    private Set<String> dataIds;
    /**
     * 用户的ID
     */
    private Set<String> userIds;
    /**
     * 组织的ID
     */
    private Set<String> organIds;
}
