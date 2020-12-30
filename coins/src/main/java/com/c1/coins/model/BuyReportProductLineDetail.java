package com.c1.coins.model;


public class BuyReportProductLineDetail {
	private String orderUrl;
	private LineOrder lineOrder;

	public BuyReportProductLineDetail(LineOrder lineOrder) {
		this.lineOrder = lineOrder;
		this.orderUrl = String.format(
				"http://hxv-creditonepuntos.hexacta.com/creditonepoints/wp-admin/post.php?post=%s&action=edit",
				lineOrder.getParentOrder().getId().toString());

	}

	public Integer getQuantity() {
		return lineOrder.getQuantity();
	}

	public User getUser() {
		return lineOrder.getParentOrder().getUser();
	}

	public String getOrderUrl() {
		return this.orderUrl;
	}

	public Order getOrder() {
		return this.lineOrder.getParentOrder();
	}

}
