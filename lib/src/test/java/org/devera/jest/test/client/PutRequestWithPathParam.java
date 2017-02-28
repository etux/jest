package org.devera.jest.test.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.devera.jest.annotations.ReSTPathParam;

import java.util.UUID;

public class PutRequestWithPathParam
{
    @ReSTPathParam
    @JsonIgnore
    private UUID identifier;
    private String body;

    public UUID getIdentifier()
    {
        return identifier;
    }

    public void setIdentifier(UUID identifier)
    {
        this.identifier = identifier;
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
