package org.devera.jest.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AnnotationTest {

    @Test
    public void getValue() throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        TestObject object = new TestObject();

        Field field = object.getClass().getField("pathParam");

        Annotation annotation = field.getAnnotation(ReSTPathParam.class);

        Method method = annotation.annotationType().getDeclaredMethod("value");

        assertThat(
            method.invoke(annotation),
            is("attributeValue")
        );
    }

    private class TestObject {

        @ReSTPathParam(value = "attributeValue")
        public String pathParam;

    }
}
