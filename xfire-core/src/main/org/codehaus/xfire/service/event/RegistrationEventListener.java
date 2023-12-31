package org.codehaus.xfire.service.event;


/**
 * An object that registers to be notified of events generated by a <code>ServiceEndpointRegistry</code> object.
 *
 * @author <a href="mailto:dan@envoisolutions.com">Dan Diephouse</a>
 * @author <a href="mailto:poutsma@mac.com">Arjen Poutsma</a>
 * @see RegistrationEvent
 */
public interface RegistrationEventListener
{
    /**
     * Notifies this <code>RegistrationEventListener</code> that the <code>ServiceEndpointRegistry</code> has registered
     * an endpoint.
     *
     * @param event an event object describing the source of the event
     */
    void endpointRegistered(RegistrationEvent event);

    /**
     * Notifies this <code>RegistrationEventListener</code> that the <code>ServiceEndpointRegistry</code> has
     * deregistered an endpoint.
     *
     * @param event an event object describing the source of the event
     */
    void endpointUnregistered(RegistrationEvent event);
}
