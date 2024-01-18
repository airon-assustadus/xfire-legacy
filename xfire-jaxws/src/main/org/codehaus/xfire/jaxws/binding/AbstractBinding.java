package org.codehaus.xfire.jaxws.binding;

import java.util.List;

import jakarta.xml.ws.Binding;
import jakarta.xml.ws.handler.Handler;

import org.codehaus.xfire.transport.Transport;

@SuppressWarnings("rawtypes")
public class AbstractBinding implements Binding
{
    private List<Handler> handlerChain;
    private final Transport transport;
    
    public AbstractBinding(Transport t)
    {
        super();
        this.transport = t;
    }

    public List<Handler> getHandlerChain()
    {
        return handlerChain;
    }

    public void setHandlerChain(List<Handler> handlerChain)
    {
        this.handlerChain = handlerChain;
    }

    @Override
    public String getBindingID() {
        return this.getClass().getSimpleName();
    }

    public Transport getTransport()
    {
        return transport;
    }
}
