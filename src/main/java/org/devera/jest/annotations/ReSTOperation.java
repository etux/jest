package org.devera.jest.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ReSTOperation {

    ReSTOperationMapping[] mappings() default {};

    String path() default "";

    Operations method() default Operations.GET;

    enum Operations {
        GET,
        POST,
        PUT,
        DELETE,
        OPTIONS,
        HEAD
    }

}
