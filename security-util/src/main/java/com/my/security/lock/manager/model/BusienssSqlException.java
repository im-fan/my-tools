package com.my.security.lock.manager.model;

public class BusienssSqlException extends Exception{
    public BusienssSqlException(Resp resp){
        super(resp.getMsg());
    }
}
