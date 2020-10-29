package com.c1.coins.model;

import com.c1.coins.utils.Utils;

public class Product {
	private String name;
	private Double coins;
	private Double usd;

	public Product(String name, String coins, String usd) {
		name = name.replaceAll(" pulgadas ", "\" ");
		this.name = name;
		this.usd = Utils.toDouble(coins.trim());
		this.coins = Utils.toDouble(usd.trim());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getCoins() {
		return coins;
	}

	public void setCoins(Double coins) {
		this.coins = coins;
	}

	public Double getUsd() {
		return usd;
	}

	public void setUsd(Double usd) {
		this.usd = usd;
	}

}
