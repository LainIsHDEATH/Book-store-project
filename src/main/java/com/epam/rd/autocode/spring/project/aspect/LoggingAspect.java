package com.epam.rd.autocode.spring.project.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut(
            "within(com.epam.rd.autocode.spring.project.controller..*) || " +
                    "within(com.epam.rd.autocode.spring.project.service..*) || " +
                    "within(com.epam.rd.autocode.spring.project.repo..*)"
    )
    public void allLayers() {}

    @Around("allLayers()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();

        log.info("===> START: {}", methodName);

        try {
            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - start;
            log.info("<=== END: {} | Finished in: {} ms",
                    methodName, executionTime);

            return result;

        } catch (Throwable error) {

            long executionTime = System.currentTimeMillis() - start;
            log.error("!!! ERROR: {} | Failed after: {} ms | Message: {}",
                    methodName, executionTime, error.getMessage());

            throw error;
        }
    }
}
