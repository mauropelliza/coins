package com.c1.coins.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Utils {

	private static DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private static double EPSILON = 0.001;

	public static String sanitize(Object obj) {
		if (obj == null) {
			return "null";
		}
		if (obj instanceof LocalDateTime) {
			return ((LocalDateTime) obj).format(TIMESTAMP_FORMATTER);
		}
		return obj.toString();
	}

	public static Double toDouble(String d) {
		return isBlank(d) ? 0.0 : Double.valueOf(d);
	}

	public static Integer toInteger(String d) {
		return isBlank(d) ? 0 : Integer.valueOf(d);
	}

	public static boolean isBlank(String d) {
		return d == null || d.trim().length() == 0;
	}

	public static boolean isZero(Double value) {
		return value == null || (value >= -EPSILON && value <= EPSILON);
	}

	public static boolean equals(Double v1, Double v2) {
		if (v1 == null || v2 == null) {
			return false;
		}

		return isZero(v1 - v2);
	}

	public static String getDBDateString(LocalDate date, boolean isStartDate) {
		String firstHours = " 00:00:00";
		String lastHours = " 23:59:59";
		String expression = date.getYear() + "-" + date.getMonthValue() + "-" + date.getDayOfMonth();

		if (isStartDate)
			return expression + firstHours;
		else
			return expression + lastHours;
	}

	public static LocalDate getFirstDayOfYear() {
		LocalDate now = LocalDate.now();
		return LocalDate.of(now.getYear(), 1, 1);
	}

	public static LocalDate getLastDayOfYear() {
		LocalDate now = LocalDate.now();
		return LocalDate.of(now.getYear(), 12, 31);
	}

	public static LocalDateTime parseToLocalDateTime(String stringDate) {
		return LocalDateTime.parse(stringDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}

	public static String getNowLocalDateTimeString() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return LocalDateTime.now().format(formatter);
	}

}
