package ro.robert.store.management.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to track the execution time of methods.
 * When applied to a method, it will log the time taken to execute that method.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TrackExecutionTime {
    
    /**
     * Optional custom name for the operation being tracked.
     * If not provided, the method name will be used.
     */
    String value() default "";
}
