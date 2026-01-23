/*******************************************************************************
 * Copyright (c) 2020, 2021 IBM Corporation and others.
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
package com.ibm.ws.messaging.JMS20security.fat.DCFTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ibm.websphere.simplicity.ShrinkHelper;
import com.ibm.ws.messaging.JMS20security.fat.TestUtils;

import componenttest.custom.junit.runner.FATRunner;
import componenttest.custom.junit.runner.Mode;
import componenttest.custom.junit.runner.Mode.TestMode;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.impl.LibertyServerFactory;

/**
 *
 */
@RunWith(FATRunner.class)
@Mode(TestMode.FULL)
public class JMSDefaultConnectionFactorySecurityTest {

    private static final LibertyServer client_server = LibertyServerFactory.getLibertyServer("ClientServer");
    private static final LibertyServer consumer_server = LibertyServerFactory.getLibertyServer("ConsumerServer");


    private static final int PORT = client_server.getHttpDefaultPort();
    private static final String HOST = client_server.getHostname();

    boolean val = false;

    private boolean runInServlet(String test) throws IOException {

        boolean result = false;
        URL url = new URL("http://" + HOST + ":" + PORT
                          + "/JMSDCFSecurity?test=" + test);
        System.out.println("The Servlet URL is : " + url.toString());
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        try {
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestMethod("GET");
            con.connect();

            InputStream is = con.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String sep = System.lineSeparator();
            StringBuilder lines = new StringBuilder();
            for (String line = br.readLine(); line != null; line = br.readLine())
                lines.append(line).append(sep);

            if (lines.indexOf(test + " COMPLETED SUCCESSFULLY") < 0) {
                org.junit.Assert.fail("Missing success message in output. "
                                      + lines);
                result = false;
            } else
                result = true;

            return result;

        } finally {
            con.disconnect();
        }
    }

    @BeforeClass
    public static void testConfigFileChange() throws Exception {

        consumer_server.copyFileToLibertyInstallRoot("lib/features",
                                             "features/testjmsinternals-1.0.mf");
        consumer_server.copyFileToLibertyServerRoot("resources/security",
                                            "serverLTPAKeys/cert.der");
        consumer_server.copyFileToLibertyServerRoot("resources/security",
                                            "serverLTPAKeys/ltpa.keys");
        consumer_server.copyFileToLibertyServerRoot("resources/security",
                                            "serverLTPAKeys/ltpaFIPS.keys");
        consumer_server.copyFileToLibertyServerRoot("resources/security",
                                            "serverLTPAKeys/mykey.jks");

        client_server.copyFileToLibertyInstallRoot("lib/features",
                                            "features/testjmsinternals-1.0.mf");
        client_server.copyFileToLibertyServerRoot("resources/security",
                                           "clientLTPAKeys/mykey.jks");

        TestUtils.addDropinsWebApp(client_server, "JMSDCFSecurity", "web");
        TestUtils.addDropinsWebApp(client_server, "JMSContextInject", "web");

        startAppservers();
    }

    /**
     * Start both the JMSConsumerClient local and remote messaging engine AppServers.
     *
     * @throws Exception
     */
    private static void startAppservers() throws Exception {
        client_server.setServerConfigurationFile("DCFResSecurityClient.xml");
        consumer_server.setServerConfigurationFile("TestServer1_ssl.xml");
        client_server.startServer("DCFTestClient.log");
        consumer_server.startServer("DCFServer.log");

        // CWWKF0011I: The TestServer1 client_server is ready to run a smarter planet. The TestServer1 client_server started in 6.435 seconds.
        // CWSID0108I: JMS client_server has started.
        // CWWKS4105I: LTPA configuration is ready after 4.028 seconds.
        for (String messageId : new String[] { "CWWKF0011I.*", "CWSID0108I.*", "CWWKS4105I.*" }) {
            String waitFor = client_server.waitForStringInLog(messageId, client_server.getMatchingLogFile("messages.log"));
            assertNotNull("Server message " + messageId + " not found", waitFor);
            waitFor = consumer_server.waitForStringInLog(messageId, consumer_server.getMatchingLogFile("messages.log"));
            assertNotNull("Server1 message " + messageId + " not found", waitFor);
        }
    }

    @org.junit.AfterClass
    public static void tearDown() {
        try {
            System.out.println("Stopping client client_server");
            client_server.stopServer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            System.out.println("Stopping engine client_server");
            consumer_server.stopServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        ShrinkHelper.cleanAllExportedArchives();
    }

    @Test
    public void testP2P_TCP_SecOn() throws Exception {

        val = runInServlet("testP2P_TCP_SecOn");
        assertTrue("testP2P_TCP_SecOn failed ", val);

    }

    @Test
    public void testPubSub_TCP_SecOn() throws Exception {

        val = runInServlet("testPubSub_TCP_SecOn");
        assertTrue("testPubSub_TCP_SecOn failed ", val);

    }

    @Test
    public void testPubSubDurable_TCP_SecOn() throws Exception {

        val = runInServlet("testPubSubDurable_TCP_SecOn");
        assertTrue("testPubSubDurable_TCP_SecOn failed ", val);

    }

    @Mode(TestMode.FULL)
    @Test
    public void testP2PMQ_TCP_SecOn() throws Exception {
        client_server.stopServer();
        consumer_server.stopServer();
        client_server.setServerConfigurationFile("DCFResSecurityClientMQ.xml");
        consumer_server.startServer();
        String waitFor = consumer_server.waitForStringInLog("CWWKF0011I.*", consumer_server.getMatchingLogFile("messages.log"));
        assertNotNull("Server ready message not found", waitFor);
        client_server.startServer();
        waitFor = client_server.waitForStringInLog("CWWKF0011I.*", client_server.getMatchingLogFile("messages.log"));
        assertNotNull("Server ready message not found", waitFor);
        val = runInServlet("testP2PMQ_TCP_SecOn");
        assertTrue("testP2PMQ_TCP_SecOn failed ", val);

        client_server.stopServer();
        consumer_server.stopServer();
        startAppservers();
    }
}
