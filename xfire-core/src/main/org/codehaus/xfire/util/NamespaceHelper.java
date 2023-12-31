/*
 * This file contains software licensed under the Aapache License:
 * 
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.xfire.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.xfire.XFireRuntimeException;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * Namespace utilities.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 */
public class NamespaceHelper
{
    /**
     * Create a unique namespace uri/prefix combination.
     * 
     * @param nsUri
     * @return The namespace with the specified URI. If one doesn't exist, one
     *         is created.
     */
    public static String getUniquePrefix(Element element, String namespaceURI)
    {
        String prefix = getPrefix(element, namespaceURI);

        if (prefix == null)
        {
            prefix = getUniquePrefix(element);
            element.addNamespaceDeclaration(Namespace.getNamespace(prefix, namespaceURI));
        }
        return prefix;
    }

    public static String getPrefix(Element element, String namespaceURI)
    {
        if (element.getNamespaceURI().equals(namespaceURI))
            return element.getNamespacePrefix();

        List namespaces = element.getAdditionalNamespaces();

        for (Iterator itr = namespaces.iterator(); itr.hasNext();)
        {
            Namespace ns = (Namespace) itr.next();

            if (ns.getURI().equals(namespaceURI))
                return ns.getPrefix();
        }

        if (element.getParentElement() != null)
            return getPrefix(element.getParentElement(), namespaceURI);
        else
            return null;
    }

    public static void getPrefixes(Element element, String namespaceURI, List prefixes)
    {
        if (element.getNamespaceURI().equals(namespaceURI))
            prefixes.add(element.getNamespacePrefix());

        List namespaces = element.getAdditionalNamespaces();

        for (Iterator itr = namespaces.iterator(); itr.hasNext();)
        {
            Namespace ns = (Namespace) itr.next();

            if (ns.getURI().equals(namespaceURI))
                prefixes.add(ns.getPrefix());
        }

        if (element.getParentElement() != null)
            getPrefixes(element.getParentElement(), namespaceURI, prefixes);
    }

    private static String getUniquePrefix(Element el)
    {
        int n = 1;

        while (true)
        {
            String nsPrefix = "ns" + n;

            if (el.getNamespace(nsPrefix) == null)
                return nsPrefix;

            n++;
        }
    }

    /**
     * Create a unique namespace uri/prefix combination.
     * 
     * @param nsUri
     * @return The namespace with the specified URI. If one doesn't exist, one
     *         is created.
     * @throws XMLStreamException
     */
    public static String getUniquePrefix(XMLStreamWriter writer,
                                         String namespaceURI,
                                         boolean declare)
        throws XMLStreamException
    {
        String prefix = writer.getNamespaceContext().getPrefix(namespaceURI);
        if (prefix == null)
        {
            prefix = getUniquePrefix(writer);

            if (declare) 
            {
                writer.setPrefix(prefix, namespaceURI);
                writer.writeNamespace(prefix, namespaceURI);
            }
        }
        
        return prefix;
    }

    public static String getUniquePrefix(XMLStreamWriter writer)
    {
        int n = 1;

        while (true)
        {
            String nsPrefix = "ns" + n;

            if (writer.getNamespaceContext().getNamespaceURI(nsPrefix) == null)
            {
                return nsPrefix;
            }

            n++;
        }
    }

    /**
     * Generates the name of a XML namespace from a given class name and
     * protocol. The returned namespace will take the form
     * <code>protocol://domain</code>, where <code>protocol</code> is the
     * given protocol, and <code>domain</code> the inversed package name of
     * the given class name. <p/> For instance, if the given class name is
     * <code>org.codehaus.xfire.services.Echo</code>, and the protocol is
     * <code>http</code>, the resulting namespace would be
     * <code>http://services.xfire.codehaus.org</code>.
     * 
     * @param className
     *            the class name
     * @param protocol
     *            the protocol (eg. <code>http</code>)
     * @return the namespace
     */
    public static String makeNamespaceFromClassName(String className, String protocol)
    {
        int index = className.lastIndexOf(".");

        if (index == -1)
        {
            return protocol + "://" + "DefaultNamespace";
        }

        String packageName = className.substring(0, index);

        StringTokenizer st = new StringTokenizer(packageName, ".");
        String[] words = new String[st.countTokens()];

        for (int i = 0; i < words.length; ++i)
        {
            words[i] = st.nextToken();
        }

        StringBuffer sb = new StringBuffer(80);

        for (int i = words.length - 1; i >= 0; --i)
        {
            String word = words[i];

            // seperate with dot
            if (i != words.length - 1)
            {
                sb.append('.');
            }

            sb.append(word);
        }

        return protocol + "://" + sb.toString();
    }

