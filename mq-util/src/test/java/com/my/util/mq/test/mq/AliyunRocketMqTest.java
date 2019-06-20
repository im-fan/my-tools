package com.my.util.mq.test.mq;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.*;
import com.aliyun.openservices.ons.api.transaction.TransactionProducer;
import com.aliyun.openservices.ons.api.transaction.TransactionStatus;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.remoting.common.RemotingHelper;
import com.my.util.mq.support.rocket.RocketMqFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;
import java.util.UUID;

/**
 * @date 2019-01-25
 */
@Slf4j
public class AliyunRocketMqTest {

    private  String tag = "TagOrderAdd";

    private Properties properties = new Properties();

    @Before
    public void before (){
        properties.setProperty(PropertyKeyConst.AccessKey,AliyunConfig.Access_Key);
        properties.setProperty(PropertyKeyConst.SecretKey,AliyunConfig.Secret_Key);
        properties.setProperty(PropertyKeyConst.GROUP_ID,AliyunConfig.GROUP_ID);
        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR,AliyunConfig.namesrvAddr);
    }

    /**发送普通消息*/
    @Test
    public  void yunTest() throws Exception {
        Producer producer = ONSFactory.createProducer(properties);
        producer.start();
        for(int i=0; i<20; i++){
            Message msg = new Message(AliyunConfig.topic, tag ,"普通消息".getBytes(RemotingHelper.DEFAULT_CHARSET));
            msg.setKey(UUID.randomUUID().toString());
            SendResult sendResult = producer.send(msg);
            log.info("发送普通消息={}",sendResult);
        }

    }

    /**发送延迟消息*/
    @Test
    public  void yunDelayTest() throws Exception {
        Producer producer = RocketMqFactory.createProducer(properties);
        producer.start();
        Message msg = new Message(AliyunConfig.topic, tag ,"延迟消息".getBytes(RemotingHelper.DEFAULT_CHARSET));
        msg.setKey(UUID.randomUUID().toString());
        /**
         * 延迟3s开始投递
         * @TODO 与期望不一致
         * */
        //long time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2019-01-25 10:37:00").getTime();
        long nowStr = System.currentTimeMillis();
        msg.setStartDeliverTime(nowStr+10000);
        SendResult sendResult = producer.send(msg);
        log.info("发送延迟消息={}",sendResult);
    }


    /**发送事务消息*/
    @Test
    public  void transactionTest() throws Exception {
        properties.setProperty(PropertyKeyConst.CheckImmunityTimeInSeconds,"3");
        TransactionProducer producer = ONSFactory.createTransactionProducer(properties, msg->{
            log.info("开始事务状态(Unknow)检查={}", JSON.toJSONString(msg));
            String msgId = msg.getMsgID();
            try {
                 log.info("事务消息检查，业务处理成功。msgId={}",msgId);
                 return TransactionStatus.CommitTransaction;
            } catch (Throwable e) {
                log.error("分布式消息事务，检查处理失败,msgId={}", msgId, e);
            }
            return TransactionStatus.Unknow;
        });
        producer.start();
        Message msg = new Message(AliyunConfig.topic, tag ,"事务消息".getBytes(RemotingHelper.DEFAULT_CHARSET));
        msg.setKey(UUID.randomUUID().toString());
        try{
            SendResult sendResult = producer.send(msg,(msg1, arg) -> {
                log.info("半事务消息发送成功，开始执行业务逻辑：executeLocalTransactionBranch:{}",JSON.toJSONString(msg1));
                if(true){
                    log.debug("手动返回未知，开始回滚");
                    return TransactionStatus.Unknow;
                }
                return TransactionStatus.CommitTransaction;
            },null);
            log.info("事务消息结果={}", JSON.toJSONString(sendResult));
        }catch (Exception e){
           log.error("eee",e);
        }
        Thread.sleep(10000);
    }




}
