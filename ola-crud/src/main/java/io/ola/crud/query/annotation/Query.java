package io.ola.crud.query.annotation;

import io.ola.crud.enums.Clauses;
import io.ola.crud.query.ConditionHandler;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yiuman
 * @date 2023/7/26
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Query {
    @AliasFor
    String method() default "EQUALS";

    String mapping() default "";

    Clauses clauses() default Clauses.AND;

    boolean require() default false;

    Class<? extends ConditionHandler> handler() default ConditionHandler.class;
}