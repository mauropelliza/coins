package com.c1.coins.repository;

import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;

public class Connection {

	private static Connection INSTANCE;

	private java.sql.Connection conn = null;
	@Value("${spring.datasource.url}")
	private String url;
	@Value("${spring.datasource.username}")
	private String userName;
	@Value("${spring.datasource.password}")
	private String password;

	static {
		try {
			System.out.println("Iniciando JDBC driver de mysql !!!\n");
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			System.out.println("Ocurrio un error al instanciar el driver de mysql !!!\n");
			throw new RuntimeException(e);	
		}
	}
	
	private Connection() {
		try {
			String connection = String.format("%s?user=%s&password=%s", 
					this.url, this.userName, this.password);
			this.conn = DriverManager.getConnection(connection);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static java.sql.Connection get() {
		if (INSTANCE == null) {
			INSTANCE = new Connection();
		}
		return INSTANCE.conn;
	}

	public static void close() {
		if (INSTANCE != null && INSTANCE.conn != null) {
			try {
				INSTANCE.conn.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
