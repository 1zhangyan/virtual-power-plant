package com.virtualpowerplant.utils;

import static com.virtualpowerplant.constant.Constant.objectMapper;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;

public class JsonUtils {

    public static String toJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("");
        }
    }


    public static List<Double> toListDouble(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<Double>>() {});
        } catch (Exception e) {
            throw new RuntimeException("cannot parse to List double" + json);
        }
    }
}
