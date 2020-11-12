package com.c1.coins.service.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.c1.coins.csv.ProductToCsvSerializer;
import com.c1.coins.model.BuyReportItemRequester;
import com.c1.coins.model.BuyReportProductOrderLine;
import com.c1.coins.model.Product;
import com.c1.coins.model.ProductFull;
import com.c1.coins.repository.DBRepository;
import com.c1.coins.service.ProductsService;
import com.c1.coins.service.WooCommerceService;
import com.c1.coins.utils.VisibilityEnum;
import com.c1.coins.validators.ProductValidator;
import com.icoderman.woocommerce.EndpointBaseType;
import com.icoderman.woocommerce.WooCommerce;

@Service
public class ProductsServiceImpl implements ProductsService {
	
	private final static String INSTOCK = "instock";
	private final static String OUTOFSTOCK = "outofstock";
	
	@Autowired
	private WooCommerceService wooCommerceService;
	
	@Autowired
	private DBRepository dBRepository;
	
	@Value("${spring.api.server}")
	private String woocommerceServer;
	
	@Autowired
	private ProductValidator productValidator;
	
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

	@Override
	public void setVisibility(Integer productId, String visibility) {
		productValidator.validateVisibility(visibility);
		productValidator.validateProductExistance(productId);
		if(VisibilityEnum.VISIBLE.getValue().equalsIgnoreCase(visibility)) {
			showProduct(productId);
		} else {
			hideProduct(productId);
		}
	}

	@Transactional
	private void hideProduct(Integer productId) {
		dBRepository.setStock(OUTOFSTOCK, productId);
		dBRepository.updateProductDates(productId);
		dBRepository.updateTermMetaCounters(productId, VisibilityEnum.HIDDEN.getValue());
		dBRepository.toggleSearcheability(productId, VisibilityEnum.HIDDEN.getValue());
		
	}
	
	@Transactional
	private void showProduct(Integer productId) {
		dBRepository.setStock(INSTOCK, productId);
		dBRepository.updateProductDates(productId);
		dBRepository.updateTermMetaCounters(productId, VisibilityEnum.VISIBLE.getValue());
		dBRepository.toggleSearcheability(productId, VisibilityEnum.VISIBLE.getValue());
	}

	@Override
	public List<ProductFull> getProductsFromWoo() {
		return dBRepository.getProductsFromWoo();
	}

	@Override
	public String getProductsCsvFromWoo() {
		List<ProductFull> products = getProductsFromWoo();
		ProductToCsvSerializer serializer = new ProductToCsvSerializer();
		try (FileWriter fw = new FileWriter("./coinsComparativeReport.csv")) {
			fw.write(serializer.header());
			for (ProductFull line : products) {
				fw.write(serializer.toString(line));
			}

		} catch (IOException e) {
			return "No se pudo generar el reporte";
		} finally {
			System.out.println("End!");
		}

		return "El reporte coinsComparativeReport.csv fue generado con exito";
	}}
