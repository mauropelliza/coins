package com.c1.coins.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.c1.coins.model.User;
import com.c1.coins.repository.DBRepository;
import com.c1.coins.service.UsersService;

@Service
public class UsersServiceImpl implements UsersService {

	@Autowired
	private DBRepository dBRepository;

	@Override
	public List<User> getUsers() {
		return dBRepository.retrieveUsers();
	}

}
