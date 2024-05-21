package io.ola.crud.model;

import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryColumn;
import io.ola.crud.enums.Clauses;
import io.ola.crud.query.ConditionHandler;
import io.ola.crud.query.annotation.QueryField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

/**
 * 查询字段你的元数据
 *
 * @author yiuman
 * @date 2023/7/26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryFieldMeta {
    /**
     * 标记注解的类型
     */
    private Class<?> objectClass;
    /**
     * 标记注册的字段
     */
    private Field field;
    /**
     * 标记的查询注解
     */
    private Annotation annotation;
    /**
     * 查询方法：eq、gt、lt等等
     */
    private String method;
    /**
     * 查询子句  and 、or
     */
    private Clauses clauses;
    /**
     * 值为空时，条件是否必须
     */
    private Boolean require;
    /**
     * 字段映射
     */
    private String mapping;
    /**
     * 嵌套
     */
    private List<QueryFieldMeta> nested;

    private Class<? extends ConditionHandler> handleClass;

    private QueryColumn queryColumn;

    public QueryFieldMeta(Class<?> objectClass, Field field, QueryField queryField, Annotation sourceAnnotation) {
        this.objectClass = objectClass;
        this.field = field;
        this.annotation = sourceAnnotation;
        this.method = queryField.method();
        this.clauses = queryField.clauses();
        this.require = queryField.require();
        this.mapping = queryField.mapping();


        if (StrUtil.isBlank(mapping)) {
            this.mapping = field.getName();
        }
        this.queryColumn = new QueryColumn(this.mapping);
        if (!ConditionHandler.class.equals(queryField.handler())) {
            this.handleClass = queryField.handler();
        }

    }
}
