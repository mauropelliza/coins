package com.c1.coins.controller;

import static com.c1.coins.utils.Fields.ACCEPT_CSV;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.c1.coins.model.BuyReportLine;
import com.c1.coins.model.Order;
import com.c1.coins.report.BuyReportGiftCardSummaryLineSerializer;
import com.c1.coins.report.BuyReportProductSummaryLineSerializer;
import com.c1.coins.service.OrdersService;
import com.google.common.collect.Lists;

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

	@GetMapping(path = "/buy-report/csv", produces = MediaType.TEXT_PLAIN_VALUE)
	public @ResponseBody String createBuyReportCsv(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam(required = false) Integer orderStatus) throws IOException {
		List<BuyReportLine> lines = ordersService.createBuyReport(startDate, endDate, orderStatus);

		List<BuyReportLine> buyGiftcardLines = lines.stream()
				.filter(line -> line.getProduct().toUpperCase().contains("GIFT CARD")).collect(Collectors.toList());
		List<BuyReportLine> buyProductLines = Lists.newArrayList(lines);
		buyProductLines.removeAll(buyGiftcardLines);

		if (lines.size() != buyProductLines.size() + buyGiftcardLines.size()) {
			throw new RuntimeException(
					"La cantidad de lineas totales con concuerda con la suma de lineas de producto + lineas de giftcard");
		}

		StringBuilder w = new StringBuilder();
		// Collections.sort(orders, new OrderByNameInListComparator(getNamesOrder()));
		BuyReportProductSummaryLineSerializer buyReportLineransformer = new BuyReportProductSummaryLineSerializer();
		w.append(buyReportLineransformer.headerCSVLine());
		for (BuyReportLine line : buyProductLines) {
			w.append(buyReportLineransformer.toString(line));
		}

		BuyReportGiftCardSummaryLineSerializer buyGiftCardLineTransformer = new BuyReportGiftCardSummaryLineSerializer();
		w.append(buyGiftCardLineTransformer.headerCSVLine());
		for (BuyReportLine line : buyGiftcardLines) {
			w.append(buyGiftCardLineTransformer.toString(line));
		}

		return w.toString();
	}
	
	@GetMapping(path = "/buy-report/xsls/old", produces = MediaType.TEXT_PLAIN_VALUE)
	public @ResponseBody String createBuyReportExcelOld(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam(required = false) Integer orderStatus) throws IOException {
		List<BuyReportLine> lines = ordersService.createBuyReport(startDate, endDate, orderStatus);

		List<BuyReportLine> buyGiftcardLines = lines.stream()
				.filter(line -> line.getProduct().toUpperCase().contains("GIFT CARD")).collect(Collectors.toList());
		List<BuyReportLine> buyProductLines = Lists.newArrayList(lines);
		buyProductLines.removeAll(buyGiftcardLines);

		if (lines.size() != buyProductLines.size() + buyGiftcardLines.size()) {
			throw new RuntimeException(
					"La cantidad de lineas totales con concuerda con la suma de lineas de producto + lineas de giftcard");
		}

		StringBuilder w = new StringBuilder();
		// Collections.sort(orders, new OrderByNameInListComparator(getNamesOrder()));
		BuyReportProductSummaryLineSerializer buyReportLineransformer = new BuyReportProductSummaryLineSerializer();
		w.append(buyReportLineransformer.headerCSVLine());
		for (BuyReportLine line : buyProductLines) {
			w.append(buyReportLineransformer.toString(line));
		}

		BuyReportGiftCardSummaryLineSerializer buyGiftCardLineTransformer = new BuyReportGiftCardSummaryLineSerializer();
		w.append(buyGiftCardLineTransformer.headerCSVLine());
		for (BuyReportLine line : buyGiftcardLines) {
			w.append(buyGiftCardLineTransformer.toString(line));
		}

		return w.toString();
	}
	
	
	@GetMapping(path = "/buy-report/xsls", produces = MediaType.TEXT_PLAIN_VALUE)
	public @ResponseBody String createBuyReportExcel(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam(required = false) Integer orderStatus) throws IOException {
		List<BuyReportLine> lines = ordersService.createBuyReportExcel(startDate, endDate, orderStatus);

		List<BuyReportLine> buyGiftcardLines = lines.stream()
				.filter(line -> line.getProduct().toUpperCase().contains("GIFT CARD")).collect(Collectors.toList());
		List<BuyReportLine> buyProductLines = Lists.newArrayList(lines);
		buyProductLines.removeAll(buyGiftcardLines);

		if (lines.size() != buyProductLines.size() + buyGiftcardLines.size()) {
			throw new RuntimeException(
					"La cantidad de lineas totales con concuerda con la suma de lineas de producto + lineas de giftcard");
		}

		StringBuilder w = new StringBuilder();
		// Collections.sort(orders, new OrderByNameInListComparator(getNamesOrder()));
		BuyReportProductSummaryLineSerializer buyReportLineransformer = new BuyReportProductSummaryLineSerializer();
		for (BuyReportLine line : buyProductLines) {
			w.append(buyReportLineransformer.toString(line));
		}

		BuyReportGiftCardSummaryLineSerializer buyGiftCardLineTransformer = new BuyReportGiftCardSummaryLineSerializer();
		w.append(buyGiftCardLineTransformer.headerCSVLine());
		for (BuyReportLine line : buyGiftcardLines) {
			w.append(buyGiftCardLineTransformer.toString(line));
		}

		return w.toString();
	}
}
