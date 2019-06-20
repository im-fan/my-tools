package com.my.util.mq.test;

import com.my.util.mq.TestApplication;
import com.my.util.mq.model.param.OrderParam;
import com.my.util.mq.service.OrderTestService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static java.lang.Long.*;

/**
 * @date 2018年8月1日
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
@Slf4j
public class OrderTest {

    @Autowired
    private OrderTestService orderTestService;


    @Test
    public void testAddOrder() {
        log.info("testAddOrder start");
        try{
            OrderParam orderParam = OrderParam.builder().tradeNum("201821868").build();
            orderTestService.addOrder(orderParam);
          log.info("testAddOrder end");
        }catch (Throwable e){
           log.error("出现了异常:{}",e);
        }
        try {
            Thread.sleep(MAX_VALUE);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        log.info("testAddOrder errr");
    }

}
