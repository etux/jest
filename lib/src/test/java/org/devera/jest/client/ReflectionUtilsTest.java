package org.devera.jest.client;

import java.lang.reflect.Method;
import java.util.Map;

import org.devera.jest.annotations.ReSTClient;
import org.devera.jest.annotations.ReSTOperation;
import org.devera.jest.annotations.ReSTPathParam;
import org.junit.Test;

import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

public class ReflectionUtilsTest
{

    @Test
    public void getPathParams_from_namedParam_array() {
        final NamedParam[] namedParams = new NamedParam[] {
            new NamedParam("name1", "value1"),
            new NamedParam("name2", 2)
        };

        Map<String, ?> pathParams = ReflectionUtils.getPathParams(namedParams);

        assertThat(
            pathParams,
            hasEntry("name1", "value1")
        );

        assertThat(
            pathParams,
            hasEntry("name2", 2)
        );
    }

    @Test
    public void findMethod() {

        Client clientInstance = new Client() {};
        Object request = new Object();

        Method method = ReflectionUtils.findMethod(clientInstance, "method");

        assertThat(method, is(notNullValue()));

        assertThat(method.getParameterCount(), is(1));
    }

    @Test
    public void findMethod_with_path_param() {
        Client clientInstance = new Client() {};
        Object request = new Object();
        String pathParam = "pathValue";

        Method method = ReflectionUtils.findMethod(clientInstance, "methodWithPathParams");

        assertThat(method, is(notNullValue()));
        assertThat(method.getParameterCount(), is(2));
    }

    @ReSTClient
    public interface Client {

        default String method(Object request) {
            return "Hello world";
        }

        @ReSTOperation
        default String methodWithPathParams(Object request, @ReSTPathParam String pathParam) {
            return "Hello world of path params";
        }
    }
}
