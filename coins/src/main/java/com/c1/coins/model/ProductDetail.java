package com.c1.coins.model;

public class ProductDetail {
	private Integer id;
	private String title;
	private String wooCoins;
	private String hxCoins;
	private String hxUsd;
	private String hxCurrency;
	private String hxCurrencyType;
	private boolean visible;
	
	public ProductDetail() {

	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getWooCoins() {
		return wooCoins;
	}
	public void setWooCoins(String wooCoins) {
		this.wooCoins = wooCoins;
	}
	public String getHxCoins() {
		return hxCoins;
	}
	public void setHxCoins(String csvCoins) {
		this.hxCoins = csvCoins;
	}
	public String getHxUsd() {
		return hxUsd;
	}
	public void setHxUsd(String hxUsd) {
		this.hxUsd = hxUsd;
	}
	public String getHxCurrency() {
		return hxCurrency;
	}
	public void setHxCurrency(String hxCurrency) {
		this.hxCurrency = hxCurrency;
	}
	public String getHxCurrencyType() {
		return hxCurrencyType;
	}
	public void setHxCurrencyType(String hxCurrencyType) {
		this.hxCurrencyType = hxCurrencyType;
	}
	public boolean getVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
