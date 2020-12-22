package com.c1.coins.model;

public class ProductFull {
	private Integer id;
	private String title;
	private String dbCoins;
	private String csvCoins;
	private String csvUsd;
	private String csvCurrency;
	private String csvCurrencyType;
	private boolean visible;
	
	public ProductFull() {

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
	public String getDbCoins() {
		return dbCoins;
	}
	public void setDbCoins(String dbCoins) {
		this.dbCoins = dbCoins;
	}
	public String getCsvCoins() {
		return csvCoins;
	}
	public void setCsvCoins(String csvCoins) {
		this.csvCoins = csvCoins;
	}
	public String getCsvUsd() {
		return csvUsd;
	}
	public void setCsvUsd(String csvUsd) {
		this.csvUsd = csvUsd;
	}
	public String getCsvCurrency() {
		return csvCurrency;
	}
	public void setCsvCurrency(String csvCurrency) {
		this.csvCurrency = csvCurrency;
	}
	public String getCsvCurrencyType() {
		return csvCurrencyType;
	}
	public void setCsvCurrencyType(String csvCurrencyType) {
		this.csvCurrencyType = csvCurrencyType;
	}
	public boolean getVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
