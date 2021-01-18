### zipkin介绍

Zipkin是一个分布式跟踪系统。它帮助收集服务架构中的延迟问题所需的定时数据。功能包括收集和查找这些数据。

如果你在日志文件中有一个跟踪ID，你可以直接跳转到它。否则，你可以根据服务、操作名称、标签和持续时间等属性进行查询。一些有趣的数据会被汇总给你，比如在服务中花费的时间百分比，以及操作是否失败。

![](https://zipkin.io/public/img/web-screenshot.png)

Zipkin UI还提供了一个依赖关系图，显示了每个应用程序中的跟踪请求数量。这对于识别包括错误路径或调用废弃服务在内的综合行为很有帮助。

![](https://zipkin.io/public/img/dependency-graph.png)

应用程序需要被 "仪器化 "才能向Zipkin报告跟踪数据。这通常意味着配置一个跟踪器或仪器库。最流行的向Zipkin报告数据的方式是通过HTTP或Kafka，尽管还有许多其他选项，如Apache ActiveMQ、gRPC和RabbitMQ。提供给UI的数据被存储在内存中，或与支持的后端（如Apache Cassandra或Elasticsearch）持久化。

### 为什么要用zipkin

随着我们的系统功能越来越多，拆分的微服务也越来越多，某一项功能的完成要涉及多个微服务的接口调用，我们需要明确的知道调用关系是怎样的。除此之外，我们还要能迅速定位发生错误的接口，响应慢的接口，以此来针对性的优化，来提高我们系统的性能。

有了zipkin的http调用链追踪，我们会清晰地看到整个系统微服务之间的依赖关系，看到每个接口的响应时间。这样每个微服务的维护团队沟通起来就更加方便，一旦需要调整接口，就知道会影响到些微服务，接口调用出现错误也很快能找到负责团队，整个团队配合会更加邮效率。

### 调用链追踪原理

zipkin主要有四个组件：collector，storage，API，web UI。collector用于收集各服务发送到zipkin的数据，storage用于存储这些链路数据，目前支持Cassandra，ElasticSearch（推荐使用，易于大规模扩展）和MySQL，API用来查找和检索跟踪链，提供给界面UI展示。

![](D:\myself\java-pj\springboot-monitoring\springcloud-zipkin\doc\原理.png)

链路的追踪原理：跟踪器位于应用程序中，记录发生的操作的时间和元数据，收集的跟踪数据称为Span，将数据发送到Zipkin的仪器化应用程序中的组件称为Reporter，Reporter通过几种传输方式（http，kafka）之一将追踪数据发送到Zipkin收集器(collector)，然后将跟踪数据进行存储(storage)，由API查询存储以向UI提供数据。

简单讲就是在我们的应用中加入额外的代码来上报http的请求过程，上报的数据会被zipkin分析并展示正在UI上，当然这部分额外的代码不需要我们来写，只需要加入依赖的jar就行了，会自动收集数据上报。

### 创建一个消费者应用

1. pom 依赖

   ```
   <parent>
   		<groupId>org.springframework.boot</groupId>
   		<artifactId>spring-boot-starter-parent</artifactId>
   		<version>2.2.12.RELEASE</version>
   		<relativePath/> <!-- lookup parent from repository -->
   	</parent>
   	<groupId>com.mike.monitor</groupId>
   	<artifactId>consumer-zipkin</artifactId>
   	<version>1.0</version>
   	<name>consumer-zipkin</name>
   	<description>Demo project for Spring Boot</description>
   
   	<properties>
   		<spring-cloud.version>Hoxton.SR9</spring-cloud.version>
   	</properties>
   
   	<dependencies>
   	    <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-web</artifactId>
           </dependency>
   		<dependency>
   			<groupId>org.springframework.cloud</groupId>
   			<artifactId>spring-cloud-sleuth-zipkin</artifactId>
   		</dependency>
   		<dependency>
   			<groupId>org.springframework.cloud</groupId>
   			<artifactId>spring-cloud-starter-sleuth</artifactId>
   		</dependency>
   	</dependencies>
   
   	<dependencyManagement>
   		<dependencies>
   			<dependency>
   				<groupId>org.springframework.cloud</groupId>
   				<artifactId>spring-cloud-dependencies</artifactId>
   				<version>${spring-cloud.version}</version>
   				<type>pom</type>
   				<scope>import</scope>
   			</dependency>
   		</dependencies>
   	</dependencyManagement>
   ```

   sleuth 用来采集数据并上报

2. 服务配置

   ```
   server.port=8081
   spring.application.name=consumer-zipkin
   
   # 发送数据方式 http
   spring.zipkin.sender.type=web
   #zipkin serrver
   spring.zipkin.base-url=http://localhost:9411
   spring.zipkin.service.name=consumer-zipkin
   
   #采样率
   spring.sleuth.sampler.probability=1
   ```

3. 测试接口

   ```
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
   		ResponseEntity<Map> e = restTemplate.getForEntity("http://localhost:8082/product/1",Map.class);
   		return e;
   	}
   
   }
   ```

   ### 创建一个提供者应用

   1. pom配置

      ```
      	<parent>
      		<groupId>org.springframework.boot</groupId>
      		<artifactId>spring-boot-starter-parent</artifactId>
      		<version>2.2.12.RELEASE</version>
      		<relativePath /> <!-- lookup parent from repository -->
      	</parent>
      	<groupId>com.mike.monitor</groupId>
      	<artifactId>provider-sky</artifactId>
      	<version>1.0</version>
      	<name>provider-sky</name>
      	<description>Demo project for Spring Boot</description>
      
      	<properties>
      		<spring-cloud.version>Hoxton.SR9</spring-cloud.version>
      	</properties>
      
      	<dependencies>
      		<dependency>
      			<groupId>org.springframework.boot</groupId>
      			<artifactId>spring-boot-starter-web</artifactId>
      		</dependency>
      		<dependency>
      			<groupId>org.springframework.cloud</groupId>
      			<artifactId>spring-cloud-sleuth-zipkin</artifactId>
      		</dependency>
      		<dependency>
      			<groupId>org.springframework.cloud</groupId>
      			<artifactId>spring-cloud-starter-sleuth</artifactId>
      		</dependency>
      	</dependencies>
      
      	<dependencyManagement>
      		<dependencies>
      			<dependency>
      				<groupId>org.springframework.cloud</groupId>
      				<artifactId>spring-cloud-dependencies</artifactId>
      				<version>${spring-cloud.version}</version>
      				<type>pom</type>
      				<scope>import</scope>
      			</dependency>
      		</dependencies>
      	</dependencyManagement>
      ```

   2. 服务配置

      ```
      server.port=8082
      spring.application.name=provider-zipkin
      
      # 发送数据方式 http
      spring.zipkin.sender.type=web
      #zipkin serrver
      spring.zipkin.base-url=http://localhost:9411
      spring.zipkin.service.name=provider-zipkin
      
      #采样率
      spring.sleuth.sampler.probability=1
      ```

   3. 测试接口

      ````
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
      ````

      ### zipkin 搭建

      1. 下载可执行jarhttps://search.maven.org/remote_content?g=io.zipkin&a=zipkin-server&v=LATEST&c=exec

      2. 启动zipkin

         ```
         java -jar zipkin.jar
         ```

         ![](D:\myself\java-pj\springboot-monitoring\springcloud-zipkin\doc\屏幕截图 2021-01-15 155742.png)

      3. 启动消费者和提供者

      4. 访问 http://localhost:9411

         ![](D:\myself\java-pj\springboot-monitoring\springcloud-zipkin\doc\屏幕截图 2021-01-15 155225.png)

   5. 触发http调用

      http://localhost:8081/api/buy/ha

                 6. 查看调用链以及详细信息

                    ![](D:\myself\java-pj\springboot-monitoring\springcloud-zipkin\doc\屏幕截图 2021-01-15 155302.png)

                    ![屏幕截图 2021-01-15 155349](D:\myself\java-pj\springboot-monitoring\springcloud-zipkin\doc\屏幕截图 2021-01-15 155349.png)

                    ![屏幕截图 2021-01-15 155408](D:\myself\java-pj\springboot-monitoring\springcloud-zipkin\doc\屏幕截图 2021-01-15 155408.png)

                    ![屏幕截图 2021-01-15 155458](D:\myself\java-pj\springboot-monitoring\springcloud-zipkin\doc\屏幕截图 2021-01-15 155458.png)

                    ![屏幕截图 2021-01-15 155524](D:\myself\java-pj\springboot-monitoring\springcloud-zipkin\doc\屏幕截图 2021-01-15 155524.png)

                    ![屏幕截图 2021-01-15 155637](D:\myself\java-pj\springboot-monitoring\springcloud-zipkin\doc\屏幕截图 2021-01-15 155637.png)