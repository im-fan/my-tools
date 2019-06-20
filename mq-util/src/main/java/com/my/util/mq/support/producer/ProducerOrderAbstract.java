package com.my.util.mq.support.producer;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.*;
import com.aliyun.openservices.ons.api.order.OrderProducer;
import com.my.util.mq.support.RocketGroupProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.Set;

public abstract class ProducerOrderAbstract extends RocketGroupProperty {

    private Logger logger = LoggerFactory.getLogger(ProducerOrderAbstract.class);

    @Value("${aliyun.rocketmq.send_timeout_millis:5000}")
    private String sendTimeoutMillis;

    private OrderProducer producer ;

    public String getSendTimeoutMillis() {
        return sendTimeoutMillis;
    }


    /**
     * 发送顺序消息
     * @param tag
     * @param body 发送的body
     * @param key  业务key，全局唯一
     * @param shardingKey 全局顺序消息，该字段可以设置为任意非空字符串。
     *                    分区顺序消息中区分不同分区的关键字段，sharding key 于普通消息的 key 是完全不同的概念。
     *        注意：shardingKey == null, shardingKey = key
     * */
    public <E> void send(String tag, E body,String key,String shardingKey) {
        try {
            shardingKey = StringUtils.isEmpty(shardingKey) ? key : shardingKey;
            Message msg = new Message(getTopic(), tag, JSON.toJSONString(body).getBytes("UTF-8"));
            SendResult send = producer.send(msg,shardingKey);
            logger.info("消息成功推入MQ：send={},entity={}" , send.toString(), JSON.toJSONString(body));
        } catch (UnsupportedEncodingException e) {
            logger.error("消息推送不支持UTF-8的编码", e);
        }
    }


    /**
     * 发送顺序消息(多tag)
     * @param tags 多个tag
     * @param body 发送的body
     * @param key  业务key，全局唯一
     * @param shardingKey 全局顺序消息，该字段可以设置为任意非空字符串。
     *                    分区顺序消息中区分不同分区的关键字段，sharding key 于普通消息的 key 是完全不同的概念。
     *        注意：shardingKey == null, shardingKey = key
     * */
    public <E> void send(Set<String> tags, E body,String key,String shardingKey) {
        tags.forEach(tag-> send(tag,body,key,shardingKey));
    }

    @PostConstruct
    public void  start(){
        Properties properties = groupProperties();
        properties.put(PropertyKeyConst.SendMsgTimeoutMillis,getSendTimeoutMillis());
        producer = ONSFactory.createOrderProducer(properties);
        producer.start();
        logger.info("start 顺序消息服务提供者。GroupId(ProducerId)={}", getGroupId());
    }

    @PreDestroy
    public void shutdown() {
        logger.info("shutdown 顺序消息服务提供者。GroupId(ProducerId)={}", getGroupId());
        producer.shutdown();
    }
}
