package com.my.util.mq.model.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderParam {

    /**用户id*/
    private Long userId;

    /**用户名*/
    private String userName;

    /**交易号*/
    private String  tradeNum;

}
