package org.apache.qpid.jms.message;

import org.apache.qpid.jms.provider.ProviderConstants;

import javax.jms.JMSException;

/** A JMS message that can be acknowledged according to the AMQP 1.0 standard. */
public interface JmsAmqpMessage extends javax.jms.Message
{
    /**
     * Acknowledges the message using the given acknowledgement type.
     *
     * @param ackType the acknowledgement type.
     *
     * @throws JMSException if the acknowledgement fails.
     */
    void acknowledge(ProviderConstants.ACK_TYPE ackType) throws JMSException;
}
