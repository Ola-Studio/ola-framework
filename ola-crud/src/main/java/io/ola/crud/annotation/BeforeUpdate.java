package io.ola.crud.annotation;


import io.ola.crud.inject.Injector;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 更新前的注解标志
 *
 * @author yiuman
 * @date 2021/8/17
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface BeforeUpdate {

    Class<? extends Injector> value();

    boolean force() default true;
}