package com.c1.coins.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.c1.coins.model.BuyReportLine;
import com.c1.coins.model.Order;
import com.c1.coins.report.excel.ExcelWorkbook;
import com.c1.coins.service.OrdersService;

@RestController
@RequestMapping(path = "orders")
public class OrdersController {

	@Autowired
	private OrdersService ordersService;

	@GetMapping
	public List<Order> createYearReport(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam(required = false) Integer orderStatus) {
		return ordersService.createYearReport(startDate, endDate, orderStatus);
	}

	@GetMapping(path = "/{orderId}")
	public Order getOrderById(@PathVariable Integer orderId) {
		return ordersService.getOrderById(orderId);
	}

	@GetMapping(path = "/buy-report")
	public List<BuyReportLine> createBuyReport(HttpServletResponse response,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam(required = false) Integer orderStatus) throws IOException {
		List<BuyReportLine> lines = ordersService.createBuyReport(startDate, endDate, orderStatus);
		return lines;
	}

	@GetMapping(path = "/buy-report/xlsx", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<Resource> createBuyReportExcel(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam(required = false) Integer orderStatus) throws IOException {

		ExcelWorkbook excel = ordersService.createBuyReportExcel(startDate, endDate, orderStatus);
		ByteArrayResource resource = new ByteArrayResource(excel.toBytes());
		return ResponseEntity.ok().contentLength(resource.contentLength())
				.contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(resource);
	}
}
