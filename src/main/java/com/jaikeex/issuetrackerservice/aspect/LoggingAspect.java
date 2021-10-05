package com.jaikeex.issuetrackerservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("within(@org.springframework.stereotype.Repository *)" +
            " || within(@org.springframework.stereotype.Service *)" +
            " || within(@org.springframework.web.bind.annotation.RestController *)")
    public void springBeanPointcut() {}

    @Pointcut("within(com.jaikeex.issuetrackerservice..*)" +
            " || within(com.jaikeex.issuetrackerservice.service..*)" +
            " || within(com.jaikeex.issuetrackerservice.controller..*)")
    public void applicationPackagePointcut() {}

    @Around("applicationPackagePointcut() && springBeanPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
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

    private void logIllegalArgumentException(ProceedingJoinPoint joinPoint) {
        log.error("Illegal argument: {} in {}.{}()",
                Arrays.toString(joinPoint.getArgs()),
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
    }

    private void logWhenEnteringMethodBody(ProceedingJoinPoint joinPoint) {
        log.debug("Enter: {}.{}()",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
    }

    private void logWhenExitingMethodBody(ProceedingJoinPoint joinPoint) {
        log.debug("Exit: {}.{}()",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
    }

}
