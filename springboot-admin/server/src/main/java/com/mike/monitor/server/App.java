package com.mike.monitor.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.codecentric.boot.admin.server.config.EnableAdminServer;

@SpringBootApplication
@EnableAdminServer
public class App
{
    public static void main( String[] args )
    {
    	 SpringApplication.run(App.class, args);
    }
}
