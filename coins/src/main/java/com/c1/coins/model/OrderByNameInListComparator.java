package com.c1.coins.model;

import java.util.Comparator;

public class OrderByNameInListComparator implements Comparator<Order> {

	public OrderByNameInListComparator() {
		super();
	}

	@Override
	public int compare(Order o1, Order o2) {
		return o1.getUserName().compareTo(o2.getUserName());
	}

}
