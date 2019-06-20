package com.my.util.mq.test.mq;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * @date 2019-01-25
 * @desc:
 */
@Slf4j
public class AliyunRocketMqConsumerTest {

    @Test
    public void yunTest() throws Exception {
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.AccessKey,AliyunConfig.Access_Key);
        properties.setProperty(PropertyKeyConst.SecretKey,AliyunConfig.Secret_Key);
        properties.setProperty(PropertyKeyConst.GROUP_ID,AliyunConfig.GROUP_ID);
        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR,AliyunConfig.namesrvAddr);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Consumer consumer = ONSFactory.createConsumer(properties);

        consumer.subscribe(AliyunConfig.topic, "*", (message, context) -> {
            try {
                String body = new String(message.getBody(), "UTF-8");
                String str = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS"));
                log.info("消费消息,time={}，messageId={}，topic={}, key={}, tag={}, body={}",
                        str, message.getMsgID(), message.getTopic(),
                        message.getKey(), message.getTag(), body);
               /* if (message.getTag().equals(tag)) {
                    log.error("MQ处理失败，message：{}", message.toString());
                    //return Action.ReconsumeLater;
                }*/
                return Action.CommitMessage;
            } catch (Exception e) {
                log.error("MQ处理失败，message：{}, error={}", message.toString(), e.getMessage());
                return Action.ReconsumeLater;
            }
        });
        consumer.start();
        log.info("消费者启动");
        countDownLatch.await();
    }

}
