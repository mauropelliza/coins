package com.c1.coins.validators;

public class DataValidationException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public DataValidationException(String message) {
		super (message);
	}
}