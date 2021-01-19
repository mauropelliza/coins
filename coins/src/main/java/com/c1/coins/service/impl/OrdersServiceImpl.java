package com.c1.coins.service.impl;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.c1.coins.model.BuyReportDetailLine;
import com.c1.coins.model.BuyReportLine;
import com.c1.coins.model.LineOrder;
import com.c1.coins.model.LogCanjeByUser;
import com.c1.coins.model.Order;
import com.c1.coins.model.OrderByUserNameComparator;
import com.c1.coins.model.User;
import com.c1.coins.report.excel.ExcelSheet;
import com.c1.coins.report.excel.ExcelWorkbook;
import com.c1.coins.report.excel.buyreport.GiftCardReportExporter;
import com.c1.coins.report.excel.buyreport.LineOrderReportExporter;
import com.c1.coins.report.excel.buyreport.ProductReportExporter;
import com.c1.coins.repository.DBRepository;
import com.c1.coins.service.OrdersService;
import com.c1.coins.utils.Utils;
import com.c1.coins.validators.DateValidator;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Service
public class OrdersServiceImpl implements OrdersService {

	@Autowired
	private DBRepository dBRepository;

	@Autowired
	private DateValidator dateValidator;

	@Override
	public List<Order> createYearReport(LocalDate startDate, LocalDate endDate, Integer orderStatus) {
		if (dateValidator.areDatesEmpty(startDate, endDate)) {
			startDate = Utils.getFirstDayOfYear();
			endDate = Utils.getLastDayOfYear();
		}
		dateValidator.validateDateRange(startDate, endDate);

		return dBRepository.getOrdersBetweenDates(startDate, endDate, orderStatus);
	}

	@Override
	public Order getOrderById(Integer orderId) {
		// TODO Auto-generated method stub
		return dBRepository.getOrderById(orderId);
	}

	@Override
	public List<LineOrder> getLineOrders(LocalDate startDate, LocalDate endDate, Integer orderStatus) {

		List<LineOrder> lineOrders = Lists.newArrayList();
		List<String> orderStatusToConsider = Lists.newArrayList("wc-completed", "wc-processing");
		dBRepository.getOrdersBetweenDates(startDate, endDate, null).stream().forEach(order -> {
			System.out.println(order.getId() + " " + order.getUserName());
			if (orderStatusToConsider.contains(order.getStatus())) {
				lineOrders.addAll(order.getLines());
			}
		});
		lineOrders.sort(new OrderByUserNameComparator());
		return lineOrders;
	}
	
	@Override
	public ExcelWorkbook createBuyReportExcel(LocalDate startDate, LocalDate endDate, Integer orderStatus) {
		List<LineOrder> lineOrders = this.getLineOrders(startDate, endDate, orderStatus);
		List<BuyReportLine> lines = toBuyOrderReportLines(lineOrders);
		List<BuyReportLine> giftcardLines = lines.stream()
				.filter(line -> line.getProduct().toUpperCase().contains("GIFT CARD")).collect(Collectors.toList());
		List<BuyReportLine> buyProductLines = Lists.newArrayList(lines);
		buyProductLines.removeAll(giftcardLines);

		if (lines.size() != buyProductLines.size() + giftcardLines.size()) {
			throw new RuntimeException(
					"La cantidad de lineas totales con concuerda con la suma de lineas de producto + lineas de giftcard");
		}

		StringBuilder w = new StringBuilder();
		// Collections.sort(orders, new OrderByNameInListComparator(getNamesOrder()));
		ExcelWorkbook book = new ExcelWorkbook();
		ExcelSheet ordersSheet = book.addSheet("Ordenes");
		LineOrderReportExporter lineOrderExporter = new LineOrderReportExporter();
		lineOrderExporter.export(lineOrders, ordersSheet);
		
		ExcelSheet giftSheet = book.addSheet("Gift Cards");
		GiftCardReportExporter giftcardExporter = new GiftCardReportExporter();
		giftcardExporter.export(giftcardLines, giftSheet);
		
		ExcelSheet productsSheet = book.addSheet("Products");
		ProductReportExporter productExporter = new ProductReportExporter();
		productExporter.export(buyProductLines, productsSheet);
		
		return book;
	}

	private static List<LogCanjeByUser> toLogOrderReportLines(List<LineOrder> lineOrders) {
		Map<User, LogCanjeByUser> map = Maps.newLinkedHashMap();
		for (LineOrder line : lineOrders) {
			User user = line.getParentOrder().getUser();
			LogCanjeByUser buyOrderLine = map.get(user);
			if (buyOrderLine == null) {
				buyOrderLine = new LogCanjeByUser(user);
				map.put(user, buyOrderLine);
			}
			buyOrderLine.addLineOrder(line);
		}

		return Lists.newArrayList(map.values());
	}

	private static List<BuyReportLine> toBuyOrderReportLines(List<LineOrder> lineOrders) {
		Map<Integer, BuyReportLine> map = Maps.newLinkedHashMap();
		for (LineOrder line : lineOrders) {
			BuyReportLine buyOrderLine = map.get(line.getProductId());
			if (buyOrderLine == null) {
				buyOrderLine = new BuyReportLine();
				buyOrderLine.setProduct(line.getProductName());
				buyOrderLine.setProductPrice(line.getProductUsdInCatalog());
				buyOrderLine.setProductCoins(line.getProductCoins());
				buyOrderLine.setCurrency(line.getProductCurrencyInCatalog());
				map.put(line.getProductId(), buyOrderLine);
			}
			buyOrderLine.addQuantity(line.getQuantity());
			buyOrderLine.addRequester(new BuyReportDetailLine(line));
		}

		List<BuyReportLine> lines = Lists.newArrayList(map.values());
		Collections.sort(lines, new Comparator<BuyReportLine>() {

			@Override
			public int compare(BuyReportLine line1, BuyReportLine line2) {
				return line1.getProduct().compareTo(line2.getProduct());
			}
		});

		return lines;
	}

}
