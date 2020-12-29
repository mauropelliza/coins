package com.c1.coins.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.c1.coins.model.User;
import com.c1.coins.service.UsersService;

@RestController
@RequestMapping(path = "users")
public class UsersController {

	@Autowired
	private UsersService usersService;

	@GetMapping
	public List<User> getUsers() {
		return usersService.getUsers();
	}
}