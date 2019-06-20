package com.my.util.mq.service.ons.producer;

import com.my.util.mq.support.producer.ProducerAbstract;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @email: runcoding@163.com
 * @created Time: 2017/12/26 15:51
 * @description 消息(普通、定时/延时消息)生产者
 **/
@Service
public class ProducerService extends ProducerAbstract {

    @Value("${aliyun.rocketmq.common.producer.groupId:PID_MD_ORDER_DEV}")
    private String groupId;

    @Value("${aliyun.rocketmq.common.producer.topic:MD_TOPIC_ORDER_DEV}")
    private String topic;

    @Override
    public String getTopic() {
        return topic;
    }

    @Override
    public String getGroupId() {
        return groupId;
    }


}
