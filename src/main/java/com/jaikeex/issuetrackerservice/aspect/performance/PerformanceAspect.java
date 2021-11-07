package com.jaikeex.issuetrackerservice.aspect.performance;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.interceptor.PerformanceMonitorInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Aspect
@Slf4j
public class PerformanceAspect {

    private static final String PERFORMANCE_MONITOR_POINTCUT = "com.jaikeex.issuetrackerservice.aspect.pointcut.PointcutConfig.performanceMonitorPointcut()";

    @Bean
    public PerformanceMonitorInterceptor performanceMonitor() {
        return new PerformanceMonitorInterceptor(false);
    }

    @Bean
    public Advisor performanceMonitorAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(PERFORMANCE_MONITOR_POINTCUT);
        return new DefaultPointcutAdvisor(pointcut, performanceMonitor());
    }
}
