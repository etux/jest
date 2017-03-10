package org.devera.jest.client.params;

public abstract class NamedParam
{
    private final String name;
    private final Object value;

    public NamedParam(final String name,
                      final Object value)
    {
        this.name = name;
        this.value = value;
    }

    public String getName()
    {
        return name;
    }

    public Object getValue()
    {
        return value;
    }
}
