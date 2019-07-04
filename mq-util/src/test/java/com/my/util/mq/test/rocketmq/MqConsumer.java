package com.my.util.mq.test.rocketmq;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.exception.MQClientException;
import com.aliyun.openservices.shade.org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

public class MqConsumer {

    public static void main(String[] args) throws MQClientException {

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(MqDict.ConsumerGroup);
//        DefaultMQPullConsumer consumer = new DefaultMQPullConsumer(MqDict.ConsumerName);

        /** 多个用;分割 **/
        consumer.setNamesrvAddr(MqDict.NameAddress);

        /** 配置topic和tag，subExpression=* 表示所有tag，多个tag tag1 || tag2 || tag3 **/
        consumer.subscribe(MqDict.Topic, "*");
        //consumer.subscribe(MqDict.Topic, "TagA1 || TagA2");
        // 使用SQL表达式进行消息过滤,需要增加enablePropertyFilter = true
//        consumer.subscribe(topic, MessageSelector.bySql("id >= 1 and id <= 5"));

        /** 使用MessageFilter方式过滤-已移除 **/
        /*ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        File classFile =new File(classLoader.getResource("MessageFilterService.java").getFile());
        String filterCode = MixAll.file2String(classFile);
        consumer.subscribe(topic, "org.apache.rocketmq.example.quickstart.filter.MessageFilterService", filterCode);
        */

        /** 并发消费-默认消费方式 **/
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            String date = DateFormatUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss.SS");
            System.out.printf("%s %s Receive New Messages: %s %n",date, Thread.currentThread().getName(), String.valueOf(msgs));
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });


        /** 顺序消费 **/
        /*consumer.registerMessageListener(new MessageListenerOrderly(){

            Random random = new Random();
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
                //context.setAutoCommit(true);
                for (MessageExt msg : msgs) {
                    // 可以看到每个queue有唯一的consume线程来消费, 订单对每个queue(分区)有序
                    System.out.println("consumeThread=" + Thread.currentThread().getName() + "queueId=" + msg.getQueueId() + ", content:" + new String(msg.getBody()));
                }

                try {
                    //模拟业务逻辑处理中...
                    TimeUnit.SECONDS.sleep(random.nextInt(10));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return ConsumeOrderlyStatus.SUCCESS;
            }
        });*/

        consumer.start();

        System.out.println("MqConsumer Started=====>");
    }
}
