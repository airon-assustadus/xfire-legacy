package org.codehaus.xfire.aegis;

public class Holder
{
    private Object value;

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }

    public String toString()
    {
        return super.toString() + "[" + value + "]";
    }
}