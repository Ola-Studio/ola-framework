package io.ola.rbac.enums;

/**
 * @author yiuman
 * @date 2023/8/4
 */
public enum DataScopeMode {
    /**
     * 自己/经办的
     * 注：数据有creator等于当前用户ID的 或 user_data表中有此用户资源数据的
     */
    SELF,
    /**
     * 当前部门
     * 注：数据中有部门ID
     */
    DEPT,
    /**
     * 当前部门父级机构
     * 注：数据中有父级部门ID的
     */
    SUPERIOR,
    /**
     * 当前部门子机构
     * 注：数据中有子部门ID的
     */
    SUBORDINATE
}
