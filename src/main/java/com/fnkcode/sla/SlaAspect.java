package com.fnkcode.sla;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SlaAspect {

    private static final Logger log = LoggerFactory.getLogger(SlaAspect.class);

    @Around("@annotation(com.fnkcode.sla.StrictSLA)")
    public Object enforceSla(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        StrictSLA strictSLA = AnnotationUtils.findAnnotation(method, StrictSLA.class);
        if (strictSLA == null) {
            return joinPoint.proceed();
        }
        long duration = strictSLA.unit().toMillis(strictSLA.value());
        long deadline = System.currentTimeMillis() + duration;

        log.info("SLA Aspect: Deadline set for {}ms from now", duration);

        try {
            SlaContext.setDeadline(deadline);
            return joinPoint.proceed();
        } finally {
            SlaContext.clear();
        }
    }
}