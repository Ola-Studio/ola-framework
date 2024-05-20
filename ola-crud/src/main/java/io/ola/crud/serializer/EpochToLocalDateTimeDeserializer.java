package io.ola.crud.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 时间戳转LocalDateTime
 *
 * @author yiuman
 * @date 2024/5/20
 */
public class EpochToLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        NumberDeserializers.LongDeserializer longDeserializer = new NumberDeserializers.LongDeserializer(Long.TYPE, 0L);
        Long epoch = longDeserializer.deserialize(p, ctxt);
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneId.systemDefault());
    }
}
