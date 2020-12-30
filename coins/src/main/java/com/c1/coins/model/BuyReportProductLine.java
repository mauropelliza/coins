package com.c1.coins.model;

import java.util.List;

import com.google.common.collect.Lists;
import com.c1.coins.model.Currency;

public class BuyReportProductLine {
	private Integer quantity = 0;;
	private String product;
	private Double price = 0.0;
	private Currency currency;
	private List<BuyReportProductLineDetail> requesters = Lists.newArrayList();

	public BuyReportProductLine() {
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

	public Double getPrice() {
		return price;
	}

	public Double getTotal() {
		return price * quantity;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public List<BuyReportProductLineDetail> getRequesters() {
		return requesters;
	}

	public void setRequesters(List<BuyReportProductLineDetail> requesters) {
		this.requesters = requesters;
	}

	public void addQuantity(Integer qty) {
		this.quantity += qty;
	}

	public void addRequester(BuyReportProductLineDetail buyReportItemRequester) {
		this.requesters.add(buyReportItemRequester);

	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	
	

}
