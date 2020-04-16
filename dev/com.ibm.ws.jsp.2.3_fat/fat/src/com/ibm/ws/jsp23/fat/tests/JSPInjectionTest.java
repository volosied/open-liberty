/*******************************************************************************
 * Copyright (c) 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.jsp23.fat.tests;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ibm.websphere.simplicity.ShrinkHelper;
import com.ibm.ws.fat.util.LoggingTest;
import com.ibm.ws.fat.util.SharedServer;
import com.ibm.ws.fat.util.browser.WebBrowser;

import componenttest.annotation.SkipForRepeat;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.custom.junit.runner.Mode;
import componenttest.custom.junit.runner.Mode.TestMode;

/**
 * All JSP 2.3 tests with all applicable server features enabled.
 *
 * Tests that just need to drive a simple request using our WebBrowser object can be placed in this class.
 *
 * If a test needs httpunit it should more than likely be placed in the JSPServerHttpUnit test class.
 */
@RunWith(FATRunner.class)
@SkipForRepeat("EE9_FEATURES")
public class JSPInjectionTest extends LoggingTest {

    private static final String JSP23_APP_NAME = "TestInjection";
    // private static final String PI44611_APP_NAME = "PI44611";
    // private static final String PI59436_APP_NAME = "PI59436";

    protected static final Map<String, String> testUrlMap = new HashMap<String, String>();

    @ClassRule
    public static SharedServer SHARED_SERVER = new SharedServer("cdiServer");

    @BeforeClass
    public static void setup() throws Exception {
        ShrinkHelper.defaultDropinApp(SHARED_SERVER.getLibertyServer(),
                                      JSP23_APP_NAME + ".war",
                                      "com.ibm.ws.jsp23.fat.testinjection.beans",
                                      "com.ibm.ws.jsp23.fat.testinjection.interceptors",
                                      "com.ibm.ws.jsp23.fat.testinjection.listeners",
                                      "com.ibm.ws.jsp23.fat.testinjection.servlets",
                                      "com.ibm.ws.jsp23.fat.testinjection.tagHandler");
        SHARED_SERVER.getLibertyServer().addInstalledAppForValidation(JSP23_APP_NAME);

        SHARED_SERVER.startIfNotStarted();
    }

    @AfterClass
    public static void testCleanup() throws Exception {
        // Stop the server
        if (SHARED_SERVER.getLibertyServer() != null && SHARED_SERVER.getLibertyServer().isStarted()) {
            SHARED_SERVER.getLibertyServer().stopServer();
        }
    }

    /**
     * Sample test
     *
     * @throws Exception
     *                       if something goes horribly wrong
     */
    // No need to run against cdi-2.0 since this test does not use CDI at all.
    @SkipForRepeat("CDI-2.0")
    @Test
    public void testServlet() throws Exception {
        // Use the SharedServer to verify a response.
        this.verifyResponse("/TestInjection/SimpleTestServlet", "Hello World");
    }
   
    /**
     * Test Tag Handlers with CDI.
     *
     * @throws Exception
     */
    @Test
    public void testTag1() throws Exception {
        // Each entry in the array is an expected output in the response
        String[] expectedInResponse = {
                                        "<b>Test 1:</b> Test Start",
                                        "<b>Test 2:</b> Message: DependentBean Hit SessionBean Hit RequestBean Hit ...constructor injection OK ...interceptor OK",
        };

        this.verifyResponse(createWebBrowserForTestCase(),"/TestInjection/Tag1.jsp", expectedInResponse);
    }

    /**
     * Test Tag Handlers with CDI Method Injection.
     *
     * @throws Exception
     */
    @Test
    public void testTag2() throws Exception {
        // Each entry in the array is an expected output in the response
        String[] expectedInResponse = {
                                        "<b>Test 1:</b> Test Start",
                                        "<b>Test 2:</b> Message: BeanCounters are OK"
        };

        this.verifyResponse(createWebBrowserForTestCase(),"/TestInjection/Tag2.jsp", expectedInResponse);
    }

