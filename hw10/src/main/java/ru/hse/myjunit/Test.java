package ru.hse.myjunit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods with this annotation are treated as tests
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {
    /**
     * Expected exception class
     * @return expected exception class or void.class if not expected
     */
    Class<?> expected() default void.class;

    /**
     * Reason for disabled tests
     * @return reason for disabled tests or empty string for enabled
     */
    String ignore() default "";
}