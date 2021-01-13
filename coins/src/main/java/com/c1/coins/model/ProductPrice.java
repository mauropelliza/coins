package com.c1.coins.model;

public class ProductPrice {
	private Integer productId;
	private String title;
	private Double price;
	private Double coins;
	private Currency currency;
	private boolean visible;

	public ProductPrice() {

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

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
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
