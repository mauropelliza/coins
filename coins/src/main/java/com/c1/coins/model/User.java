package com.c1.coins.model;

import java.util.Map;

import com.c1.coins.utils.Utils;
import com.google.common.collect.Maps;

//wp_users
public class User {
	private Integer id;
	private String displayName;
	private String email;
	private Map<String, String> meta = Maps.newHashMap();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getUser_email() {
		return email;
	}

	public void setEmail(String user_email) {
		this.email = user_email;
	}

	public void addMeta(String key, String value) {
		this.meta.put(key, value);
	}

	public String getBillingEmail() {
		return this.meta.get("billing_email");
	}

	public String getBillingCity() {
		return this.meta.get("billing_city");
	}

	public Double getSpentCoins() {
		return Utils.toDouble(this.meta.get("mycred_default"));
	}

	@Override
	public String toString() {
		return displayName;
	}

	public Double getAccumulatedCoins() {
		return Utils.toDouble(this.meta.get("initial_points"));
	}

	public void setMetadata(Map<String, String> userMetadata) {
		this.meta.clear();
		this.meta.putAll(userMetadata);
	}

}
