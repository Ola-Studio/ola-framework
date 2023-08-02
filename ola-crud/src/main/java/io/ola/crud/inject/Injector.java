package io.ola.crud.inject;

import java.lang.reflect.Field;

/**
 * @author yiuman
 * @date 2023/8/2
 */
public interface Injector {

    Object getInjectValue(Field field);
}