package com.my.security.lock.manager.model;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenDto {

    /** 请求方法地址**/
    private String requestUrl;

    /** 方法名**/
    private String methodName;

}
