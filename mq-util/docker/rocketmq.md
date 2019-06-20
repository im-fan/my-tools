### RocketMQ常用配置解释

- 当需要通过同一个namesrv管理多个集群的时候，不同集群配置不同的值
```text
brokerClusterName = MaidaoCluster
```
- 属于同一个主备配置的broker的brokerName要一样
```text
brokerName = broker-a
```
- 该选项默认是0，代表主。当配置主备的时候，备库需要递增，例如1,2等
```text
brokerId = 0
namesrvAddr = "服务器IP:9876"
```
- 用来解决docker本地ip外部无法访问的问题
```text
- 配置也可以从启动broker的参数中配置：-n 127.0.0.1:9876
brokerIP1 = 服务器IP
```
- 默认8，表示默认为每个topic创建的queue的数量
```text
defaultTopicQueueNums = 8
```
- 当topic不存在的时候自动创建topic，默认为true；线上最好关闭，有利于管理topic
```text
autoCreateTopicEnable = true
```

- 当订阅组不存在的时候，自动创建，默认为true；线上最好关闭，便于管理消息订阅组
```text
autoCreateSubscriptionGroup = true
```
- 是否要拒绝事务消息
```text
- 默认为false；当broker不希望支持事务消息的时候，可以设置为true
rejectTransactionMessage = false
```

- 是否通过域名系统获得namesrv的地址，默认为false
```text
fetchNamesrvAddrByAddressServer = false
```
- commitlog刷盘的间隔
```text
- 默认为1000毫秒，即1秒
flushIntervalCommitLog = 1000
```
- 是否是定时刷盘
```text
- 默认为false，也就是实时刷盘，实时刷盘是指有数据写入就会触发刷盘逻辑，如果满足刷页条件就刷盘
flushCommitLogTimed = false
```
- 何时触发删除文件
```text
- 默认是凌晨4点删除文件
deleteWhen = 04
```
- 文件保留时间，单位小时
```text
fileReservedTime = 48
```
- 消息体最大值
```text
- 默认值是1024 * 512 = 524288，也就是512k
maxMessageSize = 524288
```
- 命中消息在内存的最大比例
```text
accessMessageInMemoryMaxRatio = 40
```
- 是否开启消息索引功能
```text
messageIndexEnable = true
```
- 是否使用安全的消息索引功能,即可靠模式
```text
- 可靠模式下，异常宕机恢复慢，非可靠模式下，异常宕机恢复快
messageIndexSafe = false
```
- 默认是ASYNC_MASTER，异步复制master
```text
- 还有SYNC_MASTER - 同步双写master；SLAVE - slave服务器
brokerRole = ASYNC_MASTER
```
- 默认是ASYNC_FLUSH
```text
- 异步刷盘;还有SYNC_FLUSH，同步刷盘
flushDiskType = ASYNC_FLUSH
```
- 磁盘空间超过90%警戒水位，自动开始删除文件
```text
cleanFileForciblyEnable = false
```

### 其他配置项
```text
- 当为了提高namesrv地址的灵活性，可以设置为true，当打开该选项的时候，上边namesrvAddr配置中所述的都可以不配置
haMasterAddress = 127.0.0.1 #如果不设置，则从NameServer获取Master HA服务地址

# store存储配置
- 默认存储在当前用户目录的store目录下
#storePathRootDir = /home/admin/store
- 提交日志的存放路径 默认地址为当前用户目录user.home + "store" + "commitlog"
#storePathCommitLog = /home/admin/store/commitlog
- 消费队列的存放路径 默认地址为当前用户目录user.home + "store" + "consumequeue"
#storePathConsumerQueue = /home/admin/store/consumequeue

- 最大被拉取的消息字节数，消息在内存，默认256k
maxTransferBytesOnMessageInMemory = 262144
- 最大被拉取的消息个数，消息在内存，默认32个
maxTransferCountOnMessageInMemory = 32

- 最大被拉取的消息字节数，消息在磁盘，默认64k
#maxTransferBytesOnMessageInDisk = 65536
- 最大被拉取的消息个数，消息在磁盘，默认8个
maxTransferCountOnMessageInDisk = 8

```

