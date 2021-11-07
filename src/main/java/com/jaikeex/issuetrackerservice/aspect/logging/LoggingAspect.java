package com.jaikeex.issuetrackerservice.aspect.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Aspect
@Slf4j
@EnableAspectJAutoProxy
public class LoggingAspect {

    @Around("com.jaikeex.issuetrackerservice.aspect.pointcut.PointcutConfig.springBeanPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        //Logs all method enter and exit points including the arguments used.
        logWhenEnteringMethodBody(joinPoint);
        try {
            Object result = joinPoint.proceed();
            logWhenExitingMethodBody(joinPoint);
            return result;
        } catch (IllegalArgumentException exception) {
            logIllegalArgumentException(joinPoint);
            throw exception;
        }
    }

    @AfterThrowing(pointcut = "com.jaikeex.issuetrackerservice.aspect.pointcut.PointcutConfig.springBeanPointcut()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        //Logs all exceptions thrown from the pointcut classes.
        log.warn("Exception thrown from {}.{}(); cause = {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                exception.getCause() != null ? exception.getCause() : "NULL");
    }

    private void logIllegalArgumentException(JoinPoint joinPoint) {
        log.error("Illegal argument: {} in {}.{}()",
                Arrays.toString(joinPoint.getArgs()),
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
    }

    private void logWhenEnteringMethodBody(JoinPoint joinPoint) {
        log.debug("Enter [method={}.{}({})]",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                getArgsAsString(joinPoint));
    }

    private void logWhenExitingMethodBody(JoinPoint joinPoint) {
        log.debug("Exit [method={}.{}({})]",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                getArgsAsString(joinPoint));
    }

    private String getArgsAsString(JoinPoint joinPoint) {
        return Arrays.toString(joinPoint.getArgs());
    }
}
