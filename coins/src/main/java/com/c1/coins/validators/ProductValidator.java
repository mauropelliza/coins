package com.c1.coins.validators;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.c1.coins.repository.DBRepository;
import com.c1.coins.utils.CsvActionsEnum;
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
	
	public void validateVisible(String visibility) {
		if(!("SI".equalsIgnoreCase(visibility) || "NO".equalsIgnoreCase(visibility))) {
			throw new DataValidationException("solo se admiten los valores SI y NO");
		}
	}

	public void validateProductExistance(Integer productId) {
		Integer result = dbRepository.countProductsById(productId);
		if(result == null || result < 1) {
			throw new DataValidationException("el id de producto no es válido");
		}
	}
	
	public void validateAction(String action) {
		if(StringUtils.isNotBlank(action) && 
				action.equalsIgnoreCase(CsvActionsEnum.CREAR.getValue()) &&
				action.equalsIgnoreCase(CsvActionsEnum.MODIFICAR.getValue())) {
			throw new DataValidationException("el tipo de acciòn no es válida");
		}
	}
}
