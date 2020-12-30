package com.c1.coins.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.c1.coins.model.ProductDetail;
import com.c1.coins.model.ProductPrice;

public interface ProductsService {
	public List<ProductPrice> getProductPrices();

	public List<Object> getAllProductsFromWooc();

	public void setVisibility(Integer productId, String visibility);
	
	public List<ProductDetail> getProductsFromWoo();

	public String getProductsCsvFromWoo();

	public List<String> bulkUpsert(MultipartFile file);
	
	public void comparar();
}
