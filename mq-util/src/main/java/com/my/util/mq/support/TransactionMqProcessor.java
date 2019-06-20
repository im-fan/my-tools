package com.my.util.mq.support;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.transaction.TransactionProducer;
import com.aliyun.openservices.ons.api.transaction.TransactionStatus;
import com.my.util.mq.annotation.TransactionMq;
import com.my.util.mq.function.TransactionCheckerFunction;
import com.my.util.mq.support.producer.TransactionProducerAbstract;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.atomic.AtomicReference;


/**
 *
 * 针对添加@TransactionMq 注解的方法进行缓存
 * @date 2017-12-01
 *
 * @Order(100)
 *   50(start)-->
 *             100(start)-->
 *                         Integer.MAX_VALUE(start)-->
 *                         Integer.MAX_VALUE(end)<--
 *             100(end)<--
 *   50(end)<--
 */
@Aspect
@Component
@Order(100)
public class TransactionMqProcessor implements ApplicationContextAware {

    private Logger log = LoggerFactory.getLogger(TransactionMqProcessor.class);

    private ExpressionParser parser = new SpelExpressionParser();

    /**上下文对象实例 */
    private ApplicationContext applicationContext;

    @Pointcut("@annotation(com.maidao.util.mq.annotation.TransactionMq)")
    public void mqMethod(){}

    @Around("mqMethod() && @annotation(mq)")
    public Object transaction(ProceedingJoinPoint jp, TransactionMq mq) throws Throwable {
        TransactionProducerAbstract transactionProducer = (TransactionProducerAbstract) getBean(mq.producer());
        String tag  = mq.tag();
        String body = getBody(jp, mq.body());
        String key  = mq.key();
        Message msg = new Message(transactionProducer.getTopic(), tag, body.getBytes());
        if(!StringUtils.isEmpty(key)){
           msg.setKey(getBody(jp,key));
        }

        TransactionProducer producer = transactionProducer.getProducer();
        TransactionCheckerFunction transactionCheckerFunction = transactionProducer.getTransactionCheckerFunction();

        AtomicReference<Object> result = new AtomicReference();
        AtomicReference<Throwable> businessThrowable = new AtomicReference();
        try{
            SendResult sendResult = producer.send(msg, (msg1, arg) -> {
                try {
                    result.set(jp.proceed());
                    return TransactionStatus.CommitTransaction;
                } catch (Throwable e) {
                    businessThrowable.set(e);
                    log.error("开始回滚分布式事务占用的资源。Topic={},Key={},MsgID={}",msg.getTopic(),msg.getKey(),msg.getMsgID(),e);
                    boolean rollbackStatus = transactionCheckerFunction.rollback(msg);
                    return rollbackStatus ? TransactionStatus.RollbackTransaction : TransactionStatus.Unknow ;
                }
            }, null);
           log.info("消息事务Half：Topic={},MsgId={},",sendResult.getTopic(),sendResult.getMessageId());
        }catch (Throwable e){
            if (businessThrowable.get() == null){
                throw  e;
            }
            /**向外抛出业务异常*/
            throw  businessThrowable.get();
        }

        if (businessThrowable.get() != null){
            throw  businessThrowable.get();
        }
        return result.get();
    }

    /**获取数据*/
    public String getBody(ProceedingJoinPoint jp, String key) {
        MethodSignature signature = (MethodSignature)jp.getSignature();
        /**@EnableTransactionManagement(proxyTargetClass = true) 必须启用CGLIB 代理*/
        String[] parameterNames =  signature.getParameterNames();
        Object[] args = jp.getArgs();
        StandardEvaluationContext context = new StandardEvaluationContext();
        if(parameterNames != null){
            for (int i = 0; i < parameterNames.length ; i++) {
                String parameterName =  parameterNames[i];
                context.setVariable(parameterName,args[i]);
            }
        }
        return String.valueOf(parser.parseExpression(key).getValue(context));
    }

    /**获取bean*/
    public <T> T getBean(Class<T> requiredType) throws BeansException{
        return applicationContext.getBean(requiredType);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}