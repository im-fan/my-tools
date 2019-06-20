package com.my.util.mq.support.rocket.impl;

import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.impl.ONSFactoryImpl;
import com.aliyun.openservices.ons.api.impl.rocketmq.ONSUtil;

import java.util.Properties;


public class RocketMqFactoryImpl extends ONSFactoryImpl {

    @Override
    public Producer createProducer(Properties properties) {
        return new RocketMqProducerImpl(ONSUtil.extractProperties(properties));
    }

}
