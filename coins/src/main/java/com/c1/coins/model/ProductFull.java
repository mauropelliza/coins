package com.c1.coins.model;

public class ProductFull {
	private Integer id;
	private String title;
	private String stock;
	private String dbCoins;
	private String csvCoins;
	private String csvUsd;
	private String visible;
	
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
	public String getStock() {
		return stock;
	}
	public void setStock(String stock) {
		this.stock = stock;
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
	public String getVisible() {
		return visible;
	}
	public void setVisible(String visible) {
		this.visible = visible;
	}
}
