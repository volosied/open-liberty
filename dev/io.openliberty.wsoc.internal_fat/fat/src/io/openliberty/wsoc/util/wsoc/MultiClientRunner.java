/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package io.openliberty.wsoc.util.wsoc;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.ClientEndpointConfig.Builder;
import javax.websocket.Endpoint;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import io.openliberty.wsoc.common.Constants;

/**
 * Class to run multiple websocket client tests.
 */
public class MultiClientRunner {

    private static final Logger LOG = Logger.getLogger(MultiClientRunner.class.getName());

    private Object[] _receiveEndpoints = null;

    private Object _publishEndpoint = null;

    private WsocTestContext[] _receiveClients = null;

    private WsocTestContext _publishClient = null;

    private PublishTask _publishTask = null;

    private ClientEndpointConfig _cfg = null;

    private URI _uri = null;

    public static int DEFAULT_MAX_MESSAGES = 1;

    public static int DEFAULT_TIMEOUT = Constants.getDefaultTimeout();

    public static ClientEndpointConfig getDefaultConfig() {
        Builder b = ClientEndpointConfig.Builder.create();
        return b.build();
    }

    /**
     * Basic constructor. Assumes default endpoint config and designated publisher client.
     * 
     * @param receiveEndpoints - Array of annotated or programmatic endpoints.
     * @param uri - URI to connect to - in the form of ws:// or ws:///
     * @param cfg - endpoint config
     */
    public MultiClientRunner(Object[] receiveEndpoints, URI uri, ClientEndpointConfig cfg) {

        this(receiveEndpoints, null, null, uri, cfg);
    }

    /**
     * Constructor that allows a publisher client and publisher task.
     * 
     * @param receiveEndpoints - array of annotated or programmatic endpoint
     * @param publishEndpoint - single endpoint for publisher task that runs once all receiverEndpoints are connected.
     * @param ptask - task the publisher client will run.
     * @param uri - URI to connect to - in the form of ws:// or ws:///
     * @param cfg - endpoint config
     */
    public MultiClientRunner(Object[] receiveEndpoints, Object publishEndpoint, PublishTask ptask, URI uri, ClientEndpointConfig cfg) {
        _receiveEndpoints = receiveEndpoints;

        _uri = uri;
        _cfg = cfg;
        _receiveEndpoints = receiveEndpoints;
        _publishEndpoint = publishEndpoint;
        _publishTask = ptask;

    }

