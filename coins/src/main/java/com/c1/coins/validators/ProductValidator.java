package com.c1.coins.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.c1.coins.repository.DBRepository;
import com.c1.coins.utils.VisibilityEnum;

@Component
public class ProductValidator {
	
	@Autowired
	private DBRepository dbRepository;
	
	public void validateVisibility(String visibility) {
		if(!(VisibilityEnum.VISIBLE.getValue().equalsIgnoreCase(visibility) 
				|| VisibilityEnum.HIDDEN.getValue().equalsIgnoreCase(visibility))) {
			throw new DataValidationException("solo se admiten los valores HIDDEN y VISIBLE");
		}
	}

	public void validateProductExistance(Integer productId) {
		Integer result = dbRepository.countProductsById(productId);
		if(result == null || result < 1) {
			throw new DataValidationException("el id de producto no es válido");
		}
	}
}
