package com.jaikeex.issuetrackerservice.aspect.performance;

import org.apache.commons.logging.Log;
import org.springframework.aop.interceptor.PerformanceMonitorInterceptor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

public class PerformanceMonitor extends PerformanceMonitorInterceptor {

    private boolean logExceptionStackTrace = true;

    public PerformanceMonitor(boolean useDynamicLogger) {
        setUseDynamicLogger(useDynamicLogger);
    }

    @Override
    protected void writeToLog(Log logger, String message, @Nullable Throwable ex) {
        if (ex != null && this.logExceptionStackTrace) {
            logger.info(message, ex);
        }
        else {
            logger.info(message);
        }
    }

    @Override
    public void setLogExceptionStackTrace(boolean logExceptionStackTrace) {
        this.logExceptionStackTrace = logExceptionStackTrace;
    }
}
