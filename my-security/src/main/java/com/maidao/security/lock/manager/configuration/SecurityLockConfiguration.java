package com.maidao.security.lock.manager.configuration;

import com.maidao.security.lock.manager.interceptor.ClientInterceptor;
import com.maidao.security.lock.manager.interceptor.ServiceLockInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author: chenjw
 * @Date: 2017/12/6 下午4:45
 * @Description: App通用配置
 */
@Configuration
public class SecurityLockConfiguration extends WebMvcConfigurerAdapter {

    @Bean
    public ClientInterceptor clientInterceptor(){
        return new ClientInterceptor();
    }

    /**
     * 拦截器配置
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 校验拦截器
        registry.addInterceptor(new ServiceLockInterceptor());
    }
}
