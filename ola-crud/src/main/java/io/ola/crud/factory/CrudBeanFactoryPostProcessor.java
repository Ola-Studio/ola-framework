package io.ola.crud.factory;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.TypeUtil;
import io.ola.crud.CRUD;
import io.ola.crud.service.CrudService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * @author yiuman
 * @date 2024/4/30
 */
@Component
public class CrudBeanFactoryPostProcessor extends AutowiredAnnotationBeanPostProcessor implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        Field[] fields = ReflectUtil.getFields(beanClass);
        for (Field field : fields) {
            if (Objects.equals(field.getType(), CrudService.class)) {
                Type[] actualTypes = TypeUtil.getTypeArguments(field.getGenericType());
                Type entityType = ArrayUtil.get(actualTypes, 0);
                CrudService<?> crudService = CRUD.getCrudService((Class<?>) entityType);
                applicationContext.getAutowireCapableBeanFactory().configureBean(crudService, field.getName());
            }
        }
        return super.postProcessBeforeInstantiation(beanClass, beanName);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}


