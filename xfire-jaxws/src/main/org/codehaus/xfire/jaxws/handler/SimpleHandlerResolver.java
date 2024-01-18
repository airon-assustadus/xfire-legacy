package org.codehaus.xfire.jaxws.handler;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.ws.handler.Handler;
import jakarta.xml.ws.handler.HandlerResolver;
import jakarta.xml.ws.handler.PortInfo;

public class SimpleHandlerResolver
    implements HandlerResolver
{
    private List<Handler> handlers = new ArrayList<Handler>();

    public List<Handler> getHandlerChain(PortInfo portInfo)
    {
        return handlers;
    }

    public List<Handler> getHandlers()
    {
        return handlers;
    }

    public void setHandlers(List<Handler> handlers)
    {
        this.handlers = handlers;
    }
}
