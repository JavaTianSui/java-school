package com.nowcoder.community.controller.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * create by: tiansui
 * description: TODO
 * create time:  2022-08-21
 *测试

 * @return
 */
@Component
public class AlphaInterceptor implements HandlerInterceptor {
    //在controller之前执行

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }
}
