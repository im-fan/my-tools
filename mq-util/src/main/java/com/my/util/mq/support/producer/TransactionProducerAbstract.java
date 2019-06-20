package com.my.util.mq.support.producer;

import com.alibaba.fastjson.JSON;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.transaction.TransactionProducer;
import com.aliyun.openservices.ons.api.transaction.TransactionStatus;
import com.my.util.mq.function.TransactionCheckerFunction;
import com.my.util.mq.support.RocketGroupProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Properties;


public abstract class TransactionProducerAbstract extends RocketGroupProperty {

    private Logger logger = LoggerFactory.getLogger(TransactionProducerAbstract.class);

    @Value("${aliyun.rocketmq.sendTimeoutMillis:5000}")
    private String sendTimeoutMillis;

    /** 在消息属性中添加第一次消息回查的最快时间，单位秒。例如，以下设置实际第一次回查时间为 8秒(后浮动0~5秒之间)*/
    @Value("${aliyun.rocketmq.checkImmunityTimeInSeconds:8}")
    private String checkImmunityTimeInSeconds;

    private  TransactionProducer producer ;


    public String getSendTimeoutMillis() {
        return sendTimeoutMillis;
    }

    public String getCheckImmunityTimeInSeconds() {
        return checkImmunityTimeInSeconds;
    }

    /**
     * 本地事务回查，操作检查和回滚
     * @return TransactionCheckerFunction
     */
    public abstract TransactionCheckerFunction getTransactionCheckerFunction();

    public TransactionProducer getProducer() {
        return producer;
    }

    @PostConstruct
    public void  start(){
        Properties properties = groupProperties();
        properties.setProperty(PropertyKeyConst.SendMsgTimeoutMillis, getSendTimeoutMillis());
        properties.setProperty(PropertyKeyConst.CheckImmunityTimeInSeconds,getCheckImmunityTimeInSeconds());
        producer = ONSFactory.createTransactionProducer(properties, msg -> {
            logger.info("开始事务状态(Unknow)检查={}", JSON.toJSONString(msg));
            String msgId = msg.getMsgID();
            TransactionCheckerFunction transactionCheckerFunction = getTransactionCheckerFunction();
            try {
                boolean isCommit = transactionCheckerFunction.isCommit(msg);
                if (isCommit) {
                    logger.info("事务消息检查，业务处理成功。msgId={}",msgId);
                    /**本地事务处理成功，消息开始往下投递*/
                    return TransactionStatus.CommitTransaction;
                } else {
                    logger.info("分布式事务开始补偿。msgId={}",msgId);
                    /**本地事务已失败则回滚消息*/
                    boolean rollbackStatus = transactionCheckerFunction.rollback(msg);
                    return rollbackStatus ? TransactionStatus.RollbackTransaction : TransactionStatus.Unknow ;
                }
            } catch (Throwable e) {
                logger.error("分布式消息事务，检查处理失败,msgId={}", msgId, e);
            }
            return TransactionStatus.Unknow;
        });
        producer.start();
        logger.info("start 事务消息服务提供者。GroupId(ProducerId)={}", getGroupId());
    }

    @PreDestroy
    public void shutdown() {
        logger.info("shutdown 事务消息服务提供者。GroupId(ProducerId)={}", getGroupId());
        producer.shutdown();
    }
}
