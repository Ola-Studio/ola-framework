package io.ola.common.utils;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author yiuman
 * @date 2023/7/31
 */
@SuppressWarnings("unchecked")
@Component
public final class SpringUtils extends SpringUtil {
    private SpringUtils() {
    }

    public static <T> T getBean(Class<T> clazz, boolean force) {
        Object bean;
        try {
            bean = getApplicationContext().getBean(clazz);
        } catch (NoSuchBeanDefinitionException var4) {
            bean = force ? getApplicationContext().getAutowireCapableBeanFactory().createBean(clazz) : null;
        }

        return (T) bean;
    }

    public static Object getSpringProxy() {
        Object proxy = null;
        try {
            proxy = AopContext.currentProxy();
        } catch (IllegalStateException ignore) {
        }
        return proxy;
    }

    public static Object getSpringProxyOrThis(Object thisObject) {
        Object springProxy = getSpringProxy();
        return Optional.ofNullable(springProxy).orElse(thisObject);
    }
}
