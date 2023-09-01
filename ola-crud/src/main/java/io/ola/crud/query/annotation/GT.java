package io.ola.crud.query.annotation;

import com.mybatisflex.core.constant.SqlConsts;
import io.ola.crud.enums.Clauses;
import io.ola.crud.query.ConditionHandler;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author yiuman
 * @date 2023/8/2
 */
@QueryField(method = SqlConsts.GT)
@Retention(RetentionPolicy.RUNTIME)
public @interface GT {

    @AliasFor(annotation = QueryField.class)
    String mapping() default "";

    @AliasFor(annotation = QueryField.class)
    Clauses clauses() default Clauses.AND;

    @AliasFor(annotation = QueryField.class)
    boolean require() default false;

    @AliasFor(annotation = QueryField.class)
    Class<? extends ConditionHandler> handler() default ConditionHandler.class;
}