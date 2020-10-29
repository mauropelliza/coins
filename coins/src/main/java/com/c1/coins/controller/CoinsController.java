package com.c1.coins.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.c1.coins.model.Coin;
import com.c1.coins.service.CoinsService;

@RestController
@RequestMapping(path = "/coins")
public class CoinsController {
	@Autowired
	private CoinsService coinsService;
	
	@GetMapping
	public List<Coin> getAllCoins() {
		return coinsService.getAllCoins();
	}
}
