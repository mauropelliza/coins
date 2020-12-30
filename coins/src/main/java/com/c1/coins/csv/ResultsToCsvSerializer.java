package com.c1.coins.csv;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ResultsToCsvSerializer {
	private String csvSeparator = ",";
	
	private List<String> cells = null;
	
	public String header() {
		cells = new ArrayList<String>();
		cells.add("id del producto");
		cells.add("nombre");
		cells.add("coins (woo db)");
		cells.add("coins (excel)");
		cells.add("USD (excel)");
		cells.add("currency (excel)");
		cells.add("currency type (excel)");
		cells.add("visible");
		cells.add("acción");
		cells.add("resultado");

		return toString();
	}
	
	public String toString() {
		return String.join(csvSeparator, this.cells) + "\n";
	}
}
