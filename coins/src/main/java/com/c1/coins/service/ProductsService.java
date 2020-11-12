package com.c1.coins.service;

import java.util.List;

import com.c1.coins.model.Product;
import com.c1.coins.model.ProductFull;

public interface ProductsService {
	public List<Product> getAllProducts();

	public List<Object> getAllProductsFromWooc();

	public void setVisibility(Integer productId, String visibility);
	
	public List<ProductFull> getProductsFromWoo();

	public String getProductsCsvFromWoo();
}
