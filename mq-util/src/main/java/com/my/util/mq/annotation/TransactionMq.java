package com.my.util.mq.annotation;

import com.my.util.mq.support.producer.TransactionProducerAbstract;

import java.lang.annotation.*;

/**
 *消息注解
 *@Date: 2019-06-20 16:59
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface TransactionMq {

    /**mq tag, 由业务自定义*/
    String tag();

    /**获取业务码(业务的唯一标识)*/
    String  key() default "";

    /**
     mq 消息内容
     @TransactionMq(tag = "TAG_ORDER_TRANSACTION_LOCAL",body = "'{\"}userInfo:'+#po.mobile+':'+#po.id")
     public Order createOrder(orderAdd po) {
         //扣减库存(操作商品库，事务独立
         //使用优惠券(操作优惠券库，事务独立
         //
     }
     */
    String body();

    /**事务消息提供者*/
    Class<?> producer() default TransactionProducerAbstract.class;


}