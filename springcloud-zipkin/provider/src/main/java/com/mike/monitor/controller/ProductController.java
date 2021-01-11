package com.mike.monitor.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductController {
	
	@GetMapping("/{id}")
	public Map getProduct(@PathVariable Integer id) {
		Map<String, String> map = new HashMap<>();
		map.put("name", "iphone12");
		map.put("price", "12000");
		map.put("color", "white");
		return map;
		
	}

}
