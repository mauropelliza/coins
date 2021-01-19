package com.c1.coins.model;

import java.util.List;
import java.util.Map;

import org.assertj.core.util.Lists;

import com.c1.coins.utils.Utils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Maps;

@JsonIgnoreProperties(value = { "parentOrder" })
public class LineOrder {

	private Integer id;
	private String productName;
	private Integer lineOrderId;
	private Map<String, String> meta = Maps.newLinkedHashMap();
	private Order parentOrder;
	private Double productCoinsInCatalog;
	private Double productPriceInCatalog;
	private Currency productCurrencyInCatalog = Currency.UNKNOWN;

	private List<String> errors = Lists.newArrayList();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String product) {
		this.productName = product;
	}

	public void setMetas(Map<String, String> metas) {
		this.meta.clear();
		this.meta.putAll(metas);
	}

	public void validate() {
		if (this.getProductUsdInCatalog() == null) {
			this.addError("There is not dolar price for this product in the catalog");
		}
		if (this.getProductCoinsInCatalog() == null) {
			this.addError("There is not coins value for this product in the catalog");
		}
		if (this.getProductCurrencyInCatalog() == null) {
			this.addError("There is not a currency assigned to this product in the catalog");
		}
		Double productCoinsInCatalog = this.getProductCoinsInCatalog();
		productCoinsInCatalog = productCoinsInCatalog == null ? 0.0 : productCoinsInCatalog;
		if (!Utils.equals(productCoinsInCatalog, this.getProductCoins())) {
			this.addError(String.format("Price mismatch. Product name: %s. Id: %s. Catalog: %s.  Line: %s",
					this.getProductName(), this.getProductId(), this.getProductCoinsInCatalog(),
					this.getProductCoins()));
		}
	}

	public Integer getQuantity() {
		return Utils.toInteger(this.meta.get("_qty"));
	}

	public Double getProductCoins() {
		return getTotalCoins() / getQuantity();
	}

	public Double getTotalCoins() {
		return Utils.toDouble(this.meta.get("_line_total"));
	}

	public Integer getProductId() {
		return Utils.toInteger(this.meta.get("_product_id"));
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

	public void setProductPriceInCatalog(Double productPriceInCatalog) {
		this.productPriceInCatalog = productPriceInCatalog;
	}

	public Double getProductCoinsInCatalog() {
		return productCoinsInCatalog == null ? 0.0 : productCoinsInCatalog;
	}

	public Double getProductUsdInCatalog() {
		return productPriceInCatalog == null ? 0.0 : productPriceInCatalog;
	}

	public Currency getProductCurrencyInCatalog() {
		return productCurrencyInCatalog;
	}

	public void setProductCurrencyInCatalog(Currency productCurrencyInCatalog) {
		this.productCurrencyInCatalog = productCurrencyInCatalog;
	}

	@Override
	public String toString() {
		return this.getProductId() + ": " + this.productName + " x " + this.getQuantity() + ". Product Coins: "
				+ getProductCoins() + " Total:" + getTotalCoins();
	}

	public Integer getLineOrderId() {
		return lineOrderId;
	}

	public void setLineOrderId(Integer lineOrderId) {
		this.lineOrderId = lineOrderId;
	}

	public void addError(String error) {
		this.errors.add(error);
	}

	public List<String> getErrors() {
		return this.errors;
	}

	public void addErrors(List<String> errors) {
		this.errors.addAll(errors);

	}

}
