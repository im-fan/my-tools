package com.my.security.lock.manager.interceptor;

import com.maidao.commons.exception.BusinessSqlException;
import com.maidao.commons.model.base.dto.Resp;
import com.my.security.lock.manager.annotation.Security;
import com.my.security.lock.manager.model.SecurityConstant;
import com.my.security.lock.manager.model.TokenDto;
import com.my.security.lock.manager.util.JwtUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author chenjw
 * @Date 2017年09月05日
 */
public class ServiceLockInterceptor implements HandlerInterceptor {

    private Logger log = LoggerFactory.getLogger(ServiceLockInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) {
        Security annotation = ((HandlerMethod) o).getMethod().getAnnotation(Security.class);
        if (annotation == null) {
            return true;
        }

        String headerParam = request.getHeader(SecurityConstant.HeaderParam);
        String url = String.valueOf(request.getRequestURI());
        if(StringUtils.isBlank(headerParam)){
            throwException();
        }


        /** 解密token**/
        TokenDto token = JwtUtil.parseTokenDto(headerParam);
        if(token == null){
            throwException();
        }

        /** 校验方法名是否一致**/
        if(!token.getRequestUrl().equals(url)){
            throwException();
        }

        return true;
    }

    private void throwException(){
        throw new BusinessSqlException(Resp.failure("权限不足,禁止访问"));
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }


}
