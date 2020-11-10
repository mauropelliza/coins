package com.c1.coins.utils;

public enum VisibilityEnum {
	HIDDEN("HIDDEN"), VISIBLE("VISIBLE");

	private String value;

	VisibilityEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
