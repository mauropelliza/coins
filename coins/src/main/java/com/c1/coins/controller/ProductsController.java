package com.c1.coins.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.c1.coins.model.Product;
import com.c1.coins.model.ProductFull;
import com.c1.coins.service.ProductsService;

@RestController
@RequestMapping(path = "/products")
public class ProductsController {
	@Autowired
	private ProductsService productsService;
	
	@GetMapping
	public List<Product> getAllProducts() {
		return productsService.getAllProducts();
	}
	
	@GetMapping(path="/wooc")
	public List<Object> getAllProductsFromWooc() {
		return productsService.getAllProductsFromWooc();
	}
	
	@PutMapping(path="/visibility")
	public void setVisibility(@RequestParam Integer productId, @RequestParam String visibility) {
		productsService.setVisibility(productId, visibility);
	}
	
	@GetMapping(path="/report")
	public List<ProductFull> getProductsFromWoo(){
		return productsService.getProductsFromWoo();
	}
	
	@GetMapping(path="/report.csv")
	public String getProductsCsvFromWoo(){
		return productsService.getProductsCsvFromWoo();
	}
}
