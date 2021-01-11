package com.c1.coins.utils;

import java.io.Reader;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class Utils {
	private static DateTimeFormatter TIMESTAMP_FORMATTER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss").appendPattern("[.SSSSSSSSS][.SSSSSS][.SSS][.S]").toFormatter();
	
	private static double EPSILON = 0.001;

	public static String normalize(String input) {
		return Normalizer.normalize(input, Normalizer.Form.NFD)
				.replaceAll("[^\\p{ASCII}]", "").toUpperCase().trim();
	}
	
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
		return LocalDateTime.parse(stringDate, TIMESTAMP_FORMATTER);
	}

	public static String getNowLocalDateTimeString() {
		return LocalDateTime.now().format(TIMESTAMP_FORMATTER);
	}
	
	public static CSVReader getCsvReaderUsingSeparator(Reader reader, String separator) {
		CSVReaderBuilder builder = new CSVReaderBuilder(reader).withCSVParser(new CSVParserBuilder()
			    .withSeparator(separator.charAt(0)).withIgnoreLeadingWhiteSpace(true).withIgnoreQuotations(true).build());
		return builder.withSkipLines(1).build();
		
	}

}
