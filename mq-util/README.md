###  Transaction MQ 
事务消息
https://help.aliyun.com/document_detail/29548.html?spm=a2c4g.11186623.2.17.6a3757ddmq2jSz


### 只启动MQ
docker rm -f rocketmq
docker run  --name rocketmq     -p 9876:9876    rocketmqinc/rocketmq:4.4.0
docker logs -f --tail 100 rocketmq


### 启动顺序
```text
1.下载安装docker-compose命令
2.修改docker-compose.yml和rocketmq.md中ip地址
3.修改config->broker.conf文件中的ip地址
3.启动
    1.进入到项目中docker文件夹
    2.docker-compose up -d
4.localhost:8080访问控制台
```

### 停止服务
```text
docker-compose stop
docker-compose rm
```

### 控制台操作
```text
1.右上角，语言切换
2.Topic会自动新建

```