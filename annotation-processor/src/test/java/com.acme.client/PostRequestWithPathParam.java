package com.acme.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.devera.jest.annotations.ReSTPathParam;

public class PostRequestWithPathParam
{
    @ReSTPathParam
    @JsonIgnore
    private String pathName;

    private String body;

    public String getPathName()
    {
        return pathName;
    }

    public void setPathName(String pathName)
    {
        this.pathName = pathName;
    }

    public String getBody()
    {
        return body;
    }

    public void setBody(String body)
    {
        this.body = body;
    }
}
