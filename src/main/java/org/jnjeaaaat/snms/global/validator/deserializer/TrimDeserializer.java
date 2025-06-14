package org.jnjeaaaat.snms.global.validator.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class TrimDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JacksonException {
        String value = jsonParser.getValueAsString();
        if (value == null) {
            return null;
        }
        return value.trim();
    }
}
