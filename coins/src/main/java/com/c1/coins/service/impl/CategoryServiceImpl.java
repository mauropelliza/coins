package com.c1.coins.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.c1.coins.model.Category;
import com.c1.coins.repository.DBRepository;
import com.c1.coins.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private DBRepository dBRepository;
	
	@Override
	public List<Category> getAllCategories() {
		// TODO Auto-generated method stub
		return dBRepository.getAllCategories();
	}
}
