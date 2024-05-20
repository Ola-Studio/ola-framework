package io.ola.defaultbean.config;

import io.ola.crud.serializer.EpochToLocalDateTimeDeserializer;
import io.ola.crud.serializer.LocalDateTimeToEpochSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * @author yiuman
 * @date 2024/5/20
 */
@Configuration
public class OlaDefaultBeanConfiguration {
    @Bean
    @ConditionalOnBean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> builder.serializerByType(LocalDateTime.class, new LocalDateTimeToEpochSerializer())
                .deserializerByType(LocalDateTime.class, new EpochToLocalDateTimeDeserializer());
    }
}
