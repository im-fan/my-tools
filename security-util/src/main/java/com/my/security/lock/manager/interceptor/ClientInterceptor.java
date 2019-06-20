package com.my.security.lock.manager.interceptor;

import com.my.security.lock.manager.model.SecurityConstant;
import com.my.security.lock.manager.model.TokenDto;
import com.my.security.lock.manager.util.JwtUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * 客户端拦截器
 *
 *@author: Weiyf
 *@Date: 2018/9/19 16:50
 */
public class ClientInterceptor implements RequestInterceptor {

    /** 请求时增加请求头，应用名称**/
    @Override
    public void apply(RequestTemplate template) {

        String method = template.method();
        String requestUrl = template.request().url();

        TokenDto dto = TokenDto.builder()
                .requestUrl(requestUrl)
                .methodName(method)
                .build();

        /** 生成token,过期时间3分钟 **/
        String md5String = JwtUtil.createJWT(dto,SecurityConstant.JwtRefreshTtl);

        template.header(SecurityConstant.HeaderParam, md5String);
    }

}
