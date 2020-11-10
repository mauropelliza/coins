package com.c1.coins.model;

import java.util.List;

import com.google.common.collect.Lists;

public class BuyReportProductOrderLine {
	private Integer quantity = 0;;
	private String product;
	private Double price = 0.0;
	private List<BuyReportItemRequester> requesters = Lists.newArrayList();

	public BuyReportProductOrderLine() {
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

	public List<BuyReportItemRequester> getRequesters() {
		return requesters;
	}

	public void setRequesters(List<BuyReportItemRequester> requesters) {
		this.requesters = requesters;
	}

	public void addQuantity(Integer qty) {
		this.quantity += qty;
	}

	public void addRequester(BuyReportItemRequester buyReportItemRequester) {
		this.requesters.add(buyReportItemRequester);

	}

}
