package com.c1.coins.csv;

import com.c1.coins.model.ProductFull;
import com.c1.coins.utils.CSVLine;

public class ProductToCsvSerializer {
	public String header() {
		CSVLine cells = new CSVLine();
		cells.add("id del producto");
		cells.add("nombre");
		cells.add("stock");
		cells.add("coins (woo db)");
		cells.add("coins (excel)");
		cells.add("USD (excel)");
		cells.add("visible");

		return cells.toString();

	}

	public String toString(ProductFull product) {
		CSVLine cells = new CSVLine();
		cells.add(product.getId());
		cells.add(product.getTitle());
		cells.add(product.getStock());
		cells.add(product.getDbCoins());
		cells.add(product.getCsvCoins());
		cells.add(product.getCsvUsd());
		cells.add(product.getVisible());

		return cells.toString();
	}
}
