package com.c1.coins.model;

import java.util.Map;

import com.c1.coins.utils.Utils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Maps;

@JsonIgnoreProperties(value = { "parentOrder" })
public class LineOrder {

	private String product;
	private Integer productIdNumber;
	private Map<String, String> meta = Maps.newHashMap();
	private Order parentOrder;
	private Double productCoinsInCatalog;
	private Double productUsdInCatalog;

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public void addMeta(String key, String value) {
		this.meta.put(key, value);
	}

	public Integer getQuantity() {
		return Utils.toInteger(this.meta.get("_qty"));
	}

	public Double getLineTotal() {
		return Utils.toDouble(this.meta.get("_line_total"));
	}

	public Double getLineSubtotal() {
		return Utils.toDouble((this.meta.get("_line_subtotal")));
	}

	public String getProductId() {
		return this.meta.get("_product_id");
	}

	public Order getParentOrder() {
		return parentOrder;
	}

	public void setParentOrder(Order parentOrder) {
		this.parentOrder = parentOrder;
	}

	public void setProductCoinsInCatalog(Double productCoinsInCatalog) {
		this.productCoinsInCatalog = productCoinsInCatalog;
	}

	public void setProductUsdInCatalog(Double productUsdInCatalog) {
		this.productUsdInCatalog = productUsdInCatalog;
	}
	
	

	public Double getProductCoinsInCatalog() {
		return productCoinsInCatalog == null ? 0.0 : productCoinsInCatalog;
	}

	public Double getProductUsdInCatalog() {
		return productUsdInCatalog == null ? 0.0 : productUsdInCatalog;
	}

	@Override
	public String toString() {
		return this.getProductId() + ": " + this.product + " x " + this.getQuantity() + ". SubTotal: "
				+ getLineSubtotal() + " Total:" + getLineTotal();
	}

	public Integer getProductIdNumber() {
		return productIdNumber;
	}

	public void setProductIdNumber(Integer productIdNumber) {
		this.productIdNumber = productIdNumber;
	}

}
