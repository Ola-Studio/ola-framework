package io.ola.crud.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * LocalDateTime转时间戳
 *
 * @author yiuman
 * @date 2024/5/20
 */
public class LocalDateTimeToEpochSerializer extends JsonSerializer<LocalDateTime> {
    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        if (value != null) {
            long timestamp = value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            gen.writeNumber(timestamp);
        }
    }
}
