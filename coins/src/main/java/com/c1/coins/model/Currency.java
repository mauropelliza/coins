package com.c1.coins.model;

public enum Currency {
	USD(178), USD_REAL(178), USD_OFICIAL(178), USD_MEP(311), ARS(1), UNKNOWN(0);

	int coinsPerUSD = 1;

	private Currency(int coinsPerUSD) {
		this.coinsPerUSD = coinsPerUSD;
	}

	public Double toCurrency(Double coins) {
		return coins /coinsPerUSD;
	}
	
	public void setCoinsPerUSD(int coinsPerUSD) {
		this.coinsPerUSD = coinsPerUSD;
	}
	
	

}
