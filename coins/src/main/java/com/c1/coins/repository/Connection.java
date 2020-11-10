package com.c1.coins.repository;

import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Connection {
	
	
	private java.sql.Connection sqlConnection = null;
	public String name;
	
	@Value("${spring.datasource.url}")
	private String url;
	@Value("${spring.datasource.username}")
	private String userName;
	@Value("${spring.datasource.password}")
	private String password;
	
	public Connection() {
		try {
			//String connection = String.format("%s?user=%s&password=%s", 
		//			this.url, this.userName, this.password);
		//	this.sqlConnection = DriverManager.getConnection(connection);
		} catch (Exception e) {
			
		}
	}

	public java.sql.Connection get() {
		return sqlConnection;
	}

	public void close() {
		if (sqlConnection != null) {
			try {
				sqlConnection.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static void loadDriver() {
		System.out.println("Iniciando JDBC driver de mysql !!!\n");
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			System.out.println("Ocurrio un error al instanciar el driver de mysql !!!\n");
			throw new RuntimeException(e);	
		}
	}
}
