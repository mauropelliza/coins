package com.c1.coins.model;

public class ProductInCatalog {
	private Integer productId;
	private String title;
	private Double coins;
	private Currency currency;
	private boolean visible;

	public ProductInCatalog() {

	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer id) {
		this.productId = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Double getCoins() {
		return coins;
	}

	public void setCoins(Double coins) {
		this.coins = coins;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public boolean getVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
