package com.c1.coins.model;

import java.util.Comparator;

public class OrderByUserNameComparator implements Comparator<LineOrder> {

	@Override
	public int compare(LineOrder o1, LineOrder o2) {
		return o1.getParentOrder().getUserName().compareTo(o2.getParentOrder().getUserName());
	}

}
