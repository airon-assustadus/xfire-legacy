package org.codehaus.xfire.jaxws;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import jakarta.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import jakarta.xml.ws.Dispatch;
import jakarta.xml.ws.EndpointReference;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.Service.Mode;
import jakarta.xml.ws.WebServiceFeature;
import jakarta.xml.ws.handler.HandlerResolver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.xfire.annotations.AnnotationServiceFactory;
import org.codehaus.xfire.client.Client;
import org.codehaus.xfire.client.XFireProxyFactory;
import org.codehaus.xfire.jaxws.handler.SimpleHandlerResolver;
import org.codehaus.xfire.service.Endpoint;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceFactory;
import org.codehaus.xfire.transport.Transport;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ServiceDelegate
    extends jakarta.xml.ws.spi.ServiceDelegate
{
    private final JAXWSHelper jaxWsHelper = JAXWSHelper.getInstance();
    private final XFireProxyFactory factory = jaxWsHelper.getProxyFactory();
    private final ServiceFactory serviceFactory = jaxWsHelper.getServiceFactory();

    private URL wsdlLocation;
    private Executor executor;
    private HandlerResolver handlerResolver;
    private QName serviceName;
    
    private final Map<QName, Service> port2Service = new HashMap<QName, Service>();

    private final Map<Class, Service> intf2service = new HashMap<>();
    private final Map<QName, PortInfo> port2info = new HashMap<QName, PortInfo>();

    private static final Log log = LogFactory.getLog(ServiceDelegate.class);
    
    public ServiceDelegate()
    {
        handlerResolver = new SimpleHandlerResolver();
    }
    
    public ServiceDelegate(URL wsdlLocation, QName serviceName, Class clientClass)
    {
        this();
        
        this.wsdlLocation = wsdlLocation;
        this.serviceName = serviceName;
        
        try
        {
            Method method = clientClass.getMethod("getPortClassMap", new Class[0]);
            
            Map port2Class = (Map) method.invoke(null, new Object[0]);
            for (Object o : port2Class.entrySet()) {
                Map.Entry entry = (Map.Entry) o;

                QName port = (QName) entry.getKey();
                Class clazz = (Class) entry.getValue();

                Service service = getService(clazz);
                port2Service.put(port, service);
                intf2service.put(clazz, service);
            }
        }
        catch (Exception e)
        {
            throw new IllegalStateException("Could not initialize Service.", e);
        }
    }
    
    private Service getService(Class clazz)
    {
        Service service = intf2service.get(clazz);
        if (service == null)
        {
            Map<String,Object> properties = new HashMap<String,Object>();
            properties.put(AnnotationServiceFactory.ALLOW_INTERFACE, Boolean.TRUE);
            service = serviceFactory.create(clazz, serviceName, wsdlLocation, properties);
            
            intf2service.put(clazz, service);
        }
        
        return service;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getPort(QName portName, Class<T> clazz)
    {
        Endpoint endpoint = getService(clazz).getEndpoint(portName);
        
        if (endpoint == null) throw new WebServiceException("Invalid port name " + portName);
        
        return (T) createPort(endpoint);
    }

    @Override
    public <T> T getPort(QName portName, Class<T> serviceEndpointInterface, WebServiceFeature... features) {
        return getPort(portName, serviceEndpointInterface);
    }

    @Override
    public <T> T getPort(EndpointReference endpointReference, Class<T> serviceEndpointInterface, WebServiceFeature... features) {
        return getPort(serviceEndpointInterface);
    }

    private Object createPort(Endpoint endpoint)
    {
        try
        {
            return factory.create(endpoint);
        }
        catch (MalformedURLException e)
        {
            throw new WebServiceException("Invalid url: " + endpoint.getUrl(), e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getPort(Class<T> clazz)
    {
        if (getService(clazz).getEndpoints().isEmpty())
        {
            throw new WebServiceException("No available ports.");
        }
        
        return (T) createPort((Endpoint) getService(clazz).getEndpoints().iterator().next());
    }

    @Override
    public <T> T getPort(Class<T> serviceEndpointInterface, WebServiceFeature... features) {
        return getPort(serviceEndpointInterface);
    }

    @Override
    public void addPort(QName portName, String bindingUri, String address)
    {
        PortInfo portInfo = new PortInfo(bindingUri, address);
        
        port2info.put(portName, portInfo);
    }


    @Override
    public <T> Dispatch<T> createDispatch(QName port, Class<T> type, Mode serviceMode)
    {   
        Transport transport;
        String address;
        
        PortInfo portInfo = getPortInfo(port);
        if (portInfo != null)
        {
            String bindingUri = portInfo.getBindingUri();
            transport = jaxWsHelper.getBinding(bindingUri).getTransport();
            address = portInfo.getAddress();
        }
        else
        {
            Service service = port2Service.get(port);
            
            if (service == null)
            {
                throw new IllegalStateException("Could not find port " + port);
            }
            
            Endpoint ep = service.getEndpoint(port);
            transport = jaxWsHelper.getTransportManager().getTransport(ep.getBinding().getBindingId());
            address = ep.getUrl();
        }
        
        Client client = new Client(transport,
                                   jaxWsHelper.getSourceService(), 
                                   address);
        
        if (type == Source.class)
        {
            SourceDispatch dispatch = new SourceDispatch(client);
            dispatch.setMode(serviceMode);
            return (Dispatch<T>) dispatch;
        }
        else
        {
            throw new WebServiceException("Unknown dispatch type: " + type.getName());
        }
    }

    @Override
    public <T> Dispatch<T> createDispatch(QName portName, Class<T> type, Mode mode, WebServiceFeature... features) {
        return createDispatch(portName, type, mode);
    }

    @Override
    public <T> Dispatch<T> createDispatch(EndpointReference endpointReference, Class<T> type, Mode mode, WebServiceFeature... features) {
        log.error("I wasn't expecting you will call this method");
        return null;
    }

    private PortInfo getPortInfo(QName port)
    {
        return port2info.get(port);
    }

    @Override
    public Dispatch<Object> createDispatch(QName arg0, JAXBContext arg1, Mode arg2)
    {
        log.error("I wasn't expecting you will call this method");
        return null;
    }

    @Override
    public Dispatch<Object> createDispatch(QName portName, JAXBContext context, Mode mode, WebServiceFeature... features) {
        log.error("I wasn't expecting you will call this method");

        Class<Object> casting = (Class) Source.class;

        return createDispatch(portName, casting, mode);
    }

    @Override
    public Dispatch<Object> createDispatch(EndpointReference endpointReference, JAXBContext context, Mode mode, WebServiceFeature... features) {
        return null;
    }

    @Override
    public QName getServiceName()
    {
        return serviceName;
    }

    @Override
    public Iterator<QName> getPorts()
    {
        List<QName> ports = new ArrayList<QName>();
        
        ports.addAll(port2info.keySet());
        ports.addAll(port2Service.keySet());
        
        return ports.iterator();
    }

    @Override
    public URL getWSDLDocumentLocation()
    {
        return wsdlLocation;
    }

    @Override
    public HandlerResolver getHandlerResolver()
    {
        return handlerResolver;
    }

    @Override
    public void setHandlerResolver(HandlerResolver handlerResolver)
    {
        this.handlerResolver = handlerResolver;
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
    
    static class PortInfo 
    {
        private final String bindingUri;
        private final String address;
        
        public PortInfo(String bindingUri, String address2)
        {
            this.bindingUri = bindingUri;
            this.address = address2;
        }
        public String getAddress()
        {
            return address;
        }
        public String getBindingUri()
        {
            return bindingUri;
        }
    }
}
