package io.ola.crud.model;

/**
 * 排序字段元数据
 *
 * @param column    列名
 * @param descending 是否降序
 * @author yiuman
 * @date 2026/3/26
 */
public record SortField(String column, boolean descending) {

    /**
     * 创建升序排序字段
     *
     * @param column 列名
     * @return 升序SortField
     */
    public static SortField asc(String column) {
        return new SortField(column, false);
    }

    /**
     * 创建降序排序字段
     *
     * @param column 列名
     * @return 降序SortField
     */
    public static SortField desc(String column) {
        return new SortField(column, true);
    }
}
