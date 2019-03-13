## 接口安全校验
> 适用于FeignClient框架接口安全校验

### 原理
```text
在接口请求的请求头中增加JWT，服务方校验合法性
```

### 使用
```text
1.接口提供方/接口使用方引入pom
<dependency>
    <groupId>com.wyf</groupId>
    <artifactId>wyf-security</artifactId>
    <version>1.0.1-SNAPSHOT</version>
</dependency>

2.接口提供方在Controller上增加注解
@Security

```