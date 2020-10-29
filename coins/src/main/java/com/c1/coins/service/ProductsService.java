package com.c1.coins.service;

import java.util.List;

import com.c1.coins.model.Product;

public interface ProductsService {
	public List<Product> getAllProducts();

	public List<Object> getAllProductsFromWooc();
}
