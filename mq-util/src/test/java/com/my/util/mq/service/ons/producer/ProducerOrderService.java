package com.my.util.mq.service.ons.producer;

import com.my.util.mq.support.producer.ProducerOrderAbstract;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @email: runcoding@163.com
 * @created Time: 2017/12/26 15:51
 * @description 顺序消息生产者
 **/
@Service
public class ProducerOrderService extends ProducerOrderAbstract {

    @Value("${aliyun.rocketmq.order.producer.producer_id:}")
    private String producerId;

    @Value("${aliyun.rocketmq.order.producer.topic:}")
    private String topic;

    @Override
    public String getTopic() {
        return topic;
    }

    @Override
    public String getGroupId() {
        return producerId;
    }


}
