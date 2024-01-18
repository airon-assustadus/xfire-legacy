package org.codehaus.xfire.jaxws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.xml.transform.Source;
import jakarta.xml.ws.Binding;
import jakarta.xml.ws.BindingType;
import jakarta.xml.ws.EndpointReference;
import jakarta.xml.ws.soap.SOAPBinding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.jaxws.binding.AbstractBinding;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.invoker.BeanInvoker;
import org.w3c.dom.Element;

public class Endpoint
    extends jakarta.xml.ws.Endpoint
{
    private JAXWSHelper jaxWsHelper = JAXWSHelper.getInstance();
    private AbstractBinding binding;
    private Object implementor;
    private boolean published;
    private List<Source> metadata;
    private Executor executor;
    private Service service;

    private static final Log log = LogFactory.getLog(Endpoint.class);

    private Map<String,Object> properties = new HashMap<String,Object>();
    
    public Endpoint(String bindingId, Object implementor)
    {
        // Try the BindingType annotation
        if (bindingId == null)
        {
            BindingType type = implementor.getClass().getAnnotation(BindingType.class);
            if (type != null)
            {
                bindingId = type.value();
            }
        }

        // We now must use the SOAP 1.1 HTTP binding
        if (bindingId == null || bindingId.length() == 0)
        {
            bindingId = SOAPBinding.SOAP11HTTP_BINDING;
        }
        
        this.binding = jaxWsHelper.getBinding(bindingId);

        if (binding == null)
        {
            throw new IllegalStateException("Could not find binding: " + bindingId);
        }
        
        this.implementor = implementor;
        
        this.service = jaxWsHelper.getServiceFactory().create(implementor.getClass());
        this.service.setInvoker(new BeanInvoker(implementor));
    }

    @Override
    public Binding getBinding()
    {
        return binding;
    }

    @Override
    public Object getImplementor()
    {
        return implementor;
    }

    @Override
    public void publish(String address)
    {
        if (published)
        {
            throw new IllegalStateException("Endpoint has already been published.");
        }
        
        jaxWsHelper.getXFire().getServiceRegistry().register(service);

        published = true;
    }

    @Override
    public void publish(Object context)
    {
        if (published)
        {
            throw new IllegalStateException("Endpoint has already been published.");
        }
        
        published = true;
    }

    @Override
    public void stop()
    {
        if (published)
        {
            jaxWsHelper.getXFire().getServiceRegistry().unregister(service);

            published = false;
        }
    }

    @Override
    public boolean isPublished()
    {
        return published;
    }

    @Override
    public List<Source> getMetadata()
    {
        return metadata;
    }

    @Override
    public void setMetadata(List<Source> metadata)
    {
        this.metadata = metadata;
    }

    @Override
    public Executor getExecutor()
    {
        return executor;
    }

    @Override
    public void setExecutor(Executor executor)
    {
        this.executor = executor;
    }

    @Override
    public Map<String, Object> getProperties()
    {
        return properties;
    }

    @Override
    public void setProperties(Map<String, Object> properties)
    {
        this.properties = properties;
    }

    @Override
    public EndpointReference getEndpointReference(Element... referenceParameters) {
        log.error("Please do not use this method");
        return null;
    }

    @Override
    public <T extends EndpointReference> T getEndpointReference(Class<T> clazz, Element... referenceParameters) {
        log.error("Please do not use this method");
        return null;
    }
}
