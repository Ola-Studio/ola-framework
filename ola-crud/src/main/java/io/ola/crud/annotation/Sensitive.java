package io.ola.crud.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.ola.crud.enums.SensitiveStrategy;
import io.ola.crud.serializer.SensitiveJsonSerializer;
import io.ola.crud.service.SensitiveHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记脱敏
 *
 * @author yiuman
 * @date 2021/12/1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveJsonSerializer.class)
public @interface Sensitive {

    SensitiveStrategy strategy();

    Class<? extends SensitiveHandler> using() default SensitiveHandler.class;

}