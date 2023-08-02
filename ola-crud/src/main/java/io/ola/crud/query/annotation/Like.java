package io.ola.crud.query.annotation;

import com.mybatisflex.core.constant.SqlConsts;
import io.ola.crud.enums.Clauses;
import io.ola.crud.query.ConditionHandler;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author yiuman
 * @date 2023/8/2
 */

@QueryField(method = SqlConsts.LIKE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Like {

    @AliasFor(annotation = QueryField.class)
    String method() default "";

    @AliasFor(annotation = QueryField.class)
    String mapping() default "";

    @AliasFor(annotation = QueryField.class)
    Clauses clauses() default Clauses.AND;

    @AliasFor(annotation = QueryField.class)
    boolean require() default false;

    @AliasFor(annotation = QueryField.class)
    Class<? extends ConditionHandler> handler() default ConditionHandler.class;
}