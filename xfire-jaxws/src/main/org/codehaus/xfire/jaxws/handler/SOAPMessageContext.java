package org.codehaus.xfire.jaxws.handler;

import java.util.List;
import java.util.Set;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.ws.WebServiceException;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.util.stax.JDOMStreamReader;
import org.jdom.Element;
import org.jdom.Namespace;

public class SOAPMessageContext
    extends AbstractMessageContext
    implements jakarta.xml.ws.handler.soap.SOAPMessageContext
{
    private final MessageContext context;
    private SOAPMessage soapMessage;
    
    public SOAPMessageContext(MessageContext context)
    {
        this.context = context;
    }
    
    public Object[] getHeaders(QName name, JAXBContext jaxbContext, boolean allRoles)
    {
        if (context.getInMessage().getHeader() == null) return null;
        
        List children = context.getInMessage().getHeader().getChildren(name.getLocalPart(), 
                                                       Namespace.getNamespace(name.getNamespaceURI()));
        Object[] headers = new Object[children.size()];
        
        for (int i = 0; i < children.size(); i++)
        {
            Element header = (Element) children.get(i);
            try
            {
                Unmarshaller um = jaxbContext.createUnmarshaller();
                headers[i] = um.unmarshal(new JDOMStreamReader(header));
            }
            catch (JAXBException e)
            {
                throw new WebServiceException("Could not unmarshal header!", e);
            }
        }
        
        return headers;
    }

    public SOAPMessage getMessage()
    {
        return soapMessage;
    }

    public Set<String> getRoles()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void setMessage(SOAPMessage soapMessage)
    {
        this.soapMessage = soapMessage;
    }
}
