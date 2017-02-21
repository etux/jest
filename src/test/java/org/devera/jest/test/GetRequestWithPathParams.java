package org.devera.jest.test;

import org.devera.jest.annotations.JeSTPathParam;

public class GetRequestWithPathParams
{
    @JeSTPathParam
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
