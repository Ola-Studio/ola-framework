package io.ola.crud.query;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.table.ColumnInfo;
import io.ola.crud.CRUD;
import io.ola.crud.model.EntityMeta;
import io.ola.crud.model.SortField;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 排序辅助工具类
 *
 * @author yiuman
 * @date 2026/3/26
 */
@Slf4j
public final class SortHelper {

    private SortHelper() {
    }

    /**
     * 解析排序参数字符串
     * 格式: field1,-field2,field3
     * -前缀表示降序,无前缀表示升序
     *
     * @param entityClass  实体类
     * @param sortParam 排序参数字符串
     * @return 排序字段列表
     */
    public static List<SortField> parseSort(Class<?> entityClass, String sortParam) {
        if (StrUtil.isBlank(sortParam)) {
            return new ArrayList<>();
        }

        EntityMeta<?> entityMeta = CRUD.getEntityMeta(entityClass);
        Map<String, com.mybatisflex.core.table.ColumnInfo> fieldNameColumnInfoMap = entityMeta.getFieldNameColumnInfoMap();

        List<SortField> sortFields = new ArrayList<>();
        String[] parts = sortParam.split(",");
        for (String part : parts) {
            String trimmed = part.trim();
            if (StrUtil.isBlank(trimmed)) {
                continue;
            }

            boolean descending = false;
            if (trimmed.startsWith("-")) {
                descending = true;
                trimmed = trimmed.substring(1).trim();
            }

            if (StrUtil.isBlank(trimmed)) {
                continue;
            }

            com.mybatisflex.core.table.ColumnInfo columnInfo = fieldNameColumnInfoMap.get(trimmed);
            if (columnInfo != null) {
                sortFields.add(new SortField(columnInfo.getColumn(), descending));
            }
        }
        return sortFields;
    }

    /**
     * 获取实体类上@Sort标注的字段作为默认排序
     *
     * @param entityClass 实体类
     * @return 排序字段列表
     */
    public static List<SortField> getDefaultSort(Class<?> entityClass) {
        List<SortField> sortFields = new ArrayList<>();
        java.lang.reflect.Field sortField = CRUD.getSortField(entityClass);

        if (Objects.isNull(sortField)) {
            return sortFields;
        }

        EntityMeta<?> entityMeta = CRUD.getEntityMeta(entityClass);
        ColumnInfo columnInfo = entityMeta.getFieldNameColumnInfoMap().get(sortField.getName());

        if (Objects.nonNull(columnInfo)) {
            sortFields.add(SortField.asc(columnInfo.getColumn()));
        }

        return sortFields;
    }

    /**
     * 将排序条件构建到QueryWrapper
     *
     * @param queryWrapper 查询包装器
     * @param sortFields   排序字段列表
     */
    public static void buildOrderBy(QueryWrapper queryWrapper, List<SortField> sortFields) {
        if (Objects.isNull(queryWrapper) || CollUtil.isEmpty(sortFields)) {
            return;
        }

        for (SortField sortField : sortFields) {
            queryWrapper.orderBy(sortField.column(), !sortField.descending());
        }
    }
}
