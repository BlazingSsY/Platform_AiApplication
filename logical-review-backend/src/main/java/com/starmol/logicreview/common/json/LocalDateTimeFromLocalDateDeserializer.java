package com.starmol.logicreview.common.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author yuexiaopeng
 */
public class LocalDateTimeFromLocalDateDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        if (StringUtils.isEmpty(jsonParser.getText()) || StringUtils.isEmpty(jsonParser.getText().trim())) {
            return null;
        }
        LocalDate localDate = LocalDate.parse(jsonParser.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return LocalDateTime.of(localDate, LocalTime.MIN);
    }
}
