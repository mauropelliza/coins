package com.c1.coins.model;

import static com.c1.coins.utils.Utils.sanitize;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

//wp_posts  post_type = shop_order
public class Order {

	private Integer id; // ID
	private LocalDateTime date; // ID
	private Map<String, String> meta = Maps.newHashMap();

	private List<LineOrder> lines = Lists.newArrayList();
	private String status;
	private User user;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LocalDateTime getTimeStamp() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public List<LineOrder> getLines() {
		return lines;
	}

	public void setLines(List<LineOrder> lines) {
		this.lines = lines;
		lines.stream().forEach(l -> l.setParentOrder(getCloneWithoutLines()));
	}

	// frenamos la recursividad
	private Order getCloneWithoutLines() {
		Order clone = new Order();
		clone.setId(this.id);
		clone.setDate(this.date);
		clone.setStatus(this.status);
		clone.setUser(this.user);
		
		return clone;
	}

	public void addMeta(String key, String value) {
		this.meta.put(key, value);
	}

	public Integer getOrdertotal() {
		String orderTotal = this.meta.get("_order_total");
		return orderTotal == null ? null : Integer.valueOf(orderTotal);
	}

	public Integer getIdUser() {
		String userId = this.meta.get("_customer_user");
		if (userId == null) {
			return null;
		}
		return Integer.valueOf(userId);
	}

	public void setUser(User user) {
		this.user = user;

	}

	public User getUser() {
		return user;
	}

	@Override
	public String toString() {
		return "Id: " + this.getId() + " Date: " + sanitize(this.getTimeStamp()) + " User: "
				+ sanitize(this.getUserName()) + " total: " + sanitize(this.getOrdertotal()) + " Status:"
				+ sanitize(this.getStatus());
	}

	public String getUserName() {
		return this.getUser() == null ? null : this.getUser().getDisplayName();
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;

	}

	public void addMeta(Map<String, String> meta) {
		if (meta != null) {
			meta.entrySet().forEach(e -> this.meta.put(e.getKey(), e.getValue()));
		}
	}

	
}
