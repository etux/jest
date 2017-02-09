package org.devera.jest.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface ReSTClient {

    Protocol protocol() default Protocol.HTTP;

    String contextPath() default "/";


}
