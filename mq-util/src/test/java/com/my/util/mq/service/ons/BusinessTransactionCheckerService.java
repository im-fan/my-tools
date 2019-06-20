package com.my.util.mq.service.ons;

import com.aliyun.openservices.ons.api.Message;
import com.my.util.mq.function.TransactionCheckerFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class BusinessTransactionCheckerService  implements TransactionCheckerFunction {


    @Override
    public boolean isCommit(Message msg) {
        log.info("开始状态检查");
        return false;
    }

    /***
     * 注意：
     * 1. 如果rollback 返回false,或者出错，将会多次进入状态检查。
     * 2. 推荐使用分布式锁控制， @RedisLock(key = "'orderTransaction:'+#msg.topic+':'+#msg.msgID",keyLogger = true)
     *    //redis控制并发访问。全局key: maidao:lock:orderTransaction:MD_TOPIC_TRANSACTION_LOCAL:1e149938d147f9f3c70855bfbc7b2667
     */
    @Override
    public boolean rollback(Message msg) {
        log.info("开始处理回滚业务占用的资源。由于无法知道，其他服务占用资源是否成功，其他业务必须做到幂等性处理。");
        String s = null;
        //s.toString();

        return true;
    }
}
