package org.apache.qpid.jms.message;

import org.apache.qpid.jms.provider.ProviderConstants;

import javax.jms.JMSException;

/** Handles message acknowledgement based on the acknowledgement type given. */
public interface JmsAcknowledgeCallback
{
    /**
     * Acknowledges the message with the given acknowledgement type.
     *
     * @param ackType the acknowledgement type.
     *
     * @throws JMSException if the acknowledgement fails.
     */
    void call(ProviderConstants.ACK_TYPE ackType) throws JMSException;
}
