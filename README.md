# QpidJMS with AMQP-style acknowledgements

This is a fork of the QpidJMS project that adds the ability to acknowledge
messages in three ways: CONSUMED, POISONED, and RELEASED.
The fork is based on QpidJMS version 0.4.0-SNAPSHOT. The fork is
fully compatible with JMS 1.1 but provides an additional interface, JmsAmqpsMessage
(found in org.apache.qpid.jms), that features this new functionality.
Any messages consumed using this provider can be safely cast to type JmsAmpqsMessage.

## Additions
* org.apache.qpid.jms.message.JmsAmqpsMessage
* org.apache.qpid.jms.message.JmsAcknowledgeCallback

## Changes
* Added method acknowledge(ProviderConstants.ACK_TYPE ackType) to org.apache.qpid.jms.message.JmsMessage.
* Changed acknowledgeCallback type in org.apache.qpid.jms.message.JmsMessage to type JmsAcknowledgeCallback.
* Changed method onInboundMessage in org.apache.qpid.jms.JmsMessageConsumer to set a message callback
that takes an ACK_TYPE as an argument.
* Changed method acknowledge() in org.apache.qpid.jms.JmsSession to take an ACK_TYPE as an argument.
* Changed method acknowledge(JmsSessionId sessionId) in org.apache.qpid.jms.JmsConnection to take an ACK_TYPE
as an argument.
* Changed method acknowledge(final JmsSessionId sessionId, final AsyncResult request) in
org.apache.qpid.jms.provider.amqp.AmqpProvider to take an ACK_TYPE as an argument.
* Changed method acknowledge() in org.apache.qpid.jms.provider.amqp.AmqpSession to take an ACK_TYPE
as an argument.
* Changed method acknowledge() in org.apache.qpid.jms.provider.amqp.AmqpConsumer to take an ACK_TYPE
as an argument. The method maps acknowledgement types to AMQP dispositions as follows:
CONSUMED to ACCEPTED, POISONED to REJECTED, RELEASED to RELEASED.

## Intro

The QpidJMS project provides a JMS based client that uses the AMQP v1.0 protocol.

Below are some quick pointers you might find useful.

## Building the code

The project requires Maven 3. Some example commands follow.

Clean previous builds output and install all modules to local repository without
running the tests:

    mvn clean install -DskipTests

Install all modules to the local repository after running all the tests:

    mvn clean install

Perform a subset tests on the packaged release artifacts without
installing:

    mvn clean verify -Dtest=TestNamePattern*

Execute the tests and produce code coverage report:

    mvn clean test jacoco:report

## Examples

First build and install all the modules as detailed above (if running against
a source checkout/release, rather than against released binaries) and then
consult the README in the qpid-jms-examples module itself.

## Documentation

There is some basic documentation in the qpid-jms-docs module.

## Distribution assemblies

After building the modules, src and binary distribution assemblies can be found at:

    apache-qpid-jms/target
