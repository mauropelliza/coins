package com.c1.coins.model;

import java.util.List;

import com.google.common.collect.Lists;

public class LogCanjeByUser {
	private User user;
	private List<LogCanjeByUserLine> lineOrders = Lists.newArrayList();

	public LogCanjeByUser(User user) {
		super();
		this.user = user;
	}

	public void addLineOrder(LineOrder lineOrder) {
		this.lineOrders.add(new LogCanjeByUserLine(this, lineOrder));
	}

	public LogCanjeByUserLine getPreviousLine(LogCanjeByUserLine line) {
		int index = lineOrders.indexOf(line);
		if (index == 0) {
			return null;
		}
		return lineOrders.get(index - 1);
	}

	public User getUser() {
		return this.user;
	}

	public List<LogCanjeByUserLine> getLines() {
		return lineOrders;
	}

}
