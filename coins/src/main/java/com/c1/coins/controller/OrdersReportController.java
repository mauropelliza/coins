package com.c1.coins.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.c1.coins.model.Order;
import com.c1.coins.service.OrdersService;

@RestController
@RequestMapping(path="ordersReport")
public class OrdersReportController {

	@Autowired
	private OrdersService ordersService;
	
	@GetMapping
	public List<Order> createYearReport(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, 
			@RequestParam(required = false)  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate, 
			@RequestParam(required = false) Integer orderStatus) {
		return ordersService.createYearReport(startDate, endDate, orderStatus);
	}
}
