package io.ola.crud.serializer;

import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import io.ola.crud.annotation.Sensitive;
import io.ola.crud.enums.SensitiveStrategy;
import io.ola.crud.service.SensitiveHandler;

import java.io.IOException;
import java.util.Objects;

/**
 * 脱敏序列化器
 *
 * @author yiuman
 * @date 2021/12/1
 */
public class SensitiveJsonSerializer extends JsonSerializer<String> implements ContextualSerializer {

    private Class<? extends SensitiveHandler> handleClass;

    private SensitiveStrategy sensitiveStrategy;

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String sensitiveStr;
        if (Objects.nonNull(handleClass) && !handleClass.equals(SensitiveHandler.class)) {
            SensitiveHandler sensitiveHandler = SpringUtil.getBean(handleClass);
            sensitiveStr = sensitiveHandler.apply(value);
        } else {
            sensitiveStr = sensitiveStrategy.getDesensitizer().apply(value);
        }

        gen.writeString(sensitiveStr);
    }


    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        Sensitive annotation = property.getAnnotation(Sensitive.class);
        if (Objects.nonNull(annotation) && Objects.equals(String.class, property.getType().getRawClass())) {
            this.sensitiveStrategy = annotation.strategy();
            this.handleClass = annotation.using();
            return this;
        }
        return prov.findValueSerializer(property.getType(), property);
    }
}