    /**
     * Method makePackageName
     * 
     * @param namespace
     * @return
     */
    public static String makePackageName(String namespace)
    {

        String hostname = null;
        String path = "";

        // get the target namespace of the document
        try
        {
            URL u = new URL(namespace);

            hostname = u.getHost();
            path = u.getPath();
        }
        catch (MalformedURLException e)
        {
            if (namespace.indexOf(":") > -1)
            {
                hostname = namespace.substring(namespace.indexOf(":") + 1);

                if (hostname.indexOf("/") > -1)
                {
                    hostname = hostname.substring(0, hostname.indexOf("/"));
                }
            }
            else
            {
                hostname = namespace;
            }
        }

        // if we didn't file a hostname, bail
        if (hostname == null)
        {
            return null;
        }

        // convert illegal java identifier
        hostname = hostname.replace('-', '_');
        path = path.replace('-', '_');

        // chomp off last forward slash in path, if necessary
        if ((path.length() > 0) && (path.charAt(path.length() - 1) == '/'))
        {
            path = path.substring(0, path.length() - 1);
        }

        // tokenize the hostname and reverse it
        StringTokenizer st = new StringTokenizer(hostname, ".:");
        String[] words = new String[st.countTokens()];

        for (int i = 0; i < words.length; ++i)
        {
            words[i] = st.nextToken();
        }

        StringBuffer sb = new StringBuffer(namespace.length());

        for (int i = words.length - 1; i >= 0; --i)
        {
            addWordToPackageBuffer(sb, words[i], (i == words.length - 1));
        }

        // tokenize the path
        StringTokenizer st2 = new StringTokenizer(path, "/");

        while (st2.hasMoreTokens())
        {
            addWordToPackageBuffer(sb, st2.nextToken(), false);
        }

        return sb.toString();
    }

    /**
     * Massage <tt>word</tt> into a form suitable for use in a Java package
     * name. Append it to the target string buffer with a <tt>.</tt> delimiter
     * iff <tt>word</tt> is not the first word in the package name.
     * 
     * @param sb
     *            the buffer to append to
     * @param word
     *            the word to append
     * @param firstWord
     *            a flag indicating whether this is the first word
     */
    private static void addWordToPackageBuffer(StringBuffer sb, String word, boolean firstWord)
    {

        if (JavaUtils.isJavaKeyword(word))
        {
            word = JavaUtils.makeNonJavaKeyword(word);
        }

        // separate with dot after the first word
        if (!firstWord)
        {
            sb.append('.');
        }

        // prefix digits with underscores
        if (Character.isDigit(word.charAt(0)))
        {
            sb.append('_');
        }

        // replace periods with underscores
        if (word.indexOf('.') != -1)
        {
            char[] buf = word.toCharArray();

            for (int i = 0; i < word.length(); i++)
            {
                if (buf[i] == '.')
                {
                    buf[i] = '_';
                }
            }

            word = new String(buf);
        }

        sb.append(word);
    }

    /**
     * Reads a QName from the element text. Reader must be positioned at the
     * start tag.
     * 
     * @param reader
     * @return
     * @throws XMLStreamException
     */
    public static QName readQName(XMLStreamReader reader)
        throws XMLStreamException
    {
        String value = reader.getElementText();
        if (value == null)
            return null;

        int index = value.indexOf(":");

        if (index == -1)
        {
            return new QName(value);
        }

        String prefix = value.substring(0, index);
        String localName = value.substring(index + 1);
        String ns = reader.getNamespaceURI(prefix);

        if (ns == null || localName == null)
        {
            throw new XFireRuntimeException("Invalid QName in mapping: " + value);
        }

        return new QName(ns, localName, prefix);
    }

    public static QName createQName(NamespaceContext nc, String value)
    {
        if (value == null)
        {
            return null;
        }
        int index = value.indexOf(':');
        if (index == -1)
        {
            return new QName(nc.getNamespaceURI(""), value, "");
        }
        else
        {
            String prefix = value.substring(0, index);
            String localName = value.substring(index + 1);
            String ns = nc.getNamespaceURI(prefix);
            return new QName(ns, localName, prefix);
        }
    }

    public static QName createQName(Element e, String value, String defaultNamespace)
    {
        if (value == null)
            return null;

        int index = value.indexOf(":");

        if (index == -1)
        {
            return new QName(defaultNamespace, value);
        }

        String prefix = value.substring(0, index);
        String localName = value.substring(index + 1);
        String ns = e.getNamespace(prefix).getURI();

        if (ns == null || localName == null)
            throw new XFireRuntimeException("Invalid QName in mapping: " + value);

        return new QName(ns, localName, prefix);
    }
}
