package com.c1.coins.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.c1.coins.repository.DBRepository;
import com.c1.coins.utils.CsvActionsEnum;
import com.c1.coins.utils.VisibilityEnum;

@Component
public class ProductReportValidator {
	@Autowired
	private DBRepository dbRepository;
	
	public String validateVisibility(String visibility) {
		if(!(VisibilityEnum.VISIBLE.getValue().equalsIgnoreCase(visibility) 
				|| VisibilityEnum.HIDDEN.getValue().equalsIgnoreCase(visibility))) {
			return "Solo se admiten los valores HIDDEN y VISIBLE";
		}
		return StringUtils.EMPTY;
	}
	
	public String validateVisible(String visibility) {
		if(!("TRUE".equalsIgnoreCase(visibility.trim()) || "FALSE".equalsIgnoreCase(visibility.trim()))) {
			return "El campo \"Visible\" es obligatorio, sole se admiten los valores TRUE o FALSE";
		}
		return StringUtils.EMPTY;
	}

	public String validateProductExistance(Integer productId) {
		Integer result = dbRepository.countProductsById(productId);
		if(result == null || result < 1) {
			return "El id de producto no es válido";
		}
		return StringUtils.EMPTY;
	}
	
	public String validateAction(String action) {
		if(!CsvActionsEnum.CREAR.getValue().equalsIgnoreCase(action) &&
				!CsvActionsEnum.MODIFICAR.getValue().equalsIgnoreCase(action)) {
			return "El tipo de acción no es válida";
		}
		return StringUtils.EMPTY;
	}
}
