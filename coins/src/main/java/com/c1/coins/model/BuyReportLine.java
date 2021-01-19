package com.c1.coins.model;

import java.util.List;

import com.google.common.collect.Lists;
import com.c1.coins.model.Currency;

public class BuyReportLine {
	private Integer quantity = 0;;
	private String product;
	private Double productPrice = 0.0;
	private Currency currency;
	private Double productCoins = 0.0;
	private List<BuyReportDetailLine> requesters = Lists.newArrayList();

	public BuyReportLine() {
		super();
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public Double getProductPrice() {
		return productPrice;
	}

	public Double getTotalPrice() {
		return productPrice * quantity;
	}

	public void setProductPrice(Double price) {
		this.productPrice = price;
	}

	public List<BuyReportDetailLine> getRequesters() {
		return requesters;
	}

	public void setRequesters(List<BuyReportDetailLine> requesters) {
		this.requesters = requesters;
	}

	public void addQuantity(Integer qty) {
		this.quantity += qty;
	}

	public void addRequester(BuyReportDetailLine buyReportItemRequester) {
		this.requesters.add(buyReportItemRequester);

	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public Double getProductCoins() {
		return productCoins;
	}

	public void setProductCoins(Double coins) {
		this.productCoins = coins;
	}
	
	
	
	

}
