package com.my.util.mq.service;

import com.alibaba.fastjson.JSON;
import com.my.util.mq.model.param.OrderParam;
import com.my.util.mq.annotation.TransactionMq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @desc 测试
 */
@Service
public class OrderTestService {

    private Logger log  = LoggerFactory.getLogger(OrderTestService.class);


    @TransactionMq(tag = "TAG_ORDER_TRANSACTION_LOCAL",body = "'{\"tradeNum\":'+#orderParam.tradeNum+'}'")
    public boolean addOrder(OrderParam orderParam) {
        log.info("创建订单"+ JSON.toJSONString(orderParam));
        log.info("扣减库存(操作商品库，事务独立)");
        log.info("使用优惠券(操作优惠券库，事务独立)");
        String a = null;
       // String s = a.toString();
        /***--------共享交易库-----**/
        log.info("删除购物车商品");
        log.info("创建订单");
        /***--------共享交易库-----**/
        return true;
    }



}
