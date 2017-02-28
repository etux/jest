package org.devera.jest.test.client;

import org.devera.jest.annotations.ReSTPathParam;

public class GetRequestWithPathParams
{
    @ReSTPathParam
    private String pathParam;

    public String getPathParam()
    {
        return pathParam;
    }

    public void setPathParam(String pathParam)
    {
        this.pathParam = pathParam;
    }
}
