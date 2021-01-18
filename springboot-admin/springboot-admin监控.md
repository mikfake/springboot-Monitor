### 监控你的springboot应用

### Spring Boot Admin是什么？

[Spring Boot Admin](https://github.com/codecentric/spring-boot-admin) 是一个web应用，基于Spring Boot Actuator endpoints，它可以监控所有注册到Admin Server的应用的健康状态，包括一下特征：

- 显示健康状态
- 显示一些细节，比如
  - JVM & 内存使用指标
  - micrometer.io 指标
  - 数据库指标
  - 缓存指标
- 显示应用构建版本信息
- 追踪应用日志
- 查看JVM系统环境设置
- 查看Spring Boot 配置属性
- 支持 Spring Cloud's postable /env- &/refresh-endpoint
- 日志级别管理
- 与JMX-beans 交互
- 查看线程转储
- 查看http调用
- View auditevents
- 查看http调用端点
- 查看定时任务
- 查看或删除 sessions (using spring-session)
- 查看Flyway / Liquibase 数据库迁移
- 下载堆转储文件
- 告警通知 (via e-mail, Slack, Hipchat, ...)
- 状态更改的事件日志(非持久的)

Spring Boot Admin相当于一个服务端，每一个要监控的服务相当于客户端，接下来我们就开始搭建服务端和客户端。

### Spring Boot Admin服务端

1. 创建一个springboot web项目，在pom文件中引入下面的依赖：

   ```
   	<parent>
   		<groupId>org.springframework.boot</groupId>
   		<artifactId>spring-boot-starter-parent</artifactId>
   		<version>2.2.12.RELEASE</version>
   	</parent>
   	
   	<dependency>
   			<groupId>org.springframework.boot</groupId>
   			<artifactId>spring-boot-starter-web</artifactId>
   		</dependency>
   		<dependency>
   			<groupId>de.codecentric</groupId>
   			<artifactId>spring-boot-admin-server</artifactId>
   			<version>2.3.0</version>
   		</dependency>
   		<dependency>
   			<groupId>de.codecentric</groupId>
   			<artifactId>spring-boot-admin-server-ui</artifactId>
   			<version>2.3.0</version>
   		</dependency>
   ```

2. 添加配置信息：

   ```
   server.port=9000
   spring.application.name=springboot-admin
   ```

   指定应用名称和启动端口号

3. 启动`springboot-admin`服务端应用

### 客户端应用

1. 还是创建一个web应用，在pom文件中引入下面的依赖：

   ```
   	<parent>
   		<groupId>org.springframework.boot</groupId>
   		<artifactId>spring-boot-starter-parent</artifactId>
   		<version>2.2.12.RELEASE</version>
   	</parent>
   		<dependency>
   			<groupId>org.springframework.boot</groupId>
   			<artifactId>spring-boot-starter-web</artifactId>
   		</dependency>
   		<dependency>
   			<groupId>de.codecentric</groupId>
       		<artifactId>spring-boot-admin-starter-client</artifactId>
   			<version>2.3.0</version>
   		</dependency>
   ```

2. 添加配置信息：

   ```
   server.port=8081
   spring.application.name=springboot-client
   spring.boot.admin.client.url=http://localhost:9000  
   management.endpoints.web.exposure.include=*  
   management.endpoint.health.show-details=ALWAYS
   ```

   

3. 添加测试接口

   ```
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
   
   ```

4. 启动`springboot-client`服务

5. 访问http://localhost:9000 可以看到下面这些监控信息

   ![](D:\myself\java-pj\springboot-monitoring\springboot-admin\doc\屏幕截图 2021-01-15 160126.png)

   ![屏幕截图 2021-01-15 160210](D:\myself\java-pj\springboot-monitoring\springboot-admin\doc\屏幕截图 2021-01-15 160210.png)

![](D:\myself\java-pj\springboot-monitoring\springboot-admin\doc\屏幕截图 2021-01-15 160237.png)

![](D:\myself\java-pj\springboot-monitoring\springboot-admin\doc\屏幕截图 2021-01-15 160603.png)

![屏幕截图 2021-01-15 160650](D:\myself\java-pj\springboot-monitoring\springboot-admin\doc\屏幕截图 2021-01-15 160650.png)

![屏幕截图 2021-01-15 160735](D:\myself\java-pj\springboot-monitoring\springboot-admin\doc\屏幕截图 2021-01-15 160735.png)

![屏幕截图 2021-01-15 160805](D:\myself\java-pj\springboot-monitoring\springboot-admin\doc\屏幕截图 2021-01-15 160805.png)

![屏幕截图 2021-01-15 160818](D:\myself\java-pj\springboot-monitoring\springboot-admin\doc\屏幕截图 2021-01-15 160818.png)

![屏幕截图 2021-01-15 161033](D:\myself\java-pj\springboot-monitoring\springboot-admin\doc\屏幕截图 2021-01-15 161033.png)

### 监控http调用

http的监控，需要手动在性能中配置，在`method`中选择`GET`,`POST`或其他，然后在`uri`中选择你自己的接口地址就可以了，我添加了两个接口，一个演示正常请求，一个演示错误异常：

* http://localhost:8081/api/say
* http://localhost:8081/api/test/0

![](D:\myself\java-pj\springboot-monitoring\springboot-admin\doc\屏幕截图 2021-01-15 160438.png)

![屏幕截图 2021-01-15 160512](D:\myself\java-pj\springboot-monitoring\springboot-admin\doc\屏幕截图 2021-01-15 160512.png)

![屏幕截图 2021-01-15 160525](D:\myself\java-pj\springboot-monitoring\springboot-admin\doc\屏幕截图 2021-01-15 160525.png)

### 添加在线日志监控

正常只能在线调整日志级别，在客户端添加如下配置，可在西安查看日志：

1. 配置项

   ```
   #日志文件位置
   logging.file=D:/logs/output.log
   ```

2. logback.xml配置,放在resources目录下即可

   ```
   <?xml version="1.0" encoding="UTF-8"?>
   <configuration>
          <property name="APP_Name" value="mike-client" /> 
   　　　<contextName>${APP_Name}</contextName>
       <!--定义日志文件的存储地址 勿在 LogBack 的配置中使用相对路径，请根据需求配置路径-->  
       <property name="LOG_HOME" value="D:/logs" />  
       
       <!-- 彩色日志依赖的渲染类 -->
       <conversionRule conversionWord="clr" converterClass="org.springframework.boot.logging.logback.ColorConverter" />
       <conversionRule conversionWord="wex" converterClass="org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter" />
       <conversionRule conversionWord="wEx" converterClass="org.springframework.boot.logging.logback.ExtendedWhitespaceThrowableProxyConverter" />
       <!-- 彩色日志格式 -->
       <property name="CONSOLE_LOG_PATTERN"
                 value="adminTest >> ${CONSOLE_LOG_PATTERN:-%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(LN:%L){faint} %m%n${LOG_EXCEPTION_CONVERSION_WORD:-%wEx}}" />
       
       <!-- 控制台输出 -->   
       <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
           <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder"> 
               <pattern>${CONSOLE_LOG_PATTERN}</pattern>
               <charset>utf8</charset>
           </encoder> 
       </appender>
       
       <!-- 按照每天生成日志文件 -->   
       <appender name="FILE"  class="ch.qos.logback.core.rolling.RollingFileAppender">
           <file>${LOG_HOME}/output.log</file>   
           <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
               <!--日志文件输出的文件名-->
               <FileNamePattern>${LOG_HOME}/output-%d{yyyy-MM-dd}.log</FileNamePattern> 
               <!--日志文件保留天数-->
               <MaxHistory>30</MaxHistory>
           </rollingPolicy>   
           <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder"> 
               <!--格式化输出：%d表示日期，%thread表示线程名，%-5level：级别从左显示5个字符宽度%msg：日志消息，%n是换行符--> 
               <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50}:%L - %msg%n</pattern>   
           </encoder> 
       </appender> 
       
       <!--  定制 --> 
       <logger name="com.mike.monitor.client"  level="ERROR" />  
   
       <!-- 日志输出级别  ，注意：如果不写<appender-ref ref="FILE" /> ，将导致springbootadmin找不到文件，无法查看日志 -->
       <root level="INFO">
           <appender-ref ref="STDOUT" />
           <appender-ref ref="FILE" />
       </root>
   </configuration>
   ```

   以上配置只需要客户端配置。

   ![](D:\myself\java-pj\springboot-monitoring\springboot-admin\doc\屏幕截图 2021-01-15 160638.png)

   ![屏幕截图 2021-01-15 160650](D:\myself\java-pj\springboot-monitoring\springboot-admin\doc\屏幕截图 2021-01-15 160650.png)

### 为服务端客户端添加安全认证

   上面的配置，只要服务端地址暴露，任何客户端都能够注册，任何人都能查看我们的监控中心，这时不安全的，我们需要通过用户名密码的才能访问我们的服务。

   1. 服务端加入配置：

      ```
      spring.security.user.name=admin
      spring.security.user.password=admin
      ```

   2. 服务端pom加入依赖项：

      ```
      		<dependency>
      			<groupId>org.springframework.boot</groupId>
      			<artifactId>spring-boot-starter-security</artifactId>
      		</dependency>
      ```

   3. 服务端加入认证代码`SecuritySecureConfig`：

      ```
      package com.mike.monitor.server.config;
      
      import java.util.UUID;
      
      import org.springframework.context.annotation.Configuration;
      import org.springframework.http.HttpMethod;
      import org.springframework.security.config.Customizer;
      import org.springframework.security.config.annotation.web.builders.HttpSecurity;
      import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
      import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
      import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
      import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
      
      import de.codecentric.boot.admin.server.config.AdminServerProperties;
      
      @Configuration(proxyBeanMethods = false)
      public class SecuritySecureConfig extends WebSecurityConfigurerAdapter {
      	private final AdminServerProperties adminServer;
      
      	  public SecuritySecureConfig(AdminServerProperties adminServer) {
      	    this.adminServer = adminServer;
      	  }
      
      	  @Override
      	  protected void configure(HttpSecurity http) throws Exception {
      	    SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
      	    successHandler.setTargetUrlParameter("redirectTo");
      	    successHandler.setDefaultTargetUrl(this.adminServer.path("/"));
      
      	    http.authorizeRequests(
      	        (authorizeRequests) -> authorizeRequests.antMatchers(this.adminServer.path("/assets/**")).permitAll() 
      	            .antMatchers(this.adminServer.path("/login")).permitAll().anyRequest().authenticated() 
      	    ).formLogin(
      	        (formLogin) -> formLogin.loginPage(this.adminServer.path("/login")).successHandler(successHandler).and() 
      	    ).logout((logout) -> logout.logoutUrl(this.adminServer.path("/logout"))).httpBasic(Customizer.withDefaults()) 
      	        .csrf((csrf) -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) 
      	            .ignoringRequestMatchers(
      	                new AntPathRequestMatcher(this.adminServer.path("/instances"),
      	                    HttpMethod.POST.toString()), 
      	                new AntPathRequestMatcher(this.adminServer.path("/instances/*"),
      	                    HttpMethod.DELETE.toString()), 
      	                new AntPathRequestMatcher(this.adminServer.path("/actuator/**")) 
      	            ))
      	        .rememberMe((rememberMe) -> rememberMe.key(UUID.randomUUID().toString()).tokenValiditySeconds(1209600));
      	  }
      }
      
      ```

      以上代码来自于官网https://codecentric.github.io/spring-boot-admin/2.3.1/#_securing_spring_boot_admin_server

   4. 客户端加入配置：

      ```
      #admin工程的账号密码
      spring.boot.admin.client.username=admin
      spring.boot.admin.client.password=admin
      ```

   5. 再次访问监控中心

      ![](D:\myself\java-pj\springboot-monitoring\springboot-admin\doc\屏幕截图 2021-01-15 160037.png)

   输入你配置的用户名密码即可访问。

### 添加邮件告警通知

   如果我们的服务挂掉了，我们需要第一时间知道，告警通知除了支持邮件还支持其他通知类型，可参考官网。

   1. 服务端添加新的依赖

      ```
      		<dependency>
          		<groupId>org.springframework.boot</groupId>
          		<artifactId>spring-boot-starter-mail</artifactId>
      		</dependency>
      ```

   2. 服务端添加新的配置

      ```
      #发送邮件邮箱
      spring.mail.host=smtp.qq.com
      spring.mail.username=342746156@qq.com
      spring.mail.password=xxxx
      #接收邮件邮箱
      spring.boot.admin.notify.mail.from=342746156@qq.com
      spring.boot.admin.notify.mail.to=qqz007@sina.cn
      ```

      上面使用qq邮箱作为发送邮件服务，其他邮箱需要参考对应的配置，需要在邮箱设置里开启smtp服务，一般都有指导，且密码大部分都为你的邮箱登录密码。

   3. 停止自己的客户端服务，检查自己的收件箱

      ![](D:\myself\java-pj\springboot-monitoring\springboot-admin\doc\屏幕截图 2021-01-18 153753.png)

      ![屏幕截图 2021-01-18 153820](D:\myself\java-pj\springboot-monitoring\springboot-admin\doc\屏幕截图 2021-01-18 153820.png)

