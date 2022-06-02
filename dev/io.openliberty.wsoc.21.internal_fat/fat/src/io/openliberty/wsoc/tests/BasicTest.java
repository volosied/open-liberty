/*******************************************************************************
 * Copyright (c) 2013, 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package io.openliberty.wsoc.tests;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.spec.JavaArchive;
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

import componenttest.annotation.AllowedFFDC;
import componenttest.annotation.ExpectedFFDC;
import componenttest.annotation.Server;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.custom.junit.runner.Mode;
import componenttest.custom.junit.runner.Mode.TestMode;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.utils.HttpUtils;

import io.openliberty.wsoc.tests.all.AnnotatedTest;

import io.openliberty.wsoc.util.OnlyRunNotOnZRule;
import io.openliberty.wsoc.util.WebServerControl;
import io.openliberty.wsoc.util.WebServerSetup;
import io.openliberty.wsoc.util.wsoc.WsocTest;

// comment #1 (referenced below): Some tests use the AllowedFFDC annotation because these Jetty client test will not run on Z, as desired
// due to the notOnZRule, but the test framework will still look for the "ExpectedFFDC" if that annotation is used, and not finding it, will
// fail the test on Z.

// comment #2 (referenced below): Some tests we found didn't run for "test" reasons, not product code reasons, test were commented out and
// not deleted so the same pattern would not be repeated.

/**
 * Tests WebSocket Stuff
 *
 * @author unknown
 */
@RunWith(FATRunner.class)
public class BasicTest {
    public static final String SERVER_NAME = "basicTestServer";

    @Server(SERVER_NAME)
    public static LibertyServer LS;

    private static WebServerSetup bwst = null;

    @Rule
    public final TestRule notOnZRule = new OnlyRunNotOnZRule();

    private static WsocTest wt = null;
    private static AnnotatedTest at = null;

    private static final Logger LOG = Logger.getLogger(BasicTest.class.getName());

    private static final String BASIC_JAR_NAME = "basic";
    private static final String BASIC_WAR_NAME = "basic";

    @BeforeClass
    public static void setUp() throws Exception {
        // // Build the basic jar to add to the war app as a lib
        // JavaArchive BasicJar = ShrinkHelper.buildJavaArchive(BASIC_JAR_NAME + ".jar",
        //                                                      "basic.jar");
        // Build the war app and add the dependencies
        WebArchive BasicApp = ShrinkHelper.buildDefaultApp(BASIC_WAR_NAME + ".war",
                                                           "basic.war",
                                                           "basic.war.*",
                                                        //    "basic.war.coding",
                                                        //    "basic.war.configurator",
                                                        //    "basic.war.servlet",
                                                        //    "basic.war.utils",
                                                           "io.openliberty.wsoc.common",
                                                           "io.openliberty.wsoc.util.wsoc",
                                                           "io.openliberty.wsoc.tests.all",
                                                           "io.openliberty.wsoc.endpoints.client.basic");

        BasicApp = (WebArchive) ShrinkHelper.addDirectory(BasicApp, "test-applications/" + BASIC_WAR_NAME + ".war/resources");
        // BasicApp = BasicApp.addAsLibraries(BasicJar);
        ShrinkHelper.exportDropinAppToServer(LS, BasicApp);

        LS.startServer();
        LS.waitForStringInLog("CWWKZ0001I.* " + BASIC_WAR_NAME);
        bwst = new WebServerSetup(LS);
        wt = new WsocTest(LS, false);
        at = new AnnotatedTest(wt);
        bwst.setUp();
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
        bwst.tearDown();
    }

    protected WebResponse runAsLSAndVerifyResponse(String className, String testName) throws Exception {
        int securePort = 0, port = 0;
        String host = "";
        LibertyServer server = LS;
        if (WebServerControl.isWebserverInFront()) {
            try {
                host = WebServerControl.getHostname();
                securePort = WebServerControl.getSecurePort();
                port = Integer.valueOf(WebServerControl.getPort()).intValue();
            } catch (Exception e) {
                throw new RuntimeException("Failed to get ports or host from webserver", e);
            }
        } else {
            securePort = server.getHttpDefaultSecurePort();
            host = server.getHostname();
            port = server.getHttpDefaultPort();
        }
        WebBrowser browser = WebBrowserFactory.getInstance().createWebBrowser((File) null);
        String[] expectedInResponse = {
                                        "SuccessfulTest"
        };
        return verifyResponse(browser,
                              "/basic/SingleRequest?classname=" + className + "&testname=" + testName + "&targethost=" + host + "&targetport=" + port
                                       + "&secureport=" + securePort,
                              expectedInResponse);
    }

    /**
     * Submits an HTTP request at the path specified by <code>resource</code>,
     * and verifies that the HTTP response body contains the all of the supplied text
     * specified by the array of * <code>expectedResponses</code>
     *
     * @param webBrowser        the browser used to submit the request
     * @param resource          the resource on the shared server to request
     * @param expectedResponses an array of the different subsets of the text expected from the HTTP response
     * @return the HTTP response (in case further validation is required)
     * @throws Exception if the <code>expectedResponses</code> is not contained in the HTTP response body
     */
    public WebResponse verifyResponse(WebBrowser webBrowser, String resource, String[] expectedResponses) throws Exception {
        WebResponse response = webBrowser.request(HttpUtils.createURL(LS, resource).toString());
        LOG.info("Response from webBrowser: " + response.getResponseBody());
        for (String textToFind : expectedResponses) {
            response.verifyResponseBodyContains(textToFind);
        }

        return response;
    }

    //
    //
    //  ANNOTATED TESTS
    //
    //

    @Mode(TestMode.LITE)
    @Test
    public void testAnnotatedByteArraySuccess() throws Exception {
        LS.setMarkToEndOfLog();
        at.testAnnotatedByteArraySuccess();
        String result  = LS.waitForStringInLogUsingMark("Session idle timeout is default which is no time out");
        System.out.println(result);
        assertNotNull("Timeout message not found!", result);
    }

    @Mode(TestMode.FULL)
    @Test
    public void testSSCAnnotatedByteArraySuccess() throws Exception {
        this.runAsLSAndVerifyResponse("AnnotatedTest", "testAnnotatedByteArraySuccess");
    }

}