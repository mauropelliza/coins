package com.c1.coins.service;

import java.time.LocalDate;
import java.util.List;

import com.c1.coins.model.BuyReportProductLine;
import com.c1.coins.model.Order;

public interface OrdersService {
	public List<Order> createYearReport(LocalDate startDate, LocalDate endDate, Integer orderStatus);

	public Order getOrderById(Integer orderId);

	public List<BuyReportProductLine> createBuyReport(LocalDate startDate, LocalDate endDate, Integer orderStatus);
}
