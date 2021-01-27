package com.c1.coins.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.function.HandlerFilterFunction;

import com.c1.coins.model.User;
import com.c1.coins.repository.DBRepository;
import com.c1.coins.service.UsersService;
import com.opencsv.stream.reader.LineReader;

@Service
public class UsersServiceImpl implements UsersService {

	@Autowired
	private DBRepository dBRepository;

	@Override
	public List<User> getUsers() {
		return dBRepository.retrieveUsers();
	}

	@Override
	public List<String> insertCoins(Reader file) throws IOException {
		LineReader r = new LineReader(new BufferedReader(file), true);
		String line = r.readLine();//salteamos la linea de headers
		List<String> errors = Lists.newArrayList();
		while ((line = r.readLine()) != null) {
			String[] parts = line.split(",");
			String user = parts[0];
			Integer coins = Integer.valueOf(parts[1]);
			try {
				dBRepository.changeUserCoins(user, coins);
			} catch (Exception e) {
				errors.add(e.getMessage());
			}
		}
		return errors;
	}

}
