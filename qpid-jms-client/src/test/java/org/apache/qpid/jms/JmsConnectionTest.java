/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.qpid.jms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;

import javax.jms.ExceptionListener;
import javax.jms.IllegalStateException;
import javax.jms.InvalidClientIDException;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;

import org.apache.qpid.jms.message.JmsInboundMessageDispatch;
import org.apache.qpid.jms.provider.mock.MockProvider;
import org.apache.qpid.jms.provider.mock.MockProviderFactory;
import org.apache.qpid.jms.util.IdGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test basic functionality around JmsConnection
 */
public class JmsConnectionTest {

    private final IdGenerator clientIdGenerator = new IdGenerator();

    private MockProvider provider;
    private JmsConnection connection;

    @Before
    public void setUp() throws Exception {
        provider = (MockProvider) MockProviderFactory.create(new URI("mock://localhost"));
    }

    @After
    public void tearDown() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    @Test(timeout=30000, expected=JMSException.class)
    public void testJmsConnectionThrowsJMSExceptionProviderStartFails() throws JMSException, IllegalStateException, IOException {
        provider.getConfiguration().setFailOnStart(true);
        new JmsConnection("ID:TEST:1", provider, clientIdGenerator);
    }

    @Test(timeout=30000)
    public void testStateAfterCreate() throws JMSException {
        connection = new JmsConnection("ID:TEST:1", provider, clientIdGenerator);

        assertFalse(connection.isStarted());
        assertFalse(connection.isClosed());
        assertFalse(connection.isConnected());
    }

    @Test(timeout=30000)
    public void testGetExceptionListener() throws JMSException {
        connection = new JmsConnection("ID:TEST:1", provider, clientIdGenerator);

        assertNull(connection.getExceptionListener());
        connection.setExceptionListener(new ExceptionListener() {

            @Override
            public void onException(JMSException exception) {
            }
        });

        assertNotNull(connection.getExceptionListener());
    }

    @Test(timeout=30000)
    public void testReplacePrefetchPolicy() throws JMSException {
        connection = new JmsConnection("ID:TEST:1", provider, clientIdGenerator);

        JmsPrefetchPolicy newPolicy = new JmsPrefetchPolicy();
        newPolicy.setAll(1);

        assertNotSame(newPolicy, connection.getPrefetchPolicy());
        connection.setPrefetchPolicy(newPolicy);
        assertEquals(newPolicy, connection.getPrefetchPolicy());
    }

    @Test(timeout=30000)
    public void testGetConnectionId() throws JMSException {
        connection = new JmsConnection("ID:TEST:1", provider, clientIdGenerator);
        assertEquals("ID:TEST:1", connection.getId().toString());
    }

    @Test(timeout=30000)
    public void testAddConnectionListener() throws JMSException {
        connection = new JmsConnection("ID:TEST:1", provider, clientIdGenerator);
        JmsConnectionListener listener = new JmsConnectionListener() {

            @Override
            public void onInboundMessage(JmsInboundMessageDispatch envelope) {
            }

            @Override
            public void onConnectionRestored(URI remoteURI) {
            }

            @Override
            public void onConnectionInterrupted(URI remoteURI) {
            }

            @Override
            public void onConnectionFailure(Throwable error) {
            }

            @Override
            public void onConnectionEstablished(URI remoteURI) {
            }
        };

        assertFalse(connection.removeConnectionListener(listener));
        connection.addConnectionListener(listener);
        assertTrue(connection.removeConnectionListener(listener));
    }

    @Test(timeout=30000)
    public void testConnectionStart() throws JMSException, IOException {
        connection = new JmsConnection("ID:TEST:1", provider, clientIdGenerator);

        assertFalse(connection.isConnected());
        connection.start();
        assertTrue(connection.isConnected());
    }

    @Test(timeout=30000)
    public void testConnectionMulitpleStartCalls() throws JMSException, IOException {
        connection = new JmsConnection("ID:TEST:1", provider, clientIdGenerator);

        assertFalse(connection.isConnected());
        connection.start();
        assertTrue(connection.isConnected());
        connection.start();
        assertTrue(connection.isConnected());
    }

    @Test(timeout=30000)
    public void testConnectionStartAndStop() throws JMSException, IOException {
        connection = new JmsConnection("ID:TEST:1", provider, clientIdGenerator);

        assertFalse(connection.isConnected());
        connection.start();
        assertTrue(connection.isConnected());
        connection.stop();
        assertTrue(connection.isConnected());
    }

