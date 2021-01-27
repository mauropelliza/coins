package com.c1.coins.controller;

import static com.c1.coins.utils.Fields.ACCEPT_CSV;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

	@PostMapping(path = "coins")
	public void insertCoins(@RequestBody MultipartFile coins, HttpServletResponse response) throws IOException {
		try (Reader reader = new InputStreamReader(coins.getInputStream())) {
			List<String> results = usersService.insertCoins(reader);
			response.setContentType(ACCEPT_CSV);
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"productsUpsertResults.csv\"");
			StringBuffer sb = new StringBuffer();
			results.forEach((final String fila) -> sb.append(fila));
			response.getWriter().write(sb.toString());
		}
	}
}