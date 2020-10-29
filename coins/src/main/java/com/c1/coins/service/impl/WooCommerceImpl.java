package com.c1.coins.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.c1.coins.service.WooCommerceService;
import com.icoderman.woocommerce.ApiVersionType;
import com.icoderman.woocommerce.WooCommerce;
import com.icoderman.woocommerce.WooCommerceAPI;
import com.icoderman.woocommerce.oauth.OAuthConfig;

@Service
public class WooCommerceImpl implements WooCommerceService {
	@Value("${spring.api.client}")
	private String client_key;
	@Value("${spring.api.secret}")
	private String client_secret;
	
	@Override
	public WooCommerce getWooCommerce(String uri) {
		OAuthConfig config = new OAuthConfig(uri, client_key, client_secret); 
		return new WooCommerceAPI(config, ApiVersionType.V3);
	}
}
