package org.codehaus.xfire.util.stax;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * An XMLStreamReader which keeps track of the depth where we are.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse </a>
 * @since Nov 4, 2004
 */
public class DepthXMLStreamReader 
    implements XMLStreamReader
{
    XMLStreamReader reader;

    private int depth = 0;
    
    public DepthXMLStreamReader(XMLStreamReader reader)
    {
        this.reader = reader;
    }

    public int getDepth()
    {
        return depth;
    }

    public void close() throws XMLStreamException
    {
        reader.close();
    }

    public int getAttributeCount()
    {
        return reader.getAttributeCount();
    }

    public String getAttributeLocalName(int arg0)
    {
        return reader.getAttributeLocalName(arg0);
    }

    public QName getAttributeName(int arg0)
    {
        return reader.getAttributeName(arg0);
    }

    public String getAttributeNamespace(int arg0)
    {
        return reader.getAttributeNamespace(arg0);
    }

    public String getAttributePrefix(int arg0)
    {
        return reader.getAttributePrefix(arg0);
    }

    public String getAttributeType(int arg0)
    {
        return reader.getAttributeType(arg0);
    }

    public String getAttributeValue(int arg0)
    {
        return reader.getAttributeValue(arg0);
    }

    public String getAttributeValue(String arg0, String arg1)
    {
        return reader.getAttributeValue(arg0, arg1);
    }

    public String getCharacterEncodingScheme()
    {
        return reader.getCharacterEncodingScheme();
    }

    public String getElementText() throws XMLStreamException
    {
        depth--;
        return reader.getElementText();
    }

    public String getEncoding()
    {
        return reader.getEncoding();
    }

    public int getEventType()
    {
        return reader.getEventType();
    }

    public String getLocalName()
    {
        return reader.getLocalName();
    }

    public Location getLocation()
    {
        return reader.getLocation();
    }

    public QName getName()
    {
        return reader.getName();
    }

    public NamespaceContext getNamespaceContext()
    {
        return reader.getNamespaceContext();
    }

    public int getNamespaceCount()
    {
        return reader.getNamespaceCount();
    }

    public String getNamespacePrefix(int arg0)
    {
        return reader.getNamespacePrefix(arg0);
    }

    public String getNamespaceURI()
    {
        return reader.getNamespaceURI();
    }

    public String getNamespaceURI(int arg0)
    {

        return reader.getNamespaceURI(arg0);
    }

    public String getNamespaceURI(String arg0)
    {
        return reader.getNamespaceURI(arg0);
    }

    public String getPIData()
    {
        return reader.getPIData();
    }

    public String getPITarget()
    {
        return reader.getPITarget();
    }

    public String getPrefix()
    {
        return reader.getPrefix();
    }

    public Object getProperty(String arg0) throws IllegalArgumentException
    {

        return reader.getProperty(arg0);
    }

    public String getText()
    {

        return reader.getText();
    }

    public char[] getTextCharacters()
    {
        return reader.getTextCharacters();
    }

    public int getTextCharacters(int arg0, char[] arg1, int arg2, int arg3)
            throws XMLStreamException
    {
        return reader.getTextCharacters(arg0, arg1, arg2, arg3);
    }

    public int getTextLength()
    {
        return reader.getTextLength();
    }

    public int getTextStart()
    {

        return reader.getTextStart();
    }

    public String getVersion()
    {
        return reader.getVersion();
    }

    public boolean hasName()
    {

        return reader.hasName();
    }

    public boolean hasNext() throws XMLStreamException
    {
        return reader.hasNext();
    }

    public boolean hasText()
    {
        return reader.hasText();
    }

    public boolean isAttributeSpecified(int arg0)
    {
        return reader.isAttributeSpecified(arg0);
    }

    public boolean isCharacters()
    {

        return reader.isCharacters();
    }

    public boolean isEndElement()
    {

        return reader.isEndElement();
    }

    public boolean isStandalone()
    {

        return reader.isStandalone();
    }

    public boolean isStartElement()
    {

        return reader.isStartElement();
    }

    public boolean isWhiteSpace()
    {

        return reader.isWhiteSpace();
    }

    public int next() throws XMLStreamException
    {
        int next = reader.next();
        
        if ( next == START_ELEMENT )
        {
            depth++;
        }
        else if ( next == END_ELEMENT )
        {
            depth--;
        }
        
        return next;
    }

    public int nextTag()
        throws XMLStreamException
    {
        int eventType = next();
        while ((eventType == XMLStreamConstants.CHARACTERS && isWhiteSpace())
                || (eventType == XMLStreamConstants.CDATA && isWhiteSpace())
                // skip whitespace
                || eventType == XMLStreamConstants.SPACE
                || eventType == XMLStreamConstants.PROCESSING_INSTRUCTION
                || eventType == XMLStreamConstants.COMMENT)
        {
            eventType = next();
        }
        if (eventType != XMLStreamConstants.START_ELEMENT
                && eventType != XMLStreamConstants.END_ELEMENT)
        {
            throw new XMLStreamException("expected start or end tag", getLocation());
        }
        return eventType;
    }

    public void require(int arg0, String arg1, String arg2)
            throws XMLStreamException
    {
        reader.require(arg0, arg1, arg2);
    }

    public boolean standaloneSet()
    {
        return reader.standaloneSet();
    }

    public int hashCode()
    {
        return reader.hashCode();
    }

    public boolean equals(Object arg0)
    {
        return reader.equals(arg0);
    }

    public String toString()
    {
        return reader.toString();
    }

}
