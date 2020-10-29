package com.c1.coins.validators;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

@Component
public class DateValidator {
	
	public void validateDateRange(LocalDate start, LocalDate end) {
		if (start.compareTo(end) > 0)
			throw new DataValidationException("la fecha de inicio debe ser menor o igual a la fecha de fin");
	}

	public boolean areDatesEmpty(LocalDate startDate, LocalDate endDate) {
		if (startDate == null || endDate == null) {
			return true;
		}
		return false;
	}
}
