package com.recruitment.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 请求日志拦截器
 * 每次请求进来记一行「请求开始」，结束时记一行「请求结束」，带上状态码和耗时。
 * 这里只做记录，不判断 Token，preHandle 始终返回 true。
 */
@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    /** 存开始时间的 request 属性名 */
    private static final String START_TIME = "requestStartTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME, System.currentTimeMillis());
        log.info("请求开始: {} {}", request.getMethod(), request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute(START_TIME);
        long duration = startTime != null ? System.currentTimeMillis() - startTime : 0;
        log.info("请求结束: {} {} | 状态: {} | 耗时: {}ms",
                request.getMethod(), request.getRequestURI(), response.getStatus(), duration);
    }
}
