package com.my.util.mq.function;


import com.aliyun.openservices.ons.api.Message;

/**
 * 注解具体业务
 *
 *@Date: 2019-06-20 17:20
 */
public interface TransactionCheckerFunction{

    /**判断是否成功提交了主业务的事务*/
    boolean isCommit(Message msg);

    /**
     * isCommit = false, 回滚占用的业务。
     * 该处理需支持，幂等性处理。
     * */
    boolean rollback(Message msg);
}
