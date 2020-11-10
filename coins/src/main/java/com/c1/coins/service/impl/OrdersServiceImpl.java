package com.c1.coins.service.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.c1.coins.model.BuyReportItemRequester;
import com.c1.coins.model.BuyReportProductOrderLine;
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
	public List<BuyReportProductOrderLine> createBuyReport(LocalDate startDate, LocalDate endDate, Integer orderStatus) {

		List<LineOrder> lineOrders = Lists.newArrayList();
		dBRepository.getOrdersBetweenDates(startDate, endDate, null).stream().forEach(order -> {
			System.out.println(order.getId() + " " + order.getUserName());
			if (order.getId() != null && order.getId() % 2 == 0) {
				lineOrders.addAll(order.getLines());
			}
		});
		lineOrders.sort(new OrderByUserNameComparator());

		List<LogCanjeByUser> logByUserLines = toLogOrderReportLines(lineOrders);
		
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

	private static List<BuyReportProductOrderLine> toBuyOrderReportLines(List<LineOrder> lineOrders) {
		Map<String, BuyReportProductOrderLine> map = Maps.newLinkedHashMap();
		for (LineOrder line : lineOrders) {
			BuyReportProductOrderLine buyOrderLine = map.get(line.getProductId());
			if (buyOrderLine == null) {
				buyOrderLine = new BuyReportProductOrderLine();
				buyOrderLine.setProduct(line.getProduct());
				buyOrderLine.setPrice(line.getProductUsdInCatalog());
			}
			buyOrderLine.addQuantity(line.getQuantity());

			buyOrderLine.addRequester(new BuyReportItemRequester(line.getParentOrder().getId(),
					line.getParentOrder().getUser(), line.getQuantity()));
			map.put(line.getProductId(), buyOrderLine);
		}

		return Lists.newArrayList(map.values());
	}

}
