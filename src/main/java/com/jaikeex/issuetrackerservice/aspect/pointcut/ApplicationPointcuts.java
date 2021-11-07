package com.jaikeex.issuetrackerservice.aspect.pointcut;

import org.aspectj.lang.annotation.Pointcut;


/**
 * Convenience class used to declare all pointcuts used in the application's AOP.
 */
public class ApplicationPointcuts {

    /**
     * Pointcut matching all Controller, Service and Repository spring beans.
     */
    @Pointcut("serviceAnnotation() || controllerAnnotation() || repositoryAnnotation()")
    public void springBeanPointcut(){}

    /**
     * Pointcut used for performance monitoring.
     */
    @Pointcut("serviceAnnotation() || repositoryAnnotation()")
    public void performanceMonitorPointcut(){}

    /**
     * Pointcut matching every class in the main package.
     */
    @Pointcut("within(com.jaikeex.issuetrackerservice..*)")
    public void applicationPackagePointcut() {}

    /**
     * Pointcut matching all classes annotated with
     * {@link org.springframework.stereotype.Service} annotation.
     */
    @Pointcut("execution(public * (@org.springframework.stereotype.Service com.jaikeex..*).*(..))")
    public void serviceAnnotation(){}

    /**
     * Pointcut matching all classes annotated with
     * {@link org.springframework.web.bind.annotation.RestController} annotation.
     */
    @Pointcut("execution(public * (@org.springframework.web.bind.annotation.RestController com.jaikeex..*).*(..))")
    public void controllerAnnotation(){}

    /**
     * Pointcut matching all classes annotated with
     * {@link org.springframework.stereotype.Repository} annotation.
     */
    @Pointcut("execution(public * (@org.springframework.stereotype.Repository com.jaikeex..*).*(..))")
    public void repositoryAnnotation(){}

}
