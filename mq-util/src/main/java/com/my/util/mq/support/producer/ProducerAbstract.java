package com.my.util.mq.support.producer;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.SendResult;
import com.my.util.mq.support.RocketGroupProperty;
import com.my.util.mq.support.rocket.RocketMqFactory;
import com.my.util.mq.support.rocket.impl.RocketMqProducerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.Set;


public abstract class ProducerAbstract extends RocketGroupProperty {

    private Logger logger = LoggerFactory.getLogger(ProducerAbstract.class);

    @Value("${aliyun.rocketmq.sendTimeoutMillis:5000}")
    private String sendTimeoutMillis;

    private RocketMqProducerImpl producer ;

    public String getSendTimeoutMillis() {
        return sendTimeoutMillis;
    }

    /**
     * 发送消息(多tag)
     * @param tags  多个tag
     * @param body 发送的body
     * @param key  业务key，全局唯一
     * */
    public <E> void send(Set<String> tags, E body,String key){
        tags.forEach(tag-> sendDelayTime(tag,body,key,0));
    }


    /**推送消息*/
    public <E> void send(String tag, E body,String key) {
        sendDelayTime(tag,body,key,0);
    }


    /**发送延迟消息(多tag)*/
    public <E> void sendDelayTime(Set<String> tags, E body, String key ,long delayTime){
       tags.forEach(tag-> sendDelayTime(tag,body,key,delayTime));
    }


    /**发送延迟消息*/
    public <E> void sendDelayTime(String tag, E body,  String key ,long delayTime) {
        /**阿里云RocketMQ*/
        send(tag,body,key,delayTime);
    }

    private <E> void send(String tag, E body,  String key ,long delayTime){
        try {
            Message msg = new Message(getTopic(), tag, JSON.toJSONString(body).getBytes("UTF-8"));
            if(delayTime >0 ){
                msg.setStartDeliverTime(delayTime);
            }
            msg.setKey(key);
            SendResult send = producer.send(msg);
            logger.info("消息成功推入MQ：send={},entity={}" , send.toString(), JSON.toJSONString(body));
        } catch (UnsupportedEncodingException e) {
            logger.error("消息推送不支持UTF-8的编码", e);
        }
    }

    @PostConstruct
    public void  start(){
        Properties properties = groupProperties();
        properties.setProperty(PropertyKeyConst.SendMsgTimeoutMillis,getSendTimeoutMillis());
        producer = (RocketMqProducerImpl) RocketMqFactory.createProducer(properties);
        producer.start();
        logger.info("start 消息(普通、定时/延时消息)服务提供者。GroupId(ProducerId)={}", getGroupId());
    }


    @PreDestroy
    public void shutdown() {
        logger.info("shutdown 消息(普通、定时/延时消息)服务提供者。GroupId(ProducerId)={}", getGroupId());
        producer.shutdown();
    }
}
