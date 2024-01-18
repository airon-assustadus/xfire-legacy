package org.codehaus.xfire.jaxws;

import java.net.URL;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import jakarta.xml.ws.Endpoint;
import jakarta.xml.ws.EndpointReference;
import jakarta.xml.ws.WebServiceFeature;
import jakarta.xml.ws.soap.SOAPBinding;
import jakarta.xml.ws.spi.ServiceDelegate;

import jakarta.xml.ws.wsaddressing.W3CEndpointReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.transport.Transport;
import org.codehaus.xfire.transport.TransportManager;
import org.w3c.dom.Element;

public class Provider
    extends jakarta.xml.ws.spi.Provider
{
    private static final Log log = LogFactory.getLog(Provider.class);

    public Provider()
    {
    }
    
    @Override
    public ServiceDelegate createServiceDelegate(URL wsdlLocation,
                                                 QName serviceName,
                                                 Class serviceClass)
    {
        log.debug("Creating service delegate " + serviceName + " for service class " +
                  serviceClass.getName());
        
        return new org.codehaus.xfire.jaxws.ServiceDelegate(wsdlLocation, serviceName, serviceClass);
    }

    @Override
    public Endpoint createEndpoint(String bindingId, Object implementor)
    {
        org.codehaus.xfire.jaxws.Endpoint endpoint = 
            new org.codehaus.xfire.jaxws.Endpoint(bindingId, implementor);
        
        return endpoint;
    }

    @Override
    public Endpoint createAndPublishEndpoint(String address, Object implementor)
    {
        String bindingId;
        if (address.startsWith("http:") || address.startsWith("https:"))
        {
            bindingId = SOAPBinding.SOAP11HTTP_BINDING;
        }
        else
        {
            TransportManager transportManager = JAXWSHelper.getInstance().getTransportManager();
            Transport t = transportManager.getTransportForUri(address);
            bindingId = t.getSupportedBindings()[0];
        }
        
        org.codehaus.xfire.jaxws.Endpoint endpoint = 
            new org.codehaus.xfire.jaxws.Endpoint(bindingId, implementor);
        endpoint.publish(address);
        return endpoint;
    }

    @Override
    public EndpointReference readEndpointReference(Source eprInfoset) {
        log.error("Please do not use this method");
        return null;
    }

    @Override
    public <T> T getPort(EndpointReference endpointReference, Class<T> serviceEndpointInterface, WebServiceFeature... features) {
        log.error("Please do not use this method");
        return null;
    }

    @Override
    public W3CEndpointReference createW3CEndpointReference(String address, QName serviceName, QName portName, List<Element> metadata, String wsdlDocumentLocation, List<Element> referenceParameters) {
        log.error("Please do not use this method");
        return null;
    }

}
