package com.c1.coins.service;

import java.time.LocalDate;
import java.util.List;

import com.c1.coins.model.BuyReportLine;
import com.c1.coins.model.Order;
import com.c1.coins.report.excel.ExcelWorkbook;

public interface OrdersService {
	public List<Order> createYearReport(LocalDate startDate, LocalDate endDate, Integer orderStatus);

	public Order getOrderById(Integer orderId);

	public List<BuyReportLine> createBuyReport(LocalDate startDate, LocalDate endDate, Integer orderStatus);
	
	public ExcelWorkbook createBuyReportExcel(LocalDate startDate, LocalDate endDate, Integer orderStatus);
}
