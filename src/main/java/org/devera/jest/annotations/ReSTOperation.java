package org.devera.jest.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ReSTOperation {

    boolean supportsFilters() default false;
    boolean supportsPagination() default false;
    boolean supportsProjectors() default false;
    boolean isAuthenticated() default false;
    ReSTOperationMapping[] mappings() default {};

    String path() default "";

    String method() default "GET";

}
