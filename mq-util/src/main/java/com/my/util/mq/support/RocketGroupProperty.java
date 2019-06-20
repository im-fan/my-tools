package com.my.util.mq.support;

import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.shade.org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import java.util.Properties;

/**
 * @date 2019-01-28
 * @desc: RocketMq 构建属性
 */
public abstract class RocketGroupProperty {

    @Value("${aliyun.ons.accessKey:}")
    private String accessKey;

    @Value("${aliyun.ons.secretKey:}")
    private String secretKey;

    /**Name Server地址*/
    @Value("${aliyun.rocketmq.nameSrvAddr:}")
    private  String nameSrvAddr ;

    /**
     * 是否是阿里云RocketMQ
     * ONSClientAbstract
     * tcp内网接入点: http://onsaddr.cn-hangzhou.mq-internal.aliyuncs.com:8080
     * tcp公网接入点: http://onsaddr.mq-internet-access.mq-internet.aliyuncs.com
     * */
    private Boolean isAliyunRocketMQ ;

    /**消息主题*/
    protected  abstract  String getTopic();

    /**GroupId(ConsumerId || ProducerId)*/
    protected  abstract  String getGroupId();

    protected Properties groupProperties() {
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.AccessKey, getAccessKey());
        properties.setProperty(PropertyKeyConst.SecretKey, getSecretKey());
        properties.setProperty(PropertyKeyConst.GROUP_ID,  getGroupId());
        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR,getNameSrvAddr());
        return properties;
    }

    protected String getAccessKey() {
        return accessKey;
    }

    protected String getSecretKey() {
        return secretKey;
    }

    protected String getNameSrvAddr() {
        return nameSrvAddr;
    }

    protected boolean isAliyunRocketMQ() {
        if(isAliyunRocketMQ != null){
            return  isAliyunRocketMQ;
        }
        isAliyunRocketMQ = StringUtils.isBlank(nameSrvAddr) || StringUtils.contains(nameSrvAddr,"aliyuncs.com");
        return isAliyunRocketMQ;
    }
}