    @Test(timeout=30000, expected=InvalidClientIDException.class)
    public void testSetClientIDFromNull() throws JMSException, IOException {
        connection = new JmsConnection("ID:TEST:1", provider, clientIdGenerator);
        assertFalse(connection.isConnected());
        connection.setClientID("");
    }

    @Test(timeout=30000)
    public void testCreateNonTXSessionWithTXAckMode() throws JMSException, IOException {
        connection = new JmsConnection("ID:TEST:1", provider, clientIdGenerator);
        connection.start();

        try {
            connection.createSession(false, Session.SESSION_TRANSACTED);
            fail("Should not allow non-TX session with mode SESSION_TRANSACTED");
        } catch (JMSException ex) {
        }
    }

    @Test(timeout=30000)
    public void testCreateNonTXSessionWithUnknownAckMode() throws JMSException, IOException {
        connection = new JmsConnection("ID:TEST:1", provider, clientIdGenerator);
        connection.start();

        try {
            connection.createSession(false, 99);
            fail("Should not allow unkown Ack modes.");
        } catch (JMSException ex) {
        }
    }

    @Test(timeout=30000, expected=InvalidClientIDException.class)
    public void testSetClientIDFromEmptyString() throws JMSException, IOException {
        connection = new JmsConnection("ID:TEST:1", provider, clientIdGenerator);
        assertFalse(connection.isConnected());
        connection.setClientID(null);
    }

    @Test(timeout=30000, expected=IllegalStateException.class)
    public void testSetClientIDFailsOnSecondCall() throws JMSException, IOException {
        connection = new JmsConnection("ID:TEST:1", provider, clientIdGenerator);

        assertFalse(connection.isConnected());
        connection.setClientID("TEST-ID");
        assertTrue(connection.isConnected());
        connection.setClientID("TEST-ID");
    }

    @Test(timeout=30000, expected=IllegalStateException.class)
    public void testSetClientIDFailsAfterStart() throws JMSException, IOException {
        connection = new JmsConnection("ID:TEST:1", provider, clientIdGenerator);

        assertFalse(connection.isConnected());
        connection.start();
        assertTrue(connection.isConnected());
        connection.setClientID("TEST-ID");
    }

    @Test(timeout=30000)
    public void testDeleteOfTempQueueOnClosedConnection() throws JMSException, IOException {
        connection = new JmsConnection("ID:TEST:1", provider, clientIdGenerator);
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        TemporaryQueue tempQueue = session.createTemporaryQueue();
        assertNotNull(tempQueue);

        connection.close();
        try {
            tempQueue.delete();
            fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException ex) {
        }
    }

    @Test(timeout=30000)
    public void testDeleteOfTempTopicOnClosedConnection() throws JMSException, IOException {
        connection = new JmsConnection("ID:TEST:1", provider, clientIdGenerator);
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        TemporaryTopic tempTopic = session.createTemporaryTopic();
        assertNotNull(tempTopic);

        connection.close();
        try {
            tempTopic.delete();
            fail("Should have thrown an IllegalStateException");
        } catch (IllegalStateException ex) {
        }
    }

    //----- Currently these are unimplemented, these will fail after that ----//

    @Test(timeout=30000, expected=JMSException.class)
    public void testCreateConnectionConsumer() throws Exception {
        connection = new JmsConnection("ID:TEST:1", provider, clientIdGenerator);
        connection.createConnectionConsumer((JmsDestination) new JmsTopic(), "", null, 1);
    }

    @Test(timeout=30000, expected=JMSException.class)
    public void testCreateConnectionTopicConsumer() throws Exception {
        connection = new JmsConnection("ID:TEST:1", provider, clientIdGenerator);
        connection.createConnectionConsumer(new JmsTopic(), "", null, 1);
    }

    @Test(timeout=30000, expected=JMSException.class)
    public void testCreateConnectionQueueConsumer() throws Exception {
        connection = new JmsConnection("ID:TEST:1", provider, clientIdGenerator);
        connection.createConnectionConsumer(new JmsQueue(), "", null, 1);
    }

    @Test(timeout=30000, expected=JMSException.class)
    public void testCreateDurableConnectionConsumer() throws Exception {
        connection = new JmsConnection("ID:TEST:1", provider, clientIdGenerator);
        connection.createDurableConnectionConsumer(new JmsTopic(), "id", "", null, 1);
    }
}
