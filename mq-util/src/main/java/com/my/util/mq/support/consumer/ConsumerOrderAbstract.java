package com.my.util.mq.support.consumer;

import com.aliyun.openservices.ons.api.*;
import com.aliyun.openservices.ons.api.order.MessageOrderListener;
import com.aliyun.openservices.ons.api.order.OrderConsumer;
import com.my.util.mq.support.RocketGroupProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;

import javax.annotation.PreDestroy;
import java.util.Properties;

public abstract class ConsumerOrderAbstract  extends RocketGroupProperty implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger(ConsumerOrderAbstract.class);

    @Value("${aliyun.rocketmq.send_timeout_millis:5000}")
    private String Send_Timeout_Millis;

    @Value("${aliyun.rocketmq.consume_thread_nums:10}")
    private String Consume_Thread_Nums;

    private OrderConsumer consumer  ;

    /**
     *  订阅过滤表达式字符串，ONS服务器依据此表达式进行过滤。只支持或运算<br>
     *  eg: "tag1 || tag2 || tag3"<br>
     *  如果subExpression等于null或者*，则表示全部订阅
     *  订阅关系是否一致 https://help.aliyun.com/document_detail/43523.html
     * @return
     */
    public  abstract  String getExpression();

    /**消费者线程数*/
    protected String getConsumeThreadNums() {
        return Consume_Thread_Nums;
    }

    /**
     * 自定义注册监听
     * https://help.aliyun.com/document_detail/29551.html
     * 订阅关系是否一致 https://help.aliyun.com/document_detail/43523.html
     * @return
     */
    protected void subscribeList(){
        consumer.subscribe(getTopic(), getExpression(),listener());
    }

    public OrderConsumer getConsumer() {
        return consumer;
    }

    /**
     * 消费方监听
     * @return ONS消息监听对象
     */
    protected abstract MessageOrderListener listener();

    /**
     * MQ 支持以下两种订阅方式：
     * 一、 集群订阅：同一个 Consumer ID 所标识的所有 Consumer 平均分摊消费消息。
     * 例如某个 Topic 有 9 条消息，一个 Consumer ID 有 3 个 Consumer 实例，那么在集群消费模式下每个实例平均分摊，只消费其中的 3 条消息。
     *  // 集群订阅方式设置（不设置的情况下，默认为集群订阅方式）
     *  properties.put(PropertyKeyConst.MessageModel, PropertyValueConst.CLUSTERING);
     * 二、广播订阅：同一个 Consumer ID 所标识的所有 Consumer 都会各自消费某条消息一次。
     *   例如某个 Topic 有 9 条消息，一个 Consumer ID 有 3 个 Consumer 实例，那么在广播消费模式下每个实例都会各自消费 9 条消息。
     *  // 广播订阅方式设置
     *  properties.put(PropertyKeyConst.MessageModel, PropertyValueConst.BROADCASTING);
     * */
    protected  String getMessageModel(){
        return  PropertyValueConst.CLUSTERING;
    }

    /**
     * 此方法在spring boot启动后调用，用于启动消息消费方
     * @param args
     */
    @Override
    public  void run(String... args){
        Properties properties = groupProperties();
        properties.put(PropertyKeyConst.MessageModel, getMessageModel());
        properties.put(PropertyKeyConst.ConsumeThreadNums, getConsumeThreadNums());
        consumer = ONSFactory.createOrderedConsumer(properties);
        subscribeList();
        consumer.start();
        logger.info("start 消息(顺序消息)服务消费者。GroupId(ConsumerId)={}", getGroupId());
    }


    @PreDestroy
    public void shutdown() {
        logger.info("shutdown 消息(顺序消息)服务消费者。GroupId(ConsumerId)={}", getGroupId());
        consumer.shutdown();
    }
}
