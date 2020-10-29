package com.c1.coins.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.c1.coins.model.Order;
import com.c1.coins.repository.DBRepository;
import com.c1.coins.service.OrdersService;
import com.c1.coins.utils.Utils;
import com.c1.coins.validators.DateValidator;

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
}
