package com.c1.coins.service;

import java.time.LocalDate;
import java.util.List;

import com.c1.coins.model.Order;

public interface OrdersService {
	public List<Order> createYearReport(LocalDate startDate, LocalDate endDate, Integer orderStatus);
}
