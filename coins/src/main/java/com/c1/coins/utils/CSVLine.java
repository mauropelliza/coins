package com.c1.coins.utils;

import java.util.List;

import com.google.common.collect.Lists;

public class CSVLine {

	private List<String> cells = Lists.newArrayList();

	public void add(Object value) {
		this.cells.add("\"" + Utils.sanitize(value) + "\"");
	}

	@Override
	public String toString() {
		return String.join(",", this.cells) + "\n";
	}

}
