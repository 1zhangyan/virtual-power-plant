package com.virtualpowerplant.utils;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TimeUtils {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ZoneId zoneId = ZoneId.of("Asia/Shanghai");
    public static String getDayStart(String dateTimeStr) {

        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, formatter);
        long millis = dateTime.toLocalDate()
                .atStartOfDay(ZoneId.of("Asia/Shanghai"))
                .toInstant()
                .toEpochMilli();
        Instant instant = Instant.ofEpochMilli(millis);
        ZonedDateTime zonedDateTime = instant.atZone(zoneId);
        return formatter.format(zonedDateTime);
    }

    public static String getHourStart(String dateTimeStr) {
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, formatter);
        long millis = dateTime.withMinute(0)
                .withSecond(0)
                .withNano(0)
                .atZone(zoneId)
                .toInstant()
                .toEpochMilli();
        Instant instant = Instant.ofEpochMilli(millis);
        ZonedDateTime zonedDateTime = instant.atZone(zoneId);
        return formatter.format(zonedDateTime);
    }

    public static String getCurrentDayStart() {
        LocalDateTime now = LocalDateTime.now();
        long millis = now.toLocalDate()
                .atStartOfDay(zoneId)
                .toInstant()
                .toEpochMilli();
        Instant instant = Instant.ofEpochMilli(millis);
        ZonedDateTime zonedDateTime = instant.atZone(zoneId);
        return formatter.format(zonedDateTime);
    }

    public static String getNextDayStart() {
        LocalDateTime now = LocalDateTime.now();
        long millis = now.toLocalDate()
                .minusDays(1)
                .atStartOfDay(zoneId)
                .toInstant()
                .toEpochMilli();
        Instant instant = Instant.ofEpochMilli(millis);
        ZonedDateTime zonedDateTime = instant.atZone(zoneId);
        return formatter.format(zonedDateTime);
    }
    public static boolean isHourlyData(String dateTime) {
        LocalTime time = LocalTime.parse(dateTime, formatter);
        return time.getMinute() == 0 && time.getSecond() == 0;
    }
    public static double calculateHoursDifference(String startDateTimeStr, String endDateTimeStr) {
        LocalDateTime startDateTime = LocalDateTime.parse(startDateTimeStr, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(endDateTimeStr, formatter);
        Duration duration = Duration.between(startDateTime, endDateTime);
        return duration.toNanos() / 1_000_000_000.0 / 3600.0;
    }

    public static String localTimeToStr(LocalDateTime dateTime) {
        return formatter.format(dateTime);
    }
}