    /**
     * Run a multiple client test.
     * 
     * @param numMsgsExpected - Each client is expected to receive this many msgs before shutting down.. Endpoints should check the text context limit reached to determine when to
     *            shut down endpoint
     * @param runTime - How long the test will run, can do longer runs with this and setting messageCountOnly to true.
     * @param connectTimeout - How long to wait before receivers ( and publisher) connects before aborting test.
     * @param messageCountOnly- WsocTestContext will just count the number of messages received and not store them.. for longer runs.
     * @return
     * @throws Exception
     */
    public MultiClientTestContext runTest(int numMsgsExpected, int runTime, int connectTimeout, boolean messageCountOnly) throws Exception {

        LOG.info("=== MultiClientRunner.runTest START ===");
        LOG.info("Parameters: numMsgsExpected=" + numMsgsExpected + ", runTime=" + runTime +
                 ", connectTimeout=" + connectTimeout + ", messageCountOnly=" + messageCountOnly);
        LOG.info("URI: " + _uri);

        WebSocketContainer c = TestWsocContainer.getRef();

        int numClients = _receiveEndpoints.length;
        LOG.info("Number of receiver clients: " + numClients);

        WsocTestContext.connectLatch = new CountDownLatch(numClients);

        int total = numClients;
        if (_publishEndpoint != null) {
            total++;
            LOG.info("Publisher endpoint present, total clients: " + total);
        }
        WsocTestContext.completeLatch = new CountDownLatch(total);

        MultiClientTestContext mctr = new MultiClientTestContext();

        _receiveClients = new WsocTestContext[numClients];
        mctr.setReceiverContexts(_receiveClients);

        LOG.info("Connecting " + numClients + " receiver clients...");
        for (int x = 0; x < _receiveEndpoints.length; x++) {
            LOG.info("Connecting receiver client #" + x + " of type: " + _receiveEndpoints[x].getClass().getName());
            _receiveClients[x] = connectClient(_receiveEndpoints[x], c, numMsgsExpected, messageCountOnly);
            LOG.info("Receiver client #" + x + " connected with context: " + _receiveClients[x]);
        }

        if (connectTimeout > 0) {
            LOG.info("Waiting for receiver clients to connect (timeout: " + connectTimeout + "ms)...");
            if (!WsocTestContext.connectLatch.await(connectTimeout, TimeUnit.MILLISECONDS)) {
                LOG.severe("TIMEOUT: Not all receiver clients connected within " + connectTimeout + " milliseconds!");
                throw new IOException("Websocket Exception, all receiver clients did not connect within " + connectTimeout + " milliseconds.");
            }
            LOG.info("All receiver clients connected successfully");
        }

        WsocTestContext.connectLatch = new CountDownLatch(1);

        ExecutorService publishExecutor = null;

        if (_publishEndpoint != null) {
            LOG.info("Connecting publisher client of type: " + _publishEndpoint.getClass().getName());
            _publishClient = connectClient(_publishEndpoint, c, numMsgsExpected, messageCountOnly);
            mctr.setPublisherContext(_publishClient);
            LOG.info("Publisher client connected with context: " + _publishClient);

            if (connectTimeout > 0) {
                LOG.info("Waiting for publisher client to connect (timeout: " + connectTimeout + "ms)...");
                if (!WsocTestContext.connectLatch.await(connectTimeout, TimeUnit.MILLISECONDS)) {
                    LOG.severe("TIMEOUT: Publisher client did not connect within " + connectTimeout + " milliseconds!");
                    throw new IOException("Websocket Exception, publisher client did not connect within " + connectTimeout + " milliseconds.");
                }
                LOG.info("Publisher client connected successfully");
            }

            // Wait for onOpen call to complete on the other endpoint (_uri side)
            LOG.info("Waiting 50ms for onOpen to complete on server side...");
            java.lang.Thread.sleep(50);

            if (_publishTask != null) {
                LOG.info("Starting publisher task: " + _publishTask.getClass().getName());
                _publishTask.setMultiTestContext(mctr);
                publishExecutor = Executors.newSingleThreadExecutor();
                publishExecutor.execute(_publishTask);
            }
        }

        LOG.info("Waiting for wsoc test to finish (timeout: " + runTime + "ms, completeLatch count: " +
                 WsocTestContext.completeLatch.getCount() + ")");

        if (!WsocTestContext.completeLatch.await(runTime, TimeUnit.MILLISECONDS)) {
            LOG.severe("TEST TIMEOUT: Test did not complete within " + runTime + " milliseconds!");
            LOG.severe("Remaining completeLatch count: " + WsocTestContext.completeLatch.getCount());
            
            // Count and log clients by message count
            int[] messageCounts = new int[numMsgsExpected + 1];
            int clientsWithZeroMessages = 0;
            int clientsWithPartialMessages = 0;
            int clientsWithAllMessages = 0;
            
            for (int x = 0; x < numClients; x++) {
                int msgCount = _receiveClients[x].getMessageCount();
                if (msgCount >= 0 && msgCount <= numMsgsExpected) {
                    messageCounts[msgCount]++;
                }
                
                if (msgCount == 0) {
                    clientsWithZeroMessages++;
                } else if (msgCount < numMsgsExpected) {
                    clientsWithPartialMessages++;
                    // Log details for first 10 clients with partial messages
                    if (clientsWithPartialMessages <= 10) {
                        LOG.severe("Client #" + x + " received only " + msgCount + "/" + numMsgsExpected +
                                  " messages, limit reached: " + _receiveClients[x].limitReached() +
                                  ", session open: " + (_receiveClients[x].getSession() != null &&
                                                       _receiveClients[x].getSession().isOpen()));
                    }
                } else {
                    clientsWithAllMessages++;
                }
            }
            
            LOG.severe("=== MESSAGE COUNT SUMMARY ===");
            LOG.severe("Clients with 0 messages: " + clientsWithZeroMessages);
            LOG.severe("Clients with partial messages (1-" + (numMsgsExpected-1) + "): " + clientsWithPartialMessages);
            LOG.severe("Clients with all " + numMsgsExpected + " messages: " + clientsWithAllMessages);
            LOG.severe("Message count distribution:");
            for (int i = 0; i <= numMsgsExpected; i++) {
                if (messageCounts[i] > 0) {
                    LOG.severe("  " + messageCounts[i] + " clients received " + i + " messages");
                }
            }
            
            if (_publishClient != null) {
                LOG.severe("Publisher client message count: " + _publishClient.getMessageCount());
            }
            
            mctr.setTestTimedout(true);
            while (WsocTestContext.completeLatch.getCount() > 0) {
                WsocTestContext.completeLatch.countDown();
            }
        } else {
            LOG.info("Test completed successfully within timeout");
            
            // Log success summary
            int clientsCompleted = 0;
            for (int x = 0; x < numClients; x++) {
                if (_receiveClients[x].getMessageCount() >= numMsgsExpected) {
                    clientsCompleted++;
                }
            }
            LOG.info("All " + clientsCompleted + " clients completed successfully");
        }

        // Log final message counts before closing (only if there were issues)
        if (mctr.getTestTimedout()) {
            LOG.info("=== Final Message Counts (First 20 clients) ===");
            for (int x = 0; x < Math.min(20, numClients); x++) {
                LOG.info("Receiver client #" + x + ": " + _receiveClients[x].getMessageCount() +
                        " messages (expected: " + numMsgsExpected + ")");
            }
            if (numClients > 20) {
                LOG.info("... (" + (numClients - 20) + " more clients not shown)");
            }
            if (_publishClient != null) {
                LOG.info("Publisher client: " + _publishClient.getMessageCount() + " messages");
            }
        }

        // We'll close the publisher first
        if (_publishEndpoint != null) {
            if (_publishTask != null) {
                LOG.info("Shutting down publisher executor");
                //       java.lang.Thread.sleep(1000);
                publishExecutor.shutdownNow();
            }
            LOG.info("Closing publisher session");
            closeSession(_publishClient);
            //  java.lang.Thread.sleep(1000);

        }

        LOG.info("Closing receiver sessions");
        for (int x = 0; x < numClients; x++) {
            closeSession(_receiveClients[x]);
        }

        LOG.info("=== MultiClientRunner.runTest END ===");
        return mctr;

    }

