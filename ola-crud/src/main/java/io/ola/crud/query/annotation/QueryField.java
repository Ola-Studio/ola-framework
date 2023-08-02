package io.ola.crud.query.annotation;

import com.mybatisflex.core.constant.SqlConsts;
import io.ola.crud.enums.Clauses;
import io.ola.crud.query.ConditionHandler;

import java.lang.annotation.*;

/**
 * @author yiuman
 * @date 2023/7/26
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
@Inherited
public @interface QueryField {

    /**
     * 查询方法
     */
    String method() default SqlConsts.EQUALS;

    /**
     * 字段映射
     */
    String mapping() default "";

    /**
     * 子句
     */
    Clauses clauses() default Clauses.AND;

    /**
     * 是否必须（值为空是是否拼入sql）
     */
    boolean require() default false;

    /**
     * 条件处理器
     *
     * @return 用于手动拼接
     */
    Class<? extends ConditionHandler> handler() default ConditionHandler.class;
}