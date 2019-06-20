package com.my.util.mq.support.rocket.impl;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.exception.ONSClientException;
import com.aliyun.openservices.ons.api.impl.rocketmq.FAQ;
import com.aliyun.openservices.ons.api.impl.rocketmq.ONSUtil;
import com.aliyun.openservices.ons.api.impl.rocketmq.ProducerImpl;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.exception.MQClientException;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.common.protocol.ResponseCode;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.remoting.exception.RemotingConnectException;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.remoting.exception.RemotingTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * @date 2019-02-13
 * @desc: RocketMQ 服务提供者
 */
public class RocketMqProducerImpl extends ProducerImpl {

    private Logger  logger = LoggerFactory.getLogger(RocketMqProducerImpl.class);

    private  List<Long> delayLevelList ;

    public RocketMqProducerImpl(Properties properties) {
        super(properties);
        delayLevelList = initDelayLevelTable();
    }

    private  int getDelayTimeLevel(Long startDeliverTime){
        int level = 1;
        if(startDeliverTime <= 0){
            return level;
        }
        for (int i = 0; i < delayLevelList.size(); i++) {
            if(startDeliverTime <= delayLevelList.get(i)){
                return ++i;
            }
        }
        return 18;
    }

    /**
     * 延迟级别  |  时间
     * 1        | 1s
     * 3        | 10s
     * 4        | 30s
     * 5        | 1m
     * 18       | 2h
     * MessageStoreConfig messageDelayLevel = "1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h";
     * */
    private static List<Long> initDelayLevelTable(){
        HashMap<String, Long> timeUnitTable = new HashMap<>(6);
        timeUnitTable.put("s", 1000L);
        timeUnitTable.put("m", 1000L * 60);
        timeUnitTable.put("h", 1000L * 60 * 60);
        timeUnitTable.put("d", 1000L * 60 * 60 * 24);
        String levelString = "1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h";
        String[] levelArray = levelString.split(" ");
        List<Long> delayLevelList = new ArrayList<>();
        for (int i = 0; i < levelArray.length; i++) {
            String value = levelArray[i];
            String ch = value.substring(value.length() - 1);
            Long tu = timeUnitTable.get(ch);
            //int level = i + 1;
            long num = Long.parseLong(value.substring(0, value.length() - 1));
            long delayTimeMillis = tu * num;
            delayLevelList.add(delayTimeMillis);
        }
        return delayLevelList;
    }


    @Override
    public SendResult send(Message message) {
        DefaultMQProducer defaultMQProducer = getDefaultMQProducer();
        super.checkONSProducerServiceState(defaultMQProducer.getDefaultMQProducerImpl());
        com.aliyun.openservices.shade.com.alibaba.rocketmq.common.message.Message msgRMQ = ONSUtil.msgConvert(message);
        /**
         * Apache RocketMQ 与阿里云MQ发送延迟消息适配。
         * */
        if (message.getStartDeliverTime() > 0) {
            int delayTimeLevel = getDelayTimeLevel(message.getStartDeliverTime() - System.currentTimeMillis());
            msgRMQ.setDelayTimeLevel(delayTimeLevel);
        }
        try {
            com.aliyun.openservices.shade.com.alibaba.rocketmq.client.producer.SendResult sendResultRMQ = defaultMQProducer.send(msgRMQ);

            message.setMsgID(sendResultRMQ.getMsgId());
            SendResult sendResult = new SendResult();
            sendResult.setTopic(sendResultRMQ.getMessageQueue().getTopic());
            sendResult.setMessageId(sendResultRMQ.getMsgId());
            return sendResult;
        } catch (Exception e) {
            logger.error(String.format("Send message Exception, %s", message), e);
            throw checkProducerException(message.getTopic(), message.getMsgID(), e);
        }
    }

    private ONSClientException checkProducerException(String topic, String msgId, Throwable e) {
        if (e instanceof MQClientException) {
            //
            if (e.getCause() != null) {
                // 无法连接Broker
                if (e.getCause() instanceof RemotingConnectException) {
                    return new ONSClientException(
                            FAQ.errorMessage(String.format("Connect broker failed, Topic=%s, msgId=%s", topic, msgId), FAQ.CONNECT_BROKER_FAILED));
                }
                // 发送消息超时
                else if (e.getCause() instanceof RemotingTimeoutException) {
                    return new ONSClientException(FAQ.errorMessage(String.format("Send message to broker timeout, %dms, Topic=%s, msgId=%s",
                            getDefaultMQProducer().getSendMsgTimeout(), topic, msgId), FAQ.SEND_MSG_TO_BROKER_TIMEOUT));
                }
                // Broker返回异常
                else if (e.getCause() instanceof MQBrokerException) {
                    MQBrokerException excep = (MQBrokerException) e.getCause();
                    return new ONSClientException(FAQ.errorMessage(
                            String.format("Receive a broker exception, Topi=%s, msgId=%s, %s", topic, msgId, excep.getErrorMessage()),
                            FAQ.BROKER_RESPONSE_EXCEPTION));
                }
            }
            // 纯客户端异常
            else {
                MQClientException excep = (MQClientException) e;
                if (-1 == excep.getResponseCode()) {
                    return new ONSClientException(
                            FAQ.errorMessage(String.format("Topic does not exist, Topic=%s, msgId=%s", topic, msgId), FAQ.TOPIC_ROUTE_NOT_EXIST));
                } else if (ResponseCode.MESSAGE_ILLEGAL == excep.getResponseCode()) {
                    return new ONSClientException(
                            FAQ.errorMessage(String.format("ONS Client check message exception, Topic=%s, msgId=%s", topic, msgId),
                                    FAQ.CLIENT_CHECK_MSG_EXCEPTION));
                }
            }
        }

        return new ONSClientException("defaultMQProducer send exception", e);
    }
}
