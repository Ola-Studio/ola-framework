package io.ola.crud.inject;

import java.lang.annotation.Annotation;

/**
 * 注解注入解析器
 *
 * @param <A> 用于解释的注解
 * @author yiuman
 * @date 2020/7/23
 */
public interface InjectAnnotationParser<A extends Annotation> {

    /**
     * 解析注解返回需要的对象
     *
     * @param annotation 当前解析的注解
     * @return 解析返回的对象
     */
    Object parse(A annotation);

}