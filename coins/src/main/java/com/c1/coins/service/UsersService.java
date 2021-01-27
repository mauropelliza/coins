package com.c1.coins.service;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import com.c1.coins.model.User;

public interface UsersService {
	public List<User> getUsers();

	public List<String>  insertCoins(Reader file) throws IOException;
}
