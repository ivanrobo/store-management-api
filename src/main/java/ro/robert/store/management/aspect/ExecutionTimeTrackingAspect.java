package ro.robert.store.management.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import ro.robert.store.management.annotation.TrackExecutionTime;

import java.lang.reflect.Method;

/**
 * Aspect to handle execution time tracking for methods annotated with @TrackExecutionTime.
 */
@Slf4j
@Aspect
@Component
public class ExecutionTimeTrackingAspect {

    @Around("@annotation(ro.robert.store.management.annotation.TrackExecutionTime)")
    public Object trackExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        TrackExecutionTime annotation = method.getAnnotation(TrackExecutionTime.class);
        
        String operationName = annotation.value().isEmpty() ? 
            method.getName() : annotation.value();
        
        log.info("Starting operation: {}", operationName);
        
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            
            log.info("Operation: {} completed successfully in {} ms", operationName, executionTime);
            
            return result;
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            
            log.error("Operation: {} failed after {} ms with error: {}", 
                operationName, executionTime, e.getMessage());
            
            throw e;
        }
    }
}
