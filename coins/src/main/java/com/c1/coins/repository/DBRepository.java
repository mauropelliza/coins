package com.c1.coins.repository;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.c1.coins.model.LineOrder;
import com.c1.coins.model.Order;
import com.c1.coins.model.Product;
import com.c1.coins.model.ProductFull;
import com.c1.coins.model.User;
import com.c1.coins.utils.StringMapExtractor;
import com.c1.coins.utils.Utils;
import com.c1.coins.utils.VisibilityEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Repository
public class DBRepository {
	
	@Autowired
	private JdbcTemplate jdbc;
	
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
		
		List<Integer> idList = jdbc.queryForList(query, Integer.class);
		
		for(Integer id : idList) {
			result.add(getOrderById(id));
		}
		
		return result;
	}
	
	public Integer countProductsById(Integer productId) {
		String query = "SELECT count(0) FROM wp_posts p WHERE p.ID = " + productId;
		return jdbc.queryForObject(query, Integer.class);
	}

	public Order getOrderById(Integer id) {
		String query = "SELECT * FROM wp_posts p WHERE p.ID = " + id;
		Order order = jdbc.query(query, new ResultSetExtractor<Order>() {

			@Override
			public Order extractData(ResultSet rs) throws SQLException, DataAccessException {
				Order o =new Order();
				if(!rs.next())
					return o;
				
		        o.setId(rs.getInt("ID"));  
		        String stringDate = rs.getString("post_date");
		        o.setDate(Utils.parseToLocalDateTime(stringDate));  
		        o.setStatus(rs.getString("post_status"));  
		        return o;  
			}
			
		});
		
		List<LineOrder> lines = retrieveLineOrders(id);
		if (!lines.isEmpty()) {
			order.setLines(lines);
		}
		order.addMeta(retrieveOrderMetadata(id));

		order.setUser(retrieveUser(order.getIdUser()));

		
		return order;
	}

	public Map<String, String> retrieveOrderMetadata(Integer orderId) {
		String query = "SELECT * FROM wp_postmeta WHERE post_id = " + orderId;
		return jdbc.query(query, new StringMapExtractor());
	}

	public User retrieveUser(Integer userId) {
		User user = users.get(userId);
		if (user != null) {
			// si ya estaba en la lista se devuelve
			return user;
		}
		
		//si no estaba buscamos toda la info de ese user
		//datos principales
		String query = "SELECT ID, user_login,user_email,display_name FROM wp_users WHERE ID =" + userId;
		user = jdbc.query(query, new ResultSetExtractor<User>() {
			@Override
			public User extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (!rs.next()) {
					return null;
				}
				User us = new User();
				us.setId(userId);
				us.setDisplayName(rs.getString("display_name"));
				us.setEmail(rs.getString("user_email"));
				
				return us;
			}	
		});
		//metadatos
		String metadataQuery = "SELECT * FROM wp_usermeta WHERE user_id =" + userId;
		Map<String, String> userMetadata = jdbc.query(metadataQuery, new StringMapExtractor());

		for (Map.Entry<String, String> entry : userMetadata.entrySet()) {
			user.addMeta(entry.getKey(), entry.getValue());
		}

		users.put(userId, user);
		return user;
	}

	public List<LineOrder> retrieveLineOrders(Integer orderId) {
		
		String wcQuery = "SELECT order_id, order_item_id, order_item_name FROM wp_woocommerce_order_items WHERE order_id="
						+ orderId;
		List<LineOrder> lines = jdbc.query(wcQuery, new RowMapper<LineOrder>() {
			@Override
			public LineOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
				LineOrder line = new LineOrder();
				line.setProduct(rs.getString("order_item_name"));
				line.setProductIdNumber(Integer.getInteger(rs.getString("order_item_id")));
				return line;
			}	
		});
		
		for(LineOrder line : lines) {
			if(line.getProductIdNumber() == null) {
				System.out.println(line.getProduct() + " no tiene id de producto, no se puede obtener metadata");
			} else {
				String wcMetaQuery = "SELECT * FROM wp_woocommerce_order_itemmeta WHERE order_item_id=" + line.getProductIdNumber();
				Map<String, String> lineMetadata = jdbc.query(wcMetaQuery, new StringMapExtractor());

				for (Map.Entry<String, String> entry : lineMetadata.entrySet()) {
					line.addMeta(entry.getKey(), entry.getValue());
				}
			}
			
			Product product = this.productsMap.get(line.getProduct().toUpperCase());
			if (product != null) {
				line.setProductCoinsInCatalog(product.getCoins());
				line.setProductUsdInCatalog(product.getUsd());
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

	public void setStock(String stock, Integer productId) {
		String update = "update `wp_postmeta` set `meta_value` = '" + stock + "' where `meta_key` = '_stock_status' "
				+ " and `post_id` = " + productId;
		int rowsAffected = jdbc.update(update);
		System.out.println("se actualizo el stock de " + rowsAffected + " filas !");
	}

	public void updateProductDates(Integer productId) {
		String time = Utils.getNowLocalDateTimeString();
		String update = "update `wp_posts` set `post_modified` = '" + time + "' , `post_modified_gmt` = '" + time
				+ "' where `ID` = " + productId;
		int rowsAffected = jdbc.update(update);
		System.out.println("se actualizaron las fechas de " + rowsAffected + " filas !");
	}
	
	public void updateTermMetaCounters(Integer productId, String visibility) {
		String findCategory = "SELECT t.term_id FROM `wp_term_relationships` r " + 
				"inner join `wp_term_taxonomy` t on r.term_taxonomy_id = t.term_id " + 
				"where r.object_id = " + productId + " and t.taxonomy = 'product_cat'";
		System.out.println(findCategory);
		Integer termId = jdbc.queryForObject(findCategory, Integer.class);
		System.out.println("El term id de la categoria del producto es: " + termId);
		
		String getCounter = "SELECT `meta_value` FROM `wp_termmeta` WHERE `term_id` = " + termId
				+ " AND `meta_key` = 'product_count_product_cat'";
		System.out.println(getCounter);
		Integer counter = jdbc.queryForObject(getCounter, Integer.class);
		System.out.println("El contador de la categoria del producto es: " + counter);
		
		String updateCounter = "update `wp_termmeta` set `meta_value` = " + getUpdatedTermMetaCounter(counter,visibility) + " WHERE `term_id` = "
				+ termId  + " AND `meta_key` = 'product_count_product_cat'";
		System.out.println(updateCounter);
		int rowsAffected = jdbc.update(updateCounter);
		System.out.println("se actualizo el contador de " + rowsAffected + " filas !");
	}
	
	
	private Integer getUpdatedTermMetaCounter(Integer counter, String visibility) {
		if(VisibilityEnum.HIDDEN.getValue().equalsIgnoreCase(visibility)) {
			return counter - 1;
		} else {
			return counter + 1;
		}
		
	}
	
	private Integer getTermTaxonomyMetaCounter(Integer counter, String visibility) {
		if(VisibilityEnum.HIDDEN.getValue().equalsIgnoreCase(visibility)) {
			return counter + 1;
		} else {
			return counter - 1;
		}
		
	}

	public void toggleSearcheability(Integer productId, String visibility) {
		if(VisibilityEnum.VISIBLE.getValue().equalsIgnoreCase(visibility)) {
			deleteRelation(productId, 6);
			deleteRelation(productId, 7);
			deleteRelation(productId, 9);
		} else {
			insertRelation(productId, 6);
			insertRelation(productId, 7);
			insertRelation(productId, 9);
		}
	}
	
	private void deleteRelation (Integer productId, Integer termTaxonomyId) {
		if (!searchForRelation(productId, termTaxonomyId))
			return;
		
		String deleteIfExist = "delete from wp_term_relationships where object_id = " + 
				productId +  " and term_taxonomy_id = " + termTaxonomyId;
		System.out.println(deleteIfExist);
		int rowsAffected = jdbc.update(deleteIfExist);
		System.out.println("se borraron " + rowsAffected + " filas de wp_term_relationships!");
		
		updateTermTaxonomyCounters(productId, termTaxonomyId, VisibilityEnum.VISIBLE.getValue());
	}
	
	private void insertRelation (Integer productId, Integer termTaxonomyId) {
		if (searchForRelation(productId, termTaxonomyId))
			return;
		
		String insertRestrictions = "insert into wp_term_relationships VALUES " +
		"(" + productId + ", " + termTaxonomyId + ",0)";
		System.out.println(insertRestrictions);
		int rowsInserted = jdbc.update(insertRestrictions);
		System.out.println("se insertaron " + rowsInserted + " filas en wp_term_relationships!");
		
		updateTermTaxonomyCounters(productId, termTaxonomyId, VisibilityEnum.HIDDEN.getValue());
	}
		
	private boolean searchForRelation(Integer productId, Integer termTaxonomyId) {
		String count = "SELECT count(0) FROM `wp_term_relationships` WHERE `object_id` = " + productId
				+ " AND `term_taxonomy_id` = " + termTaxonomyId;
		System.out.println(count);
		Integer counter = jdbc.queryForObject(count, Integer.class);
		System.out.println("Cantidad de filas de wp_term_relationships que coinciden: " + counter);
		
		return counter == null || counter < 1 ? false : true;
	}
	
	public void updateTermTaxonomyCounters(Integer productId, Integer termTaxonomyId, String visibility) {
		String getCount = "SELECT `count` FROM `wp_term_taxonomy` WHERE `term_taxonomy_id` = " + termTaxonomyId
				+ " AND `term_id` = " + termTaxonomyId + " AND `taxonomy` = 'product_visibility'";
		System.out.println(getCount);
		Integer counter = jdbc.queryForObject(getCount, Integer.class);
		System.out.println(counter);
		
		String updateCount = "UPDATE `wp_term_taxonomy` SET `count` = " + getTermTaxonomyMetaCounter(counter,visibility) +
		" WHERE `term_taxonomy_id` = " + termTaxonomyId + " AND `term_id` = " + termTaxonomyId +
		" AND `taxonomy` = 'product_visibility'";
		System.out.println(updateCount);
		Integer rows = jdbc.update(updateCount);
		System.out.println("Cantidad de filas actualizadas: " + rows);
	}
	
	public List<ProductFull> getProductsFromWoo() {
		String query = "SELECT DISTINCT p.id ID, p.post_title TITLE, pm.meta_value STOCK, pm2.meta_value COINS, " +
		"case when (select count(1) from wp_term_relationships rel where rel.term_taxonomy_id in (6,7,9) and rel.object_id = p.id) > 0 " +
				"then 'SI' else 'NO' END AS VISIBLE, csv.coins CSVCOINS,  CONCAT(csv.price,csv.currency,csv.currency_type) USD " +
	    "FROM wp_posts p LEFT JOIN wp_postmeta pm ON p.id = pm.post_id " +
	    "LEFT JOIN wp_postmeta pm2 ON p.id = pm2.post_id " +
	    "WHERE p.post_type = 'product' and pm.meta_key = '_stock_status' and pm2.meta_key = '_price'";
		
		List<ProductFull> productos = jdbc.query(query, new RowMapper<ProductFull>() {
			@Override
			public ProductFull mapRow(ResultSet rs, int rowNum) throws SQLException {
				ProductFull product = new ProductFull();
				product.setId(rs.getInt("ID"));
				product.setTitle(rs.getString("TITLE"));
				product.setStock(rs.getString("STOCK"));
				product.setDbCoins(rs.getString("COINS"));
				product.setVisible(rs.getString("VISIBLE"));
				product.setCsvCoins(rs.getString("CSVCOINS"));
				product.setCsvUsd(rs.getString("USD"));
				
				return product;
			}
		}); 
		
		return productos;
	}

}
