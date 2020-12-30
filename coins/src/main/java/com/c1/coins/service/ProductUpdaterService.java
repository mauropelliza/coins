package com.c1.coins.service;

import com.c1.coins.model.ProductDetailWithAction;

public interface ProductUpdaterService {
	public String updateWooCommerceDB(ProductDetailWithAction update);
	
	public void hideProduct(Integer productId);
	
	public void showProduct(Integer productId);

	public Integer insertPostInWooCommerceDB(ProductDetailWithAction product); 
	
	public void insertPostMetadata(ProductDetailWithAction product);
	
	public void deletePost(Integer postId);
}
