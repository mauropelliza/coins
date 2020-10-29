package com.c1.coins.repository;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Repository;

import com.c1.coins.model.LineOrder;
import com.c1.coins.model.Order;
import com.c1.coins.model.Product;
import com.c1.coins.model.User;
import com.c1.coins.utils.Utils;
import com.c1.coins.validators.DBException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Repository
public class DBRepository {
	private Map<Integer, User> users = Maps.newHashMap();
	private Map<String, Product> productsMap;

	public DBRepository() {
		try {
			productsMap = loadProducts(new File("./products.csv"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public Map<String, Product> getProducts() {
		return productsMap;
	}

	public List<Order> getOrdersBetweenDates(LocalDate startDate, LocalDate endDate, Integer orderStatus) {
		List<Order> result = Lists.newArrayList();
		String queryOrder = orderStatus == null  || orderStatus == 0 ? "desc" : "asc";
		String query = String.format(
				"SELECT ID FROM wp_posts p WHERE p.post_type = 'shop_order' and post_date>='%s' and post_date<='%s' ORDER BY post_date " + queryOrder,
				Utils.getDBDateString(startDate, true) , Utils.getDBDateString(endDate, false));
		System.out.println(query);
		
		try (ResultSet rs = Connection.get().createStatement().executeQuery(query)) {

			while (rs.next()) {
				int id = rs.getInt("ID");
				Order order = getOrderById(id);
				if (order != null) {
					result.add(order);
				}

			}
		} catch (SQLException e) {
			throw new DBException("ocurrió un error al consultar la base de datos");
		}
		return result;
	}

	public Order getOrderById(Integer id) throws SQLException {
		Order order = new Order();
		String query = "SELECT * FROM wp_posts p WHERE p.ID = " + id;
		try (ResultSet rs = Connection.get().createStatement().executeQuery(query)) {
			if (!rs.next()) {
				return null;
			}

			String date = rs.getString("post_date");

			order.setId(id);
			order.setDate(LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			order.setStatus(rs.getString("post_status"));
			List<LineOrder> lines = retrieveLineOrders(id);
			if (!lines.isEmpty()) {
				order.setLines(lines);
			}
			order.addMeta(retrieveOrderMetadata(id));

			order.setUser(retrieveUser(order.getIdUser()));

		}
		return order;
	}

	public Map<String, String> retrieveOrderMetadata(Integer orderId) throws SQLException {
		Map<String, String> metadata = Maps.newHashMap();
		try (ResultSet rs = Connection.get().createStatement()
				.executeQuery("SELECT * FROM wp_postmeta WHERE post_id = " + orderId)) {
			while (rs.next()) {
				metadata.put(rs.getString("meta_key"), rs.getString("meta_value"));
			}
		}
		return metadata;
	}

	public User retrieveUser(Integer userId) throws SQLException {
		User user = users.get(userId);
		if (user != null) {
			return user;
		}

		try (ResultSet rs = Connection.get().createStatement()
				.executeQuery("SELECT ID, user_login,user_email,display_name FROM wp_users WHERE ID =" + userId)) {
			if (!rs.next()) {
				return null;
			}
			user = new User();
			user.setId(userId);
			user.setDisplayName(rs.getString("display_name"));
			user.setEmail(rs.getString("user_email"));
			try (ResultSet rsDetail = Connection.get().createStatement()
					.executeQuery("SELECT * FROM wp_usermeta WHERE user_id =" + userId)) {
				while (rsDetail.next()) {
					user.addMeta(rsDetail.getString("meta_key"), rsDetail.getString("meta_value"));
				}

			}
			users.put(userId, user);
			return user;
		}

	}

	public List<LineOrder> retrieveLineOrders(Integer orderId) throws SQLException {
		List<LineOrder> lines = Lists.newArrayList();
		try (ResultSet rs = Connection.get().createStatement().executeQuery(
				"SELECT order_id, order_item_id, order_item_name FROM wp_woocommerce_order_items WHERE order_id="
						+ orderId)) {

			while (rs.next()) {
				LineOrder line = new LineOrder();
				line.setProduct(rs.getString("order_item_name"));
				String orderItemId = rs.getString("order_item_id");
				try (ResultSet rsDetail = Connection.get().createStatement().executeQuery(
						"SELECT * FROM wp_woocommerce_order_itemmeta WHERE order_item_id=" + orderItemId)) {
					while (rsDetail.next()) {
						line.addMeta(rsDetail.getString("meta_key"), rsDetail.getString("meta_value"));
					}

				}
				Product product = this.productsMap.get(line.getProduct().toUpperCase());
				if (product != null) {
					line.setProductCoinsInCatalog(product.getCoins());
					line.setProductUsdInCatalog(product.getUsd());
				}
				lines.add(line);
			}
		}
		return lines;
	}

	private Map<String, Product> loadProducts(File productsFile) throws IOException {

		Map<String, Product> products = Maps.newLinkedHashMap();
		try (CSVParser csvParser = new CSVParser(new FileReader(productsFile), CSVFormat.RFC4180)) {
			for (CSVRecord record : csvParser.getRecords()) {
				Product product = new Product(record.get(0), record.get(1), record.get(2));
				System.out.println(product.getName());
				products.put(product.getName().toUpperCase(), product);
			}

		}
		return products;
	}

}
