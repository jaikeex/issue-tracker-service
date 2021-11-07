package com.jaikeex.issuetrackerservice.aspect.pointcut;

import org.aspectj.lang.annotation.Pointcut;

public class PointcutConfig {

    @Pointcut("serviceAnnotation() || controllerAnnotation() || repositoryAnnotation()")
    public void springBeanPointcut(){}

    @Pointcut("serviceAnnotation() || repositoryAnnotation()")
    public void performanceMonitorPointcut(){}

    @Pointcut("within(com.jaikeex.issuetrackerservice..*)")
    public void applicationPackagePointcut() {}

    @Pointcut("execution(public * (@org.springframework.stereotype.Service com.jaikeex..*).*(..))")
    public void serviceAnnotation(){}

    @Pointcut("execution(public * (@org.springframework.web.bind.annotation.RestController com.jaikeex..*).*(..))")
    public void controllerAnnotation(){}

    @Pointcut("execution(public * (@org.springframework.stereotype.Repository com.jaikeex..*).*(..))")
    public void repositoryAnnotation(){}

}
