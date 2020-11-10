package com.c1.coins.model;

public class BuyReportItemRequester {
	private String orderUrl;
	private User user;
	private Integer id;
	private Integer quantity;

	public BuyReportItemRequester(Integer id, User user, Integer quantity) {
		this.id = id;
		this.user = user;
		this.quantity = quantity;
		this.orderUrl = String.format(
				"http://hxv-creditonepuntos.hexacta.com/creditonepoints/wp-admin/post.php?post=%s&action=edit",
				id.toString());
	}

	public Integer getQuantity() {
		return quantity;
	}

	public String getUserDisplayName() {
		return this.user.getDisplayName();
	}

	public String getUserBillingEmail() {
		return this.user.getBillingEmail();
	}

	public String getOrderUrl() {
		return this.orderUrl;
	}

	public String getUserOffice() {
		return this.user.getBillingCity();
	}

}
