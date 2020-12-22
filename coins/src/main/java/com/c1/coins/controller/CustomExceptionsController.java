package com.c1.coins.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.c1.coins.validators.CsvReadingException;
import com.c1.coins.validators.DBException;
import com.c1.coins.validators.DataValidationException;

@ControllerAdvice
public class CustomExceptionsController {

	@ExceptionHandler(value = DataValidationException.class)
	public ResponseEntity<Object> exception(DataValidationException exception) {
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler({DBException.class, CsvReadingException.class})
	public ResponseEntity<Object> dbException(DBException exception) {
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
