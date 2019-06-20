package com.my.util.mq.model.param;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 返回实体包装类
 * @date 2018年11月12日 下午8:19:10
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Resp<E>  {

    private Integer status;

    private String message;

    private E data;

}