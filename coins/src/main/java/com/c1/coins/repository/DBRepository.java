package com.c1.coins.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.c1.coins.model.Category;
import com.c1.coins.model.CsvProduct;
import com.c1.coins.model.LineOrder;
import com.c1.coins.model.Order;
import com.c1.coins.model.Product;
import com.c1.coins.model.ProductFull;
import com.c1.coins.model.User;
import com.c1.coins.utils.Fields;
import com.c1.coins.utils.StringMapExtractor;
import com.c1.coins.utils.Utils;
import com.c1.coins.utils.VisibilityEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

@Repository
public class DBRepository {

	@Autowired
	private JdbcTemplate jdbc;

	@Value("${post-basic-url}")
	private String postBasicUrl;

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
		String queryOrder = orderStatus == null || orderStatus == 0 ? "desc" : "asc";
		String query = String.format(
				"SELECT ID FROM wp_posts p WHERE p.post_type = 'shop_order' and post_date>='%s' and post_date<='%s' ORDER BY post_date "
						+ queryOrder,
				Utils.getDBDateString(startDate, true), Utils.getDBDateString(endDate, false));
		System.out.println(query);

		List<Integer> idList = jdbc.queryForList(query, Integer.class);

		for (Integer id : idList) {
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
				Order o = new Order();
				if (!rs.next())
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

	public List<User> retrieveUsers() {
		users.clear();

		// si no estaba buscamos toda la info de ese user
		// datos principales
		String query = "SELECT ID, user_login,user_email,display_name FROM wp_users order by ID";
		List<User> userList = jdbc.query(query, new RowMapper<User>() {

			@Override
			public User mapRow(ResultSet rs, int rowNumber) throws SQLException {
				User user = new User();
				Integer userId = rs.getInt("ID");
				user.setId(userId);
				user.setDisplayName(rs.getString("display_name"));
				user.setEmail(rs.getString("user_email"));
				System.out.println(userId + " " + user.getDisplayName());
				String metadataQuery = "SELECT * FROM wp_usermeta WHERE user_id =" + userId;
				Map<String, String> userMetadata = jdbc.query(metadataQuery, new StringMapExtractor());

				for (Map.Entry<String, String> entry : userMetadata.entrySet()) {
					user.addMeta(entry.getKey(), entry.getValue());
				}
				users.put(user.getId(), user);

				return user;

			}
		});
		// metadatos

		return userList;
	}

	public User retrieveUser(Integer userId) {
		User user = users.get(userId);
		if (user != null) {
			// si ya estaba en la lista se devuelve
			return user;
		}

		// si no estaba buscamos toda la info de ese user
		// datos principales
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
		// metadatos
		String metadataQuery = "SELECT * FROM wp_usermeta WHERE user_id =" + userId;
		Map<String, String> userMetadata = jdbc.query(metadataQuery, new StringMapExtractor());

		for (Map.Entry<String, String> entry : userMetadata.entrySet()) {
			user.addMeta(entry.getKey(), entry.getValue());
		}

		users.put(userId, user);
		return user;
	}

	public List<Category> getAllCategories() {
		String wcQuery = "select tm.term_id ID, tm.name NAME from wp_terms tm where tm.term_id in (SELECT distinct t.term_id "
				+ "FROM `wp_term_taxonomy` t where t.taxonomy = 'product_cat')";
		return jdbc.query(wcQuery, new RowMapper<Category>() {
			@Override
			public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
				Category cat = new Category();
				cat.setId(rs.getInt("ID"));
				cat.setName(rs.getString("NAME"));
				return cat;
			}
		});
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

		for (LineOrder line : lines) {
			if (line.getProductIdNumber() == null) {
				System.out.println(line.getProduct() + " no tiene id de producto, no se puede obtener metadata");
			} else {
				String wcMetaQuery = "SELECT * FROM wp_woocommerce_order_itemmeta WHERE order_item_id="
						+ line.getProductIdNumber();
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

	public Map<String, Product> loadProducts(File productsFile) throws IOException {

		Map<String, Product> products = Maps.newLinkedHashMap();
		//try (CSVParser csvParser = new CSVParser(new FileReader(productsFile), CSVFormat.RFC4180)) {
		try (Reader reader = new InputStreamReader(new FileInputStream(productsFile));) {
			CSVReader csvReader = Utils.getCsvReaderUsingSeparator(reader, ",");
			//for (CSVRecord record : csvParser.getRecords()) {
			String[] record = null;
			try {
				while ((record = csvReader.readNext()) != null) {
					Product product = new Product(record[0], record[2], record[1]);
					products.put(Utils.normalize(product.getName()), product);
				}
			} catch (CsvValidationException e) {
				throw new RuntimeException(e);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				throw new RuntimeException(e);
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

	public void setProductName(String productName, Integer productId) {
		String update = "update `wp_posts` set `post_title` = '" + productName + "',  `post_name` = '" 
				+ productName.replace(" ", "-") + "' where `post_type` = 'product' and `ID` = " + productId;
		int rowsAffected = jdbc.update(update);
		System.out.println("se actualizo el nombre de producto de " + rowsAffected + " filas !");
	}

	public void setProductDbCoins(String dbCoins, Integer productId) {
		String update = "update `wp_postmeta` set `meta_value` = '" + dbCoins + 
				"' where `meta_key` = '_price' and `post_id` = " + productId;
		
		int rowsAffected = jdbc.update(update);
		System.out.println("se actualizo el precio en coins de producto de " + rowsAffected + " filas !");		
	}

	public void updateProductDates(Integer productId) {
		String time = Utils.getNowLocalDateTimeString();
		String update = "update `wp_posts` set `post_modified` = '" + time + "' , `post_modified_gmt` = '" + time
				+ "' where `ID` = " + productId;
		int rowsAffected = jdbc.update(update);
		System.out.println("se actualizaron las fechas de " + rowsAffected + " filas !");
	}

	public void updateTermMetaCounters(Integer productId, String visibility) {
		String findCategory = "SELECT t.term_id FROM `wp_term_relationships` r "
				+ "inner join `wp_term_taxonomy` t on r.term_taxonomy_id = t.term_id where r.object_id = "
				+ productId + " and t.taxonomy = 'product_cat'";
		System.out.println(findCategory);
		List<Integer> termIds = jdbc.queryForList(findCategory, Integer.class);
		termIds.stream().forEach(t -> System.out.println("El term id de la categoria del producto es: " + t));

		for(Integer termId : termIds) {
			String getCounter = "SELECT `meta_value` FROM `wp_termmeta` WHERE `term_id` = " + termId
					+ " AND `meta_key` = 'product_count_product_cat'";
			System.out.println(getCounter);
			Integer counter = jdbc.queryForObject(getCounter, Integer.class);
			System.out.println("El contador de la categoria del producto es: " + counter);
	
			String updateCounter = "update `wp_termmeta` set `meta_value` = "
					+ getUpdatedTermMetaCounter(counter, visibility) + " WHERE `term_id` = " + termId
					+ " AND `meta_key` = 'product_count_product_cat'";
			System.out.println(updateCounter);
			int rowsAffected = jdbc.update(updateCounter);
			System.out.println("se actualizo el contador de " + rowsAffected + " filas !");
		}
	}

	private Integer getUpdatedTermMetaCounter(Integer counter, String visibility) {
		if (VisibilityEnum.HIDDEN.getValue().equalsIgnoreCase(visibility)) {
			return counter - 1;
		} else {
			return counter + 1;
		}

	}

	private Integer getTermTaxonomyMetaCounter(Integer counter, String visibility) {
		if (VisibilityEnum.HIDDEN.getValue().equalsIgnoreCase(visibility)) {
			return counter + 1;
		} else {
			return counter - 1;
		}

	}

	public void toggleSearcheability(Integer productId, String visibility) {
		if (VisibilityEnum.VISIBLE.getValue().equalsIgnoreCase(visibility)) {
			deleteRelation(productId, 6);
			deleteRelation(productId, 7);
			deleteRelation(productId, 9);
		} else {
			insertRelation(productId, 6);
			insertRelation(productId, 7);
			insertRelation(productId, 9);
		}
	}

	private void deleteRelation(Integer productId, Integer termTaxonomyId) {
		if (!searchForRelation(productId, termTaxonomyId))
			return;

		String deleteIfExist = "delete from wp_term_relationships where object_id = " + productId
				+ " and term_taxonomy_id = " + termTaxonomyId;
		System.out.println(deleteIfExist);
		int rowsAffected = jdbc.update(deleteIfExist);
		System.out.println("se borraron " + rowsAffected + " filas de wp_term_relationships!");

		updateTermTaxonomyCounters(termTaxonomyId, VisibilityEnum.VISIBLE.getValue());
	}

	private void insertRelation(Integer productId, Integer termTaxonomyId) {
		if (searchForRelation(productId, termTaxonomyId))
			return;

		String insertRestrictions = "insert into wp_term_relationships VALUES " + "(" + productId + ", "
				+ termTaxonomyId + ",0)";
		System.out.println(insertRestrictions);
		int rowsInserted = jdbc.update(insertRestrictions);
		System.out.println("se insertaron " + rowsInserted + " filas en wp_term_relationships!");

		updateTermTaxonomyCounters(termTaxonomyId, VisibilityEnum.HIDDEN.getValue());
	}

	private boolean searchForRelation(Integer productId, Integer termTaxonomyId) {
		String count = "SELECT count(0) FROM `wp_term_relationships` WHERE `object_id` = " + productId
				+ " AND `term_taxonomy_id` = " + termTaxonomyId;
		System.out.println(count);
		Integer counter = jdbc.queryForObject(count, Integer.class);
		System.out.println("Cantidad de filas de wp_term_relationships que coinciden: " + counter);

		return counter == null || counter < 1 ? false : true;
	}

	public void updateTermTaxonomyCounters(Integer termTaxonomyId, String visibility) {
		String getCount = "SELECT `count` FROM `wp_term_taxonomy` WHERE `term_taxonomy_id` = " + termTaxonomyId
				+ " AND `term_id` = " + termTaxonomyId + " AND `taxonomy` = 'product_visibility'";
		System.out.println(getCount);
		Integer counter = jdbc.queryForObject(getCount, Integer.class);
		System.out.println(counter);

		String updateCount = "UPDATE `wp_term_taxonomy` SET `count` = "
				+ getTermTaxonomyMetaCounter(counter, visibility) + " WHERE `term_taxonomy_id` = " + termTaxonomyId
				+ " AND `term_id` = " + termTaxonomyId + " AND `taxonomy` = 'product_visibility'";
		System.out.println(updateCount);
		Integer rows = jdbc.update(updateCount);
		System.out.println("Cantidad de filas actualizadas: " + rows);
	}

	public List<ProductFull> getProductsFromWoo() {
		String query = "SELECT DISTINCT p.id " + Fields.ID + ", p.post_title " + Fields.TITLE + ", pm.meta_value " + Fields.STOCK
				+ ", pm2.meta_value " + Fields.DBCOINS
				+ ", case when (select count(1) from wp_term_relationships rel where rel.term_taxonomy_id in (6,7,9) "
				+ "and rel.object_id = p.id) > 0 then FALSE else TRUE END AS " + Fields.VISIBLE 
				+ ", csv.coins " + Fields.CSVCOINS + ",  csv.price " + Fields.CSVUSD
				+ ", csv.currency " + Fields.CSVCURRENCY + ", csv.currency_type " + Fields.CSVCURRENCYTYPE
				+ " FROM wp_posts p LEFT JOIN wp_postmeta pm ON p.id = pm.post_id "
				+ "LEFT JOIN wp_postmeta pm2 ON p.id = pm2.post_id "
				+ "LEFT JOIN product_prices csv on p.id = csv.product_id "
				+ "WHERE p.post_type = 'product' and pm.meta_key = '_stock_status' and pm2.meta_key = '_price'";

		List<ProductFull> productos = jdbc.query(query, new RowMapper<ProductFull>() {
			@Override
			public ProductFull mapRow(ResultSet rs, int rowNum) throws SQLException {
				ProductFull product = new ProductFull();
				product.setId(rs.getInt(Fields.ID));
				product.setTitle(rs.getString(Fields.TITLE));
				//product.setStock(rs.getString(Fields.STOCK));// este campo no se va a usar por el momento
				product.setDbCoins(rs.getString(Fields.DBCOINS));
				product.setVisible(rs.getBoolean(Fields.VISIBLE));// si el campo es visible se asume que hay stock
				product.setCsvCoins(rs.getString(Fields.CSVCOINS));
				product.setCsvUsd(rs.getString(Fields.CSVUSD));
				product.setCsvCurrency(rs.getString(Fields.CSVCURRENCY));
				product.setCsvCurrencyType(rs.getString(Fields.CSVCURRENCYTYPE));

				return product;
			}
		});

		return productos;
	}
	
	public void updateProductPrices(CsvProduct product) {
		String update = "update product_prices set "
		+ "`product_name` = '" + product.getTitle() + "', `coins` = '" + product.getDbCoins() + "', "
		+ " `price` = '" + product.getCsvUsd() + "', `currency` = '" + product.getCsvCurrency() + "', "
		+ "`currency_type` = '" + product.getCsvCurrencyType() 
		+ "' where `product_id` = " + product.getId();
		System.out.println(update);
		int rowsUpdated = jdbc.update(update);
		System.out.println("se actualizaron " + rowsUpdated + " filas en product_prices!");
		
	}
	
	public void insertProductPrices(CsvProduct product) {
		String insert = "insert into product_prices (`product_id`, `product_name`, `coins` ,  `price` ,	`currency` " + 
				" ,	`currency_type`) values (" + product.getId() + ", '" + product.getTitle() + "', '" + product.getDbCoins() + 
				"', '" + product.getCsvUsd() + "', '" + product.getCsvCurrency() + "', '" + product.getCsvCurrencyType() + "')";
		System.out.println(insert);
		int rowsInserted = jdbc.update(insert);
		System.out.println("se insertaron " + rowsInserted + " filas en product_prices!");
	}
	
	public Integer insertPost (CsvProduct product) {
		String now = Utils.getNowLocalDateTimeString();
		String titulo = product.getTitle();
		
		String insertPost = "INSERT INTO `wp_posts` (`post_author`, `post_date`, `post_date_gmt`, `post_content`, `post_title`, "
		+ "`post_excerpt`, `post_status`, `comment_status`, `ping_status`, `post_password`, `post_name`, `to_ping`, `pinged`, "
		+ "`post_modified`, `post_modified_gmt`, `post_content_filtered`, `post_parent`, `guid`, `menu_order`, `post_type`, "
		+ " `post_mime_type`, `comment_count`) VALUES (1, '" + now + "', '" + now + "', '" + titulo + "', '" + titulo + "', "
		+ "'" + titulo + "', 'publish', 'open', 'closed', '', '" + titulo.replace(" ", "-") + "', '', '', '" + now + "', '" + now
		+ "', '', 0, " + "'', 0, 'product', '', 0)";
		System.out.println(insertPost);
		int rowsInserted = jdbc.update(insertPost);
		System.out.println("se insertaron " + rowsInserted + " filas en wp_posts!");
		
		return jdbc.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
	}

	public int insertPostMetadata(Integer newPostId) {
		
		String updateUrl = "UPDATE `wp_posts` SET `guid` = '" + postBasicUrl + newPostId + "' WHERE ID = " + newPostId;
		System.out.println(updateUrl);
		int updated = jdbc.update(updateUrl);
		System.out.println("se actualizo la url de " + updated + " filas en wp_posts!");
		
		String insertRelation = "INSERT INTO `wp_term_relationships` (`object_id`, `term_taxonomy_id`, `term_order`) VALUES " + 
				"(" + newPostId + ", 2, 0), (" + newPostId + ", 15, 0)";
		System.out.println(insertRelation);
		int rowsInserted = jdbc.update(insertRelation);
		System.out.println("se insertaron " + rowsInserted + " filas en wp_term_relationships!");
		
		insertWpPostMeta(newPostId);
		
		String update = "UPDATE `wp_term_taxonomy` TAX INNER JOIN `wp_term_taxonomy` TAX2 " + 
				"ON TAX.`term_taxonomy_id` = TAX2.`term_taxonomy_id` AND " + 
				"TAX.`term_id` = TAX2.`term_id` AND TAX.`taxonomy` = TAX2.`taxonomy` " + 
				"SET TAX.`count` = TAX2.`count` + 1 " + 
				"WHERE TAX.`term_taxonomy_id` = 2 AND TAX.`term_id` = 2 AND  TAX.`taxonomy` = 'product_type'";
		System.out.println(update);
		int rowsUpdated = jdbc.update(update);
		System.out.println("se Actualizaron " + rowsUpdated + " filas en wp_term_taxonomy !");

		String updateTax = "UPDATE `wp_term_taxonomy` TAX INNER JOIN `wp_term_taxonomy` TAX2 " + 
				"ON TAX.`term_taxonomy_id` = TAX2.`term_taxonomy_id` AND " + 
				"TAX.`term_id` = TAX2.`term_id` AND TAX.`taxonomy` = TAX2.`taxonomy` " + 
				"SET TAX.`count` = TAX2.`count` + 1 " + 
				"WHERE TAX.`term_taxonomy_id` = 15 AND TAX.`term_id` = 15 AND  TAX.`taxonomy` = 'product_cat'";
		System.out.println(updateTax);
		int taxUpdated = jdbc.update(updateTax);
		System.out.println("se Actualizaron " + taxUpdated + " filas en wp_term_taxonomy !");
		createStockAndVisibility(newPostId);
		
		return 0;
	}

	public void deletePost(Integer newId) {
		String deletePost = "DELETE FROM `wp_posts` WHERE `ID` = " + newId;
		System.out.println(deletePost);
		int rowsDeleted = jdbc.update(deletePost);
		System.out.println("SE BORRÓ " + rowsDeleted + " POST!");
	}

	public void deleteRelationship(Integer newId) {
		String deleteRelation = "DELETE FROM `wp_term_relationships` WHERE `object_id` = " + newId +
				" AND `term_taxonomy_id` = 2 ";
		System.out.println(deleteRelation);
		int rowsDeleted = jdbc.update(deleteRelation);
		System.out.println("se borraron " + rowsDeleted + " filas en wp_term_relationships!");
	}

	public void restoreTaxonomyCounter() {
		String update = "UPDATE `wp_term_taxonomy` TAX INNER JOIN `wp_term_taxonomy` TAX2 " + 
				"ON TAX.`term_taxonomy_id` = TAX2.`term_taxonomy_id` AND " + 
				"TAX.`term_id` = TAX2.`term_id` AND TAX.`taxonomy` = TAX2.`taxonomy` " + 
				"SET TAX.`count` = TAX2.`count` - 1 " + 
				"WHERE TAX.`term_taxonomy_id` = 2 AND TAX.`term_id` = 2 AND  TAX.`taxonomy` = 'product_type'";
		System.out.println(update);
		int rowsUpdated = jdbc.update(update);
		System.out.println("se restauro el valor de " + rowsUpdated + " filas en wp_term_taxonomy !");
	}

	public void createStockAndVisibility(Integer newId) {
		String insert = "insert `wp_postmeta` (`meta_value`, `meta_key`, `post_id`) " +
				"VALUES ('instock', '_stock_status', "+ newId + ")";
		System.out.println(insert);
		int rowsInserted = jdbc.update(insert);
		System.out.println("se creo el registro de STOCK para " + rowsInserted + " !");
		
		String updateCounter = "update `wp_termmeta` META1 INNER JOIN `wp_termmeta` META2 "
				+ " ON META1.`term_id` = META2.`term_id` AND META1.`meta_key` = META2.`meta_key` "
				+ " set META1.`meta_value` =  META2.`meta_value` + 1 "
				+ " WHERE META1.`term_id` = 15 AND META1.`meta_key` = 'product_count_product_cat'";
		System.out.println(updateCounter);
		int rowsAffected = jdbc.update(updateCounter);
		System.out.println("se actualizo el contador de " + rowsAffected + " filas !");		
	}
	
	public void insertWpPostMeta(Integer postId) {
		String update = "insert wp_postmeta (post_id, meta_key, meta_value ) values " 
			+ "(" + postId + ", '_edit_lock','1608314587:1'), (" + postId + ", '_edit_last','1'), (" + postId + ", '_sku', null), " 
			+ "(" + postId + ", '_regular_price','500'), (" + postId + ", '_sale_price','450'), (" + postId + ", '_sale_price_dates_from', null), " 
			+ "(" + postId + ", '_sale_price_dates_to', null), (" + postId + ", 'total_sales','0'), (" + postId + ", '_tax_status','taxable'), " 
			+ "(" + postId + ", '_tax_class',null), (" + postId + ", '_manage_stock','no'), (" + postId + ", '_backorders','no'), " 
			+ "(" + postId + ", '_low_stock_amount',null), (" + postId + ", '_sold_individually','no'), (" + postId + ", '_weight',null), " 
			+ "(" + postId + ", '_length',null), (" + postId + ", '_width',null), (" + postId + ", '_height',null), (" + postId + ", '_upsell_ids','a:0:{}'), " 
			+ "(" + postId + ", '_crosssell_ids','a:0:{}'), (" + postId + ", '_purchase_note',null), (" + postId + ", '_default_attributes','a:0:{}'), " 
			+ "(" + postId + ", '_virtual','no'), (" + postId + ", '_downloadable','no'), (" + postId + ", '_product_image_gallery',null), " 
			+ "(" + postId + ", '_download_limit','-1'), (" + postId + ", '_download_expiry','-1'), (" + postId + ", '_stock',null), " 
			+ "(" + postId + ", '_product_version','3.5.6'), (" + postId + ", '_price','450')";
		System.out.println(update);
		int rowsAffected = jdbc.update(update);
		
		System.out.println("se inserto la información en postmeta para " + rowsAffected + " post !");
	}
}
