package org.codehaus.xfire.jaxws;

import jakarta.xml.ws.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.service.OperationInfo;

import javax.xml.transform.Source;
import java.util.Map;
import java.util.concurrent.Future;

public class JAXBDispatch
    implements jakarta.xml.ws.Dispatch<Source>
{
    private Client client;
    private Service.Mode mode;
    private OperationInfo opInfo;

    private static final Log log = LogFactory.getLog(JAXBDispatch.class);
    
    public JAXBDispatch(Client client)
    {
        this.client = client;
    
        opInfo = client.getService().getServiceInfo().getOperation("invoke");
    }

    public Source invoke(Source source)
    {
        try
        {
            Object[] result = client.invoke(opInfo, new Object[] { source });
            if (result.length == 0) return null;
            
            return (Source) result[0];
        }
        catch (Exception e)
        {
            throw new WebServiceException(e);
        }
    }

    public Future< ? > invokeAsync(Source source, AsyncHandler<Source> arg1)
    {
        log.error("Please do not use this method");
        return null;
    }

    public Response<Source> invokeAsync(Source source)
    {
        log.error("Please do not use this method");
        return null;
    }

    public void invokeOneWay(Source source)
    {
        log.error("Please do not use this method");
        
    }

    public Binding getBinding()
    {
        log.error("Please do not use this method");
        return null;
    }

    @Override
    public EndpointReference getEndpointReference() {
        log.error("Please do not use this method");
        return null;
    }

    @Override
    public <T extends EndpointReference> T getEndpointReference(Class<T> clazz) {
        log.error("Please do not use this method");
        return null;
    }

    public Map<String, Object> getRequestContext()
    {
        log.error("Please do not use this method");
        return null;
    }

    public Map<String, Object> getResponseContext()
    {
        log.error("Please do not use this method");
        return null;
    }

    public Service.Mode getMode()
    {
        return mode;
    }

    public void setMode(Service.Mode mode)
    {
        this.mode = mode;
    }

    
}
