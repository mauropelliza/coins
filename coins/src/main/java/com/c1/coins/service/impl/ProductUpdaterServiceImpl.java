package com.c1.coins.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.c1.coins.model.CsvProduct;
import com.c1.coins.repository.DBRepository;
import com.c1.coins.service.ProductUpdaterService;
import com.c1.coins.utils.VisibilityEnum;

@Service
public class ProductUpdaterServiceImpl implements ProductUpdaterService {

	private final static String INSTOCK = "instock";
	private final static String OUTOFSTOCK = "outofstock";
	
	@Autowired
	private DBRepository dBRepository;
	
	@Transactional
	public String updateWooCommerceDB(CsvProduct update) {
			if (update.getVisible()) {
				showProduct(update.getId());
			} else {
				hideProduct(update.getId());
			}
			dBRepository.setProductName(update.getTitle(), update.getId());
			dBRepository.setProductDbCoins(update.getDbCoins(), update.getId());
			dBRepository.updateProductPrices(update);

		return StringUtils.EMPTY;
	}
	
	@Transactional
	public void insertPostMetadata(CsvProduct product) {
		dBRepository.insertPostMetadata(product.getId());
		dBRepository.insertProductPrices(product);
	}
	
	@Transactional
	public Integer insertPostInWooCommerceDB(CsvProduct update) {
		return dBRepository.insertPost(update);
	}
	
	public void deletePost(Integer postId) {
		dBRepository.deletePost(postId);
	}
	
	private String rollbackInsert(int level, Integer newId) {
		System.out.println("EJECUTANDO EL ROLLBACK !!!");
		try {
			if(level == 1) {
				dBRepository.deletePost(newId);
			} else if(level == 2) {
				dBRepository.deleteRelationship(newId);
				dBRepository.deletePost(newId);
			} else if(level == 3) {
				dBRepository.restoreTaxonomyCounter();
				dBRepository.deleteRelationship(newId);
				dBRepository.deletePost(newId);
			}
			
			return "OK";
		} catch (Exception e) {
			return "hubo un problema al revertir los cambios, la informacion quedo incosistente";
		}
	}
	
	@Override
	public void hideProduct(Integer productId) {
		dBRepository.setStock(OUTOFSTOCK, productId);
		dBRepository.updateProductDates(productId);
		dBRepository.updateTermMetaCounters(productId, VisibilityEnum.HIDDEN.getValue());
		dBRepository.toggleSearcheability(productId, VisibilityEnum.HIDDEN.getValue());
	}

	@Override
	public void showProduct(Integer productId) {
		dBRepository.setStock(INSTOCK, productId);
		dBRepository.updateProductDates(productId);
		dBRepository.updateTermMetaCounters(productId, VisibilityEnum.VISIBLE.getValue());
		dBRepository.toggleSearcheability(productId, VisibilityEnum.VISIBLE.getValue());
	}
}
