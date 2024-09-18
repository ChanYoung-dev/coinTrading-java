package com.chan.backtestBinance.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

    public static long getKoreanStartTime(int year, int month, int day, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        calendar.set(year, month - 1, day, hour, minute, second);
        calendar.set(Calendar.MILLISECOND, 0);
        Date koreanDate = calendar.getTime();

        // UTC로 변환
        Calendar utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        utcCalendar.setTime(koreanDate);

        return utcCalendar.getTimeInMillis();
    }

    public static String convertToKoreanTime(long timestamp) {
        // 타임스탬프를 Instant로 변환
        Instant instant = Instant.ofEpochMilli(timestamp);

        // 한국 시간대(KST)로 변환
        LocalDateTime koreanDateTime = LocalDateTime.ofInstant(instant, ZoneId.of("Asia/Seoul"));

        // 원하는 포맷으로 날짜 표현 (예: yyyy-MM-dd HH:mm:ss)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return koreanDateTime.format(formatter);
    }
}
