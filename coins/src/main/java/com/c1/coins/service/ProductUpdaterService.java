package com.c1.coins.service;

import com.c1.coins.model.CsvProduct;

public interface ProductUpdaterService {
	public String updateWooCommerceDB(CsvProduct update);
	
	public void hideProduct(Integer productId);
	
	public void showProduct(Integer productId);

	public Integer insertPostInWooCommerceDB(CsvProduct product); 
	
	public void insertPostMetadata(CsvProduct product);
	
	public void deletePost(Integer postId);
}
