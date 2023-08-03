package io.ola.crud.inject.impl;

import io.ola.crud.inject.Injector;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author yiuman
 * @date 2023/8/2
 */
public class NowInjector implements Injector {
    @Override
    public Object getInjectValue(Field field) {
        field.setAccessible(true);
        if (field.getType().isAssignableFrom(Date.class)) {
            return new Date();
        }

        if (field.getType().isAssignableFrom(long.class) || field.getType().isAssignableFrom(Long.class)) {
            return System.currentTimeMillis();
        }


        if (field.getType().isAssignableFrom(LocalDateTime.class)) {
            return LocalDateTime.now();
        }
        return null;
    }
}
