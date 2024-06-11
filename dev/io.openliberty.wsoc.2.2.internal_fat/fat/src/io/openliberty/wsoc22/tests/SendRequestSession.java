
/*******************************************************************************
 * Copyright (c) 2022, 2024 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package io.openliberty.wsoc22.tests;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import com.ibm.websphere.simplicity.ShrinkHelper;
import com.ibm.ws.fat.util.browser.WebBrowser;
import com.ibm.ws.fat.util.browser.WebBrowserFactory;
import com.ibm.ws.fat.util.browser.WebResponse;

import componenttest.annotation.Server;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.custom.junit.runner.Mode;
import componenttest.custom.junit.runner.Mode.TestMode;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.utils.HttpUtils;
// import io.openliberty.wsoc.tests.all.MiscTest;
// import io.openliberty.wsoc.tests.all.TimeOutTest;
// import io.openliberty.wsoc.tests.all.UpgradeTest;
// import io.openliberty.wsoc.tests.all.UserPropertiesTest;
// import io.openliberty.wsoc.util.OnlyRunNotOnZRule;
// import io.openliberty.wsoc.util.WebServerControl;
// import io.openliberty.wsoc.util.WebServerSetup;
// import io.openliberty.wsoc.util.wsoc.WsocTest;
// import io.openliberty.wsoc.tests.all.SSLTest;

import org.asynchttpclient.ws.*;
import org.asynchttpclient.*;
import io.netty.util.concurrent.*;
/**
 * WebSocket 2.2 Test
 */
@RunWith(FATRunner.class)
public class SendRequestSession {

    @Server("sessionTestServer")
    public static LibertyServer LS;

    // private static WebServerSetup bwst = null;

    // @Rule
    // public final TestRule notOnZRule = new OnlyRunNotOnZRule();

    // private static WsocTest wt = null;
    // private static WsocTest wt_secure = null;

    private static final Logger LOG = Logger.getLogger(SendRequestSession.class.getName());

    private static final String SESSION_WAR_NAME = "session";

    @BeforeClass
    public static void setUp() throws Exception {

        // Build the war app and add the dependencies
        WebArchive SessionApp = ShrinkHelper.buildDefaultApp(SESSION_WAR_NAME + ".war",
                "session.war",
                "session.war.*");

        SessionApp = (WebArchive) ShrinkHelper.addDirectory(SessionApp,
                "test-applications/" + SESSION_WAR_NAME + ".war/resources");
        // BasicApp = BasicApp.addAsLibraries(BasicJar);
        ShrinkHelper.exportDropinAppToServer(LS, SessionApp);

        LS.startServer();
        LS.waitForStringInLog("CWWKZ0001I.* " + SESSION_WAR_NAME);
        // bwst = new WebServerSetup(LS);
        // wt = new WsocTest(LS, false);
        // wt_secure = new WsocTest(LS, true);
        // bwst.setUp();

        // Allow Jetty to finish starting up -
        // https://github.com/OpenLiberty/open-liberty/issues/23172
        // Updated to 5100 - Jan 2nd 2024
        Thread.sleep(5100);
    }

    @AfterClass
    public static void tearDown() throws Exception {

        // give the system 10 seconds to settle down before stopping
        try {
            Thread.sleep(10000);
        } catch (InterruptedException x) {

        }

        if (LS != null && LS.isStarted()) {
            LS.stopServer("CWWKH0023E", "CWWKH0020E", "CWWKH0039E", "CWWKH0040E", "SRVE8115W", "SRVE0190E");
        }
        //bwst.tearDown();
    }

    /*
     * testSSC means liberty wsoc impl is the client and server
     * tests above use Jetty as the client
     */
    @Mode(TestMode.LITE)
    @Test
    public void testSession() throws Exception {
        WebSocketUpgradeHandler.Builder upgradeHandlerBuilder = new WebSocketUpgradeHandler.Builder();
        WebSocketUpgradeHandler wsHandler = upgradeHandlerBuilder
                .addWebSocketListener(new WebSocketListener() {
                    @Override
                    public void onOpen(WebSocket websocket) {
                        // WebSocket connection opened
                        System.out.println("Opened Websocket");
                    }

                    @Override
                    public void onClose(WebSocket websocket, int code, String reason) {
                        // WebSocket connection closed                        
                    }

                    @Override
                    public void onError(Throwable t) {
                        // WebSocket connection error
                    }

                    @Override
                    public void onTextFrame(String payload, boolean finalFragment, int rsv){
                        // Log message
                        System.out.println("Debugging: " + payload);
                    }
                }).build();

        WebSocket webSocketClient = Dsl.asyncHttpClient()
                .prepareGet("ws://" + 
                            LS.getHostname() + ":" + 
                            LS.getHttpDefaultPort() + 
                            "/session/echo")
                .setRequestTimeout(5000)
                .execute(wsHandler)
                .get();
        
        if (webSocketClient.isOpen()) {
            System.out.println("sending message");
            webSocketClient.sendTextFrame("test message");
        }
    }

}