    /**
     * Test the tag library event listeners injected using CDI.
     * Constructor injection.
     *
     * @throws Exception
     */
    @Test
    public void testTagLibraryEventListenerCI() throws Exception {
        final int iterations = 5;
        final String URI ="/TestInjection/TagLibraryEventListenerCI.jsp?increment=true";
        // Each entry in the array is an expected output in the response
        WebBrowser browser = this.createWebBrowserForTestCase();
        WebBrowser browser1 = this.createWebBrowserForTestCase(); //Used to test @SessionScoped
        for (int i = 1; i <= iterations; i++) {
            String[] expectedInResponse = {
                                            "<li>TestConstructorInjection index: " + i + "</li>",
                                            "<li>TestConstructorInjectionRequest index: " + 1 + "</li>",
                                            "<li>TestConstructorInjectionApplication index: " + i + "</li>",
                                            "<li>TestConstructorInjectionSession index: " + i + "</li>"
            };

            this.verifyResponse(browser, URI, expectedInResponse);
        }

        //Testing if a new TestConstructorInjectionSession was injected when another session is used
        String[] expectedInResponse = {
                                        "<li>TestConstructorInjection index: " + (iterations + 1) + "</li>",
                                        "<li>TestConstructorInjectionRequest index: " + 1 + "</li>",
                                        "<li>TestConstructorInjectionApplication index: " + (iterations + 1) + "</li>",
                                        "<li>TestConstructorInjectionSession index: " + 1 + "</li>"
        };

        this.verifyResponse(browser1, URI, expectedInResponse);
    }

    /**
     * Test the tag library event listeners injected using CDI.
     * Field injection.
     *
     * @throws Exception
     */
    @Test
    public void testTagLibraryEventListenerFI() throws Exception {
        final int iterations = 5;
        final String URI ="/TestInjection/TagLibraryEventListenerFI.jsp?increment=true";
        // Each entry in the array is an expected output in the response
        WebBrowser browser = this.createWebBrowserForTestCase();
        WebBrowser browser1 = this.createWebBrowserForTestCase(); //Used to test @SessionScoped
        for (int i = 1; i <= iterations; i++) {
            String[] expectedInResponse = {
                                            "<li>TestFieldInjection index: " + i + "</li>",
                                            "<li>TestFieldInjectionRequest index: " + 1 + "</li>",
                                            "<li>TestFieldInjectionApplication index: " + i + "</li>",
                                            "<li>TestFieldInjectionSession index: " + i + "</li>"
            };

            this.verifyResponse(browser, URI, expectedInResponse);
        }

        //Testing if a new TestFieldInjectionSession was injected when another session is used
        String[] expectedInResponse = {
                                        "<li>TestFieldInjection index: " + (iterations + 1) + "</li>",
                                        "<li>TestFieldInjectionRequest index: " + 1 + "</li>",
                                        "<li>TestFieldInjectionApplication index: " + (iterations + 1) + "</li>",
                                        "<li>TestFieldInjectionSession index: " + 1 + "</li>"
        };

        this.verifyResponse(browser1, URI, expectedInResponse);
    }

    /**
     * Test the tag library event listeners injected using CDI.
     * Method injection.
     *
     * @throws Exception
     */
    @Test
    public void testTagLibraryEventListenerMI() throws Exception {
        final int iterations = 5;
        final String URI ="/TestInjection/TagLibraryEventListenerMI.jsp?increment=true";
        // Each entry in the array is an expected output in the response
        WebBrowser browser = this.createWebBrowserForTestCase();
        WebBrowser browser1 = this.createWebBrowserForTestCase(); //Used to test @SessionScoped
        for (int i = 1; i <= iterations; i++) {
            String[] expectedInResponse = {
                                            "<li>TestMethodInjection index: " + i + "</li>",
                                            "<li>TestMethodInjectionRequest index: " + 1 + "</li>",
                                            "<li>TestMethodInjectionApplication index: " + i + "</li>",
                                            "<li>TestMethodInjectionSession index: " + i + "</li>"
            };

            this.verifyResponse(browser, URI, expectedInResponse);
        }

        //Testing if a new TestMethodInjectionSession was injected when another session is used
        String[] expectedInResponse = {
                                        "<li>TestMethodInjection index: " + (iterations + 1) + "</li>",
                                        "<li>TestMethodInjectionRequest index: " + 1 + "</li>",
                                        "<li>TestMethodInjectionApplication index: " + (iterations + 1) + "</li>",
                                        "<li>TestMethodInjectionSession index: " + 1 + "</li>"
        };

        this.verifyResponse(browser1, URI, expectedInResponse);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.ws.fat.util.LoggingTest#getSharedServer()
     */
    @Override
    protected SharedServer getSharedServer() {
        return SHARED_SERVER;
    }

}