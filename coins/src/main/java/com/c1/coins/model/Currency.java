package com.c1.coins.model;

public enum Currency {
	USD(178), USD_REAL(178), USD_OFICIAL(178), USD_MEP(311), ARS(1), UNKNOWN(0);

	int priceToCoinsFactor = 1;

	private Currency(int priceFactor) {
		this.priceToCoinsFactor = priceFactor;
	}

	public Double priceToCoins(Double price) {
		return price * priceToCoinsFactor;
	}

}
