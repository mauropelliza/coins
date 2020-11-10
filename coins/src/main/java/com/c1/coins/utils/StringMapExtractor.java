package com.c1.coins.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class StringMapExtractor implements ResultSetExtractor<Map<String, String>>{

	@Override
	public Map<String, String> extractData(ResultSet rs) throws SQLException, DataAccessException {
		Map<String, String> map = new HashMap<String, String>();
		while (rs.next()) {
			map.put(rs.getString("meta_key"), rs.getString("meta_value"));
		}
		return map;
	}

}
