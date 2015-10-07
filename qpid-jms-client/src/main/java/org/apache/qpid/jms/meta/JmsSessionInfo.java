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
package org.apache.qpid.jms.meta;

import javax.jms.Session;

import org.apache.qpid.jms.util.ToStringSupport;

public final class JmsSessionInfo implements JmsResource, Comparable<JmsSessionInfo> {

    private final JmsSessionId sessionId;
    private int acknowledgementMode;
    private boolean sendAcksAsync;

    public JmsSessionInfo(JmsConnectionInfo connectionInfo, long sessionId) {
        if (connectionInfo == null) {
            throw new IllegalArgumentException("Connection info object cannot be null");
        }
        this.sessionId = new JmsSessionId(connectionInfo.getId(), sessionId);
    }

    public JmsSessionInfo(JmsSessionId sessionId) {
        if (sessionId == null) {
            throw new IllegalArgumentException("session Id object cannot be null");
        }

        this.sessionId = sessionId;
    }

    public JmsSessionInfo copy() {
        JmsSessionInfo copy = new JmsSessionInfo(sessionId);
        copy(copy);
        return copy;
    }

    private void copy(JmsSessionInfo copy) {
        copy.acknowledgementMode = acknowledgementMode;
        copy.sendAcksAsync = sendAcksAsync;
    }

    @Override
    public JmsSessionId getId() {
        return sessionId;
    }

    @Override
    public void visit(JmsResourceVistor vistor) throws Exception {
        vistor.processSessionInfo(this);
    }

    public int getAcknowledgementMode() {
        return acknowledgementMode;
    }

    public void setAcknowledgementMode(int acknowledgementMode) {
        this.acknowledgementMode = acknowledgementMode;
    }

    public boolean isTransacted() {
        return acknowledgementMode == Session.SESSION_TRANSACTED;
    }

    public boolean isSendAcksAsync() {
        return sendAcksAsync;
    }

    public void setSendAcksAsync(boolean sendAcksAsync) {
        this.sendAcksAsync = sendAcksAsync;
    }

    @Override
    public String toString() {
        return ToStringSupport.toString(this);
    }

    @Override
    public int hashCode() {
        return sessionId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        JmsSessionInfo other = (JmsSessionInfo) obj;
        return sessionId.equals(other.sessionId);
    }

    @Override
    public int compareTo(JmsSessionInfo other) {
        return sessionId.compareTo(other.sessionId);
    }
}
