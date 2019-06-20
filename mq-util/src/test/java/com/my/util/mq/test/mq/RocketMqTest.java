package com.my.util.mq.test.mq;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.exception.MQClientException;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.producer.*;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.common.message.Message;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.common.message.MessageExt;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.remoting.common.RemotingHelper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @date 2019-01-24
 * @desc: rocketmq test
 */
@Slf4j
public class RocketMqTest {

    private  String tag = "TagOrderAdd";

    @Test
    public  void test() throws Exception {
        //Instantiate with a producer group name.
        DefaultMQProducer producer = new DefaultMQProducer("groupordername");
        // Specify name server addresses.
        producer.setNamesrvAddr(AliyunConfig.namesrvAddr);
        producer.setVipChannelEnabled(false);
        //Launch the instance.
        producer.start();


        // Instantiate with specified consumer group name.
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("groupordername");

        // Specify name server addresses.
        consumer.setNamesrvAddr(AliyunConfig.namesrvAddr);

        // Subscribe one more more topics to consume.
        consumer.subscribe(AliyunConfig.topic, "*");
        // Register callback to execute on arrival of messages fetched from brokers.
        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                            ConsumeConcurrentlyContext context) {
                System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        //Launch the consumer instance.
        consumer.start();


        for (int i = 0; i < 1; i++) {
            //Create a message instance, specifying topic, tag and message body.
            Message msg = new Message(AliyunConfig.topic /* Topic */,
                    tag,
                    ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
            );
            //Call send message to deliver message to one of brokers.
            SendResult sendResult = producer.send(msg);
            System.out.printf("%s%n", sendResult);
        }
        //Shut down once the producer instance is not longer in use.
        producer.shutdown();

        Thread.sleep(10000);
    }


    /**发送延迟消息*/
    @Test
    public  void delayTest() throws Exception {

        // Instantiate a producer to send scheduled messages
        DefaultMQProducer producer = new DefaultMQProducer(AliyunConfig.GROUP_ID);
        producer.setNamesrvAddr(AliyunConfig.namesrvAddr);
        producer.setVipChannelEnabled(false);
        producer.start();
        Message message = new Message(AliyunConfig.topic,"DelayMessage", ("Hello scheduled message ").getBytes());
        // This message will be delivered to consumer 10 seconds later.
        //long time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2019-01-25 10:37:00").getTime();
        /**
         * 延迟级别  |  时间
         * 1        | 1s
         * 2        | 5s
         * 3        | 10s
         * 4        | 30s
         * 5        | 1m
         * 18       | 2h
         * */
        message.setDelayTimeLevel(1);
        // Send the message
        SendResult sendResult = producer.send(message);
        log.info("发送延迟消息={}", JSON.toJSONString(sendResult));
    }

    @Test
    public void transactionTest() throws Exception {
        TransactionMQProducer producer = new TransactionMQProducer(AliyunConfig.GROUP_ID);
        producer.setNamesrvAddr(AliyunConfig.namesrvAddr);
        producer.setVipChannelEnabled(false);
        producer.setTransactionCheckListener(new TransactionCheckListener(){
            @Override
            public LocalTransactionState checkLocalTransactionState(MessageExt msg) {
                log.info("本地业务代码检查:checkLocalTransactionState:{}",JSON.toJSONString(msg));
                return LocalTransactionState.COMMIT_MESSAGE;
            }
        });
        producer.start();
        try {
            Message msg = new Message(AliyunConfig.topic, "transactionTag", "这是一条事务消息".getBytes(RemotingHelper.DEFAULT_CHARSET));
            TransactionSendResult sendResult = producer.sendMessageInTransaction(msg, (msg1, arg) -> {
                log.info("半事务消息发送成功，开始执行业务逻辑：executeLocalTransactionBranch:{}",JSON.toJSONString(msg1));
                if(true){
                    log.error("业务执行错误，开始回滚");
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
                return LocalTransactionState.COMMIT_MESSAGE;
            },null);
            log.info("发送事务消息，后半消息是否成功。{}",JSON.toJSONString(sendResult));
            Thread.sleep(10);
        } catch (MQClientException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Thread.sleep(Integer.MAX_VALUE);
       }

}