    private void closeSession(WsocTestContext wtc) throws Exception {
        Session sess = wtc.getSession();
        if (sess != null) {
            if (sess.isOpen()) {
                LOG.info("Reached max messages or test timeout, closing wsoc session for " + wtc);
                sess.close();
            }
        }
    }

    private WsocTestContext connectClient(Object endpoint, WebSocketContainer c, int maxMessages, boolean messagesCountOnly) throws Exception {
        LOG.info("connectClient: Creating WsocTestContext with maxMessages=" + maxMessages +
                ", messagesCountOnly=" + messagesCountOnly);
        WsocTestContext wct = new WsocTestContext(maxMessages, messagesCountOnly);

        if (!(endpoint instanceof TestHelper)) {
            LOG.severe("ERROR: Endpoint does not implement TestHelper: " + endpoint.getClass().getName());
            throw new WsocTestException("Test class does not implement TestHelper, can't run this test.");
        }
        TestHelper th = (TestHelper) endpoint;
        th.addTestResponse(wct);
        LOG.info("TestHelper.addTestResponse called for endpoint: " + endpoint.getClass().getName());

        if (endpoint instanceof Endpoint) {
            LOG.info("Connecting programmatic endpoint to: " + _uri);
            wct.addSession(c.connectToServer((Endpoint) endpoint, _cfg, _uri));
        }
        else {
            LOG.info("Connecting annotated endpoint to: " + _uri);
            wct.addSession(c.connectToServer(endpoint, _uri));
        }
        LOG.info("Client connected successfully, session: " + wct.getSession());
        return wct;

    }
}
