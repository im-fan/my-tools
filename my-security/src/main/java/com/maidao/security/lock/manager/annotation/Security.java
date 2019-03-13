package com.maidao.security.lock.manager.annotation;

import com.maidao.security.lock.manager.configuration.SecurityLockConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: weiyf
 * @Date: 2018/1/6 上午11:36
 * @Description: 开启安全验证
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Import(SecurityLockConfiguration.class)
public @interface Security {
}
