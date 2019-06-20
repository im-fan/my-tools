package com.my.util.mq.service.ons.consumer;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.MessageListener;
import com.my.util.mq.support.consumer.ConsumerAbstract;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @date 2018/12/17
 * @desc: 消息消费者
 */
@Slf4j
@Service
public class BusinessConsumerService  extends ConsumerAbstract {


    @Value("${aliyun.rocketmq.common.consumer.topic:MD_TOPIC_PAYMENT_DEV}")
    private String topic;

    @Value("${aliyun.rocketmq.common.consumer.groupId:CID_MD_ORDER_DEV}")
    private String groupId;


    @Override
    protected String getTopic() {
        return topic;
    }

    @Override
    public String getExpression() {
        return "TAG_PAYMENT_ORDER||TAG_PAYMENT_SHOPKEEPER||TRADE_CENTER_RESULT_ISSUE_TAG";
    }

    @Override
    protected String getGroupId() {
        return groupId;
    }

    @Override
    protected MessageListener listener() {
        return (message, context) -> {
            try{
                String body = new String(message.getBody(), "UTF-8");
                log.info("消费消息，messageId={}，topic={}, key={}, tag={}, body={}",
                        message.getMsgID(), message.getTopic(), message.getKey(), message.getTag(), body);
                if (message.getTag().equals("TAG_PAYMENT_SHOPKEEPER")) {
                    /**订单支付成功，店主服务消费*/

                    return Action.ReconsumeLater;
                }
                return Action.CommitMessage;
            }catch (Exception e){
                log.error("MQ处理失败，message：{}, error={}", message.toString(), e.getMessage());
                return Action.ReconsumeLater;
            }
        };
    }
}
