package com.my.security.lock.manager.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Resp<T> {

    private Integer code;
    private String msg;
    private T data;

    public Resp failure(String msg){
        this.code = 4000;
        this.msg = msg;
        return this;
    }

}
