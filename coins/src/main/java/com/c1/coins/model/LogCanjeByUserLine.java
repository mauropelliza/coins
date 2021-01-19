package com.c1.coins.model;

import com.c1.coins.utils.Utils;

public class LogCanjeByUserLine {

	private LogCanjeByUser parentLine;
	private LineOrder lineOrder;

	public LogCanjeByUserLine(LogCanjeByUser parent, LineOrder lineOrder) {
		super();
		this.parentLine = parent;
		this.lineOrder = lineOrder;
	}

	public Double getAccumulatedCoins() {
		LogCanjeByUserLine previus = this.parentLine.getPreviousLine(this);
		if (previus == null) {
			return this.getLineTotal();
		}
		return previus.getAccumulatedCoins() + this.getLineTotal();
	}

	public Double getRemaingCoins() {

		// Si la linea se proceso entonces la cantidad de puntos que tenia el user es
		// currentCoins - acumulado
		// Si ya se proceso entonces cantidad de puntos = currentPoints + acumulado
		if ("wc-processing".equals(lineOrder.getParentOrder().getStatus())) {
			return this.parentLine.getUser().getSpentCoins() - this.getAccumulatedCoins();
		}
		return this.getParentOrder().getUser().getSpentCoins();
	}

	public String getProduct() {
		return lineOrder.getProductName();
	}

	public Integer getQuantity() {
		return lineOrder.getQuantity();
	}

	public Double getLineTotal() {
		return lineOrder.getTotalCoins();
	}

	public Integer getProductId() {
		return lineOrder.getProductId();
	}

	public String getUserName() {
		return this.parentLine.getUser().getDisplayName();
	}

	public Order getParentOrder() {
		return lineOrder.getParentOrder();
	}

	public String validate() {
		StringBuilder b = new StringBuilder();
		Double productPriceInCatalog = this.lineOrder.getProductUsdInCatalog();
		if (Utils.isZero(productPriceInCatalog)) {
			b.append("Product has no price in catalog");
		} else {
			Double coinsForOneProduct = this.getLineTotal() / this.getQuantity();
			if (!Utils.equals(coinsForOneProduct, lineOrder.getProductCoinsInCatalog())) {
				b.append("Coins in catalog: " + lineOrder.getProductCoinsInCatalog() + ".\nCoins in order: "
						+ coinsForOneProduct);
			}
			Double calculatedCoinsForOneProduct = this.lineOrder.getProductCurrencyInCatalog()
					.priceToCoins(productPriceInCatalog);
			if (!Utils.equals(calculatedCoinsForOneProduct, lineOrder.getProductCoinsInCatalog())) {
				b.append("Coins in catalog: " + lineOrder.getProductCoinsInCatalog() + ".\nCalculated coins: "
						+ calculatedCoinsForOneProduct);
			}
		}
		return b.toString();
	}

}
