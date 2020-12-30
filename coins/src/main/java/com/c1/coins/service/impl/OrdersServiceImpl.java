package com.c1.coins.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.c1.coins.model.BuyReportProductLine;
import com.c1.coins.model.BuyReportProductLineDetail;
import com.c1.coins.model.LineOrder;
import com.c1.coins.model.LogCanjeByUser;
import com.c1.coins.model.Order;
import com.c1.coins.model.OrderByUserNameComparator;
import com.c1.coins.model.User;
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
	public List<BuyReportProductLine> createBuyReport(LocalDate startDate, LocalDate endDate, Integer orderStatus) {

		List<LineOrder> lineOrders = Lists.newArrayList();
		List<String> orderStatusToConsider = Lists.newArrayList("wc-completed", "wc-processing");
		dBRepository.getOrdersBetweenDates(startDate, endDate, null).stream().forEach(order -> {
			System.out.println(order.getId() + " " + order.getUserName());
			if (orderStatusToConsider.contains(order.getStatus())) {
				lineOrders.addAll(order.getLines());
			}
		});
		lineOrders.sort(new OrderByUserNameComparator());

		// List<LogCanjeByUser> logByUserLines = toLogOrderReportLines(lineOrders);

		return toBuyOrderReportLines(lineOrders);
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

	private static List<BuyReportProductLine> toBuyOrderReportLines(List<LineOrder> lineOrders) {
		Map<String, BuyReportProductLine> map = Maps.newLinkedHashMap();
		for (LineOrder line : lineOrders) {
			BuyReportProductLine buyOrderLine = map.get(line.getProductId());
			if (buyOrderLine == null) {
				buyOrderLine = new BuyReportProductLine();
				buyOrderLine.setProduct(line.getProductName());
				buyOrderLine.setPrice(line.getProductUsdInCatalog());
			}
			buyOrderLine.addQuantity(line.getQuantity());

			buyOrderLine.addRequester(new BuyReportProductLineDetail(line));
			map.put(line.getProductId(), buyOrderLine);
		}

		List<BuyReportProductLine> lines = Lists.newArrayList(map.values());
		Collections.sort(lines, new Comparator<BuyReportProductLine>() {

			@Override
			public int compare(BuyReportProductLine line1, BuyReportProductLine line2) {
				return line1.getProduct().compareTo(line2.getProduct());
			}
		});

		return lines;
	}

}
