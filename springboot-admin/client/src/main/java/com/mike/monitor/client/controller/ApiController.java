package com.mike.monitor.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class ApiController {
	
	@Autowired
	private RestTemplate restTemplate;
	@GetMapping("/say")
	public String hello() {
		return "hello world";
	}

	
	@GetMapping("/call")
	public String call() {
		ResponseEntity<String> s = restTemplate.getForEntity("http://www.baidu.com", String.class);
		return s.getBody();
	}
	
	@GetMapping("/test/{num}")
	public int divisible( @PathVariable(name = "num") int num) {
		return 5/num;
	}
}
