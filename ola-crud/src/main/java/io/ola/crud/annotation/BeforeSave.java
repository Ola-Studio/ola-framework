package io.ola.crud.annotation;


import io.ola.crud.inject.Injector;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 保存前的注解标志
 *
 * @author yiuman
 * @date 2021/8/17
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface BeforeSave {

    Class<? extends Injector> value();

    boolean force() default true;

}