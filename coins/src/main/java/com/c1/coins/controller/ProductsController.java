package com.c1.coins.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.c1.coins.model.Product;
import com.c1.coins.model.ProductFull;
import com.c1.coins.service.ProductsService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping(path = "/products")
public class ProductsController {
	private static final String ACCEPT_CSV = "text/csv";
	private static final String ACCEPT_JSON = "application/json";
	
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
	
	@PostMapping(path="/report")
	public void getProductsFromWoo(HttpServletResponse response, @RequestHeader("Accept") String accept) throws IOException{
		// dependiendo del Accept header que se envie se devuelve la informacion en distintos formatos
		if(ACCEPT_CSV.equalsIgnoreCase(accept)) {
			String products = productsService.getProductsCsvFromWoo();
			response.setContentType(ACCEPT_CSV);
	        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
	                "attachment; filename=\"coinsComparativeReport.csv\"");
			response.getWriter().write(products);
		} else {
			// uso jackson para convertir la lista a un string de json
			ObjectMapper mapper = new ObjectMapper();
			List<ProductFull> productList = productsService.getProductsFromWoo();
			String jsonString = mapper.writeValueAsString(productList);
			response.setContentType(ACCEPT_JSON);
			response.getWriter().write(jsonString);
		} 
	}
	
	@PostMapping
	public void bulkUpsert(@RequestBody  MultipartFile productUpdates, HttpServletResponse response) throws IOException {
		List<String> results = productsService.bulkUpsert(productUpdates);
		response.setContentType(ACCEPT_CSV);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"productsUpsertResults.csv\"");
        StringBuffer sb = new StringBuffer();
        results.forEach((final String fila) -> sb.append(fila));
		response.getWriter().write(sb.toString());
	}
}
