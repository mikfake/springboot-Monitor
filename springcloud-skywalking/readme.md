### 下载APM

http://skywalking.apache.org/downloads/  选择SkyWalking APM tar

### 启动

```
选择对应的操作系统启动脚本 在\bin目录下
```

http://localhost:8080/  ui界面

### 应用添加JVM参数

保持service name和配置文件中的一致

```
-javaagent:D:\myself\apache-skywalking-apm-bin-es7\agent\skywalking-agent.jar
-Dskywalking.agent.service_name=consumer-service
```

```
注册中心
-javaagent:D:\myself\apache-skywalking-apm-bin-es7\agent\skywalking-agent.jar
-Dskywalking.agent.service_name=consumer-service
```



