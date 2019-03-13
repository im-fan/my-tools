package com.maidao.statistics.web.model;

import java.io.Serializable;

/**
 * 返回信息
 *
 *@author: Weiyf
 *@Date: 2018/10/12 16:54
 */
public class Resp<E> implements Serializable {

    private int status;
    private String message;
    private E data;

    //成功
    public static final Integer SuccessStatus = 200;

    //失败
    public static final Integer FailStatus = 400;

    public Resp(int status,String message,E data){
        this.status = status;
        this.message = message;
        this.data = data;
    }

    /**  成功**/
    public static <T> Resp<T> success(T data){
        return new Resp(SuccessStatus,"成功",data);
    }

    public static <T> Resp<T> success(T data,String message){
        return new Resp(SuccessStatus,message,data);
    }

    public static Resp success(String message){
        return new Resp(SuccessStatus,message,null);
    }

    /**  失败**/
    public static Resp failed(String message){
        return new Resp(FailStatus,message,null);
    }

    public boolean isSuccess(){
        return this.status == SuccessStatus;
    }

    public boolean isFailed(){
        return this.status == FailStatus;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public E getData() {
        return data;
    }

    public void setData(E data) {
        this.data = data;
    }
}
