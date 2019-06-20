package com.my.util.mq.service.ons.producer;

import com.my.util.mq.function.TransactionCheckerFunction;
import com.my.util.mq.support.producer.TransactionProducerAbstract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @email: runcoding@163.com
 * @created Time: 2017/12/26 15:51
 * @description 订单ons生产者服务
 **/
@Service
public class TransactionProducerService  extends TransactionProducerAbstract {

    @Value("${aliyun.rocketmq.transaction.producer.groupId:}")
    private String groupId;

    @Value("${aliyun.rocketmq.transaction.producer.topic:}")
    private String topic;

    @Autowired
    private TransactionCheckerFunction transactionCheckerFunction;

    @Override
    public String getTopic() {
        return topic;
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    @Override
    public TransactionCheckerFunction getTransactionCheckerFunction() {
        return transactionCheckerFunction;
    }

}
