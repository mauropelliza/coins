package com.c1.coins.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.c1.coins.model.Category;
import com.c1.coins.service.CategoryService;

@RestController
@RequestMapping(path = "/products/categories")
public class CategoryController {
	
	@Autowired
	private CategoryService categoryService;
	
	@GetMapping
	public List<Category> getAllCategories() {
		return categoryService.getAllCategories();
	}
}
