package com.mike.monitor.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class BuyController {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@GetMapping("/buy/{name}")
	public ResponseEntity buy(@PathVariable String name) {
		ResponseEntity<Map> e = restTemplate.getForEntity("http://provider-service/product/1",Map.class);
		return e;
	}

}
