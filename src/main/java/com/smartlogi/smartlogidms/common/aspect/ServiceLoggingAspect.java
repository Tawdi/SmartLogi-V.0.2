package com.smartlogi.smartlogidms.common.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(ServiceLoggingAspect.class);

    private final ObjectWriter prettyWriter;

    @Autowired
    public ServiceLoggingAspect(ObjectMapper objectMapper) {
        this.prettyWriter = objectMapper.writerWithDefaultPrettyPrinter();
    }

    @Pointcut("execution(* com.smartlogi.smartlogidms..*Service*.*(..))")
    public void serviceMethods() {
    }

    @Before("serviceMethods()")
    public void logBefore(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String argsJson = serializeToJson(joinPoint.getArgs());
        logger.info("Entering method: {}.{} with arguments: \n {}",
                className, methodName, argsJson);
    }

    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String resultJson = serializeToJson(result);
        logger.info("Method {}.{} returned:\n {}",
                className, methodName, resultJson);
    }

    @AfterThrowing(pointcut = "serviceMethods()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        logger.error("Exception in method {}.{}: {}",
                className, methodName, exception.getMessage());
    }

    @Around("serviceMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long timeTaken = System.currentTimeMillis() - startTime;
        logger.info("Method {}.{} executed in {} ms",
                className, methodName, timeTaken);
        return result;
    }

    private String serializeToJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        try {
            return prettyWriter.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.warn("Failed to serialize to JSON: {}", e.getMessage());
            return obj.toString();  // Fallback to default toString()
        }
    }

}