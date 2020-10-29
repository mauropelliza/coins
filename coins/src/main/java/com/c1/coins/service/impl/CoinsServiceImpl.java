package com.c1.coins.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.c1.coins.model.Coin;
//import com.c1.coins.repository.CoinsJpaDao;
import com.c1.coins.service.CoinsService;

@Service
public class CoinsServiceImpl implements CoinsService {
	//@Autowired
	//private CoinsJpaDao coinsJpaDao;
		
	@Override
	public List<Coin> getAllCoins() {
		return null;//coinsJpaDao.findAll();
	}
}
