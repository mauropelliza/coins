package com.c1.coins.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.c1.coins.model.Product;
import com.c1.coins.repository.DBRepository;
import com.c1.coins.service.ProductsService;
import com.c1.coins.service.WooCommerceService;
import com.icoderman.woocommerce.EndpointBaseType;
import com.icoderman.woocommerce.WooCommerce;

@Service
public class ProductsServiceImpl implements ProductsService {
	@Autowired
	private WooCommerceService wooCommerceService;
	
	@Autowired
	private DBRepository dBRepository;
	
	@Value("${spring.api.server}")
	private String woocommerceServer;
	
	@Override
	public List<Object> getAllProductsFromWooc() {

		// Setup client
		WooCommerce wooCommerce = wooCommerceService.getWooCommerce(woocommerceServer);

		// Get all with request parameters
		Map<String, String> params = new HashMap<>();
		params.put("per_page", "100");
		params.put("offset", "0");
		List products = wooCommerce.getAll(EndpointBaseType.ORDERS.getValue(), params);

		System.out.println("amount of products: " + products.size());

		return products;
	}
	
	@Override
	public List<Product> getAllProducts() {
		Map<String, Product> productMap = dBRepository.getProducts();
		if (productMap.isEmpty())
			return new ArrayList<Product>();
		
		return productMap.values().stream().collect(Collectors.toList());		
	}
}
