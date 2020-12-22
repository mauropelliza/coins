package com.c1.coins.utils;

public enum CsvActionsEnum {
	CREAR("CREAR"), MODIFICAR("MODIFICAR");
	
	private String value;
	
	private CsvActionsEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
	
}
