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
package org.apache.qpid.jms.provider.amqp.builders;

import org.apache.qpid.jms.meta.JmsSessionInfo;
import org.apache.qpid.jms.provider.amqp.AmqpSession;
import org.apache.qpid.jms.provider.amqp.AmqpTransactionContext;
import org.apache.qpid.proton.amqp.messaging.Source;
import org.apache.qpid.proton.amqp.transaction.Coordinator;
import org.apache.qpid.proton.amqp.transaction.TxnCapability;
import org.apache.qpid.proton.amqp.transport.ReceiverSettleMode;
import org.apache.qpid.proton.amqp.transport.SenderSettleMode;
import org.apache.qpid.proton.engine.Sender;

/**
 * Resource builder responsible for creating and opening an AmqpTemporaryDestination instance.
 */
public class AmqpTransactionContextBuilder extends AmqpResourceBuilder<AmqpTransactionContext, AmqpSession, JmsSessionInfo, Sender> {

    public AmqpTransactionContextBuilder(AmqpSession parent, JmsSessionInfo resourceInfo) {
        super(parent, resourceInfo);
    }

    @Override
    protected Sender createEndpoint(JmsSessionInfo resourceInfo) {
        Coordinator coordinator = new Coordinator();
        coordinator.setCapabilities(TxnCapability.LOCAL_TXN);
        Source source = new Source();

        String coordinatorName = "qpid-jms:coordinator:" + resourceInfo.getId().toString();

        Sender sender = getParent().getEndpoint().sender(coordinatorName);
        sender.setSource(source);
        sender.setTarget(coordinator);
        sender.setSenderSettleMode(SenderSettleMode.UNSETTLED);
        sender.setReceiverSettleMode(ReceiverSettleMode.FIRST);

        return sender;
    }

    @Override
    protected AmqpTransactionContext createResource(AmqpSession parent, JmsSessionInfo resourceInfo, Sender endpoint) {
        return new AmqpTransactionContext(parent, resourceInfo, endpoint);
    }

    @Override
    protected boolean isClosePending() {
        // When no link terminus was created, the peer will now detach/close us otherwise
        // we need to validate the returned remote source prior to open completion.
        return getEndpoint().getRemoteTarget() == null;
    }
}
