package org.codehaus.xfire.plexus;

import jakarta.servlet.ServletException;

import org.codehaus.plexus.servlet.PlexusServletUtils;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.transport.http.XFireServlet;

/**
 * An XFire servlet which obtains its XFire instance from Plexus. This
 * is only if you already using Plexus in your application. For a message
 * Plexus managed version of XFire see StandaloneXFireServlet.
 * 
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @since Feb 13, 2004
 */
public class PlexusXFireServlet
    extends XFireServlet
{
	public XFire createXFire()
    	throws ServletException
    {
        return (XFire) PlexusServletUtils.lookup(getServletContext(), XFire.ROLE);
    }
}
