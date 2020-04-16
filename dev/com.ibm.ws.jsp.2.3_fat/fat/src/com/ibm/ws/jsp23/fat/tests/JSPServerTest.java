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
public class JSPServerTest extends LoggingTest {

    private static final String JSP23_APP_NAME = "TestJSP2.3";
    private static final String PI44611_APP_NAME = "PI44611";
    private static final String PI59436_APP_NAME = "PI59436";

    protected static final Map<String, String> testUrlMap = new HashMap<String, String>();

    @ClassRule
    public static SharedServer SHARED_SERVER = new SharedServer("jspServer");

    @BeforeClass
    public static void setup() throws Exception {
        ShrinkHelper.defaultDropinApp(SHARED_SERVER.getLibertyServer(),
                                      JSP23_APP_NAME + ".war",
                                      "com.ibm.ws.jsp23.fat.testjsp23.servlets");
        SHARED_SERVER.getLibertyServer().addInstalledAppForValidation(JSP23_APP_NAME);

        ShrinkHelper.defaultDropinApp(SHARED_SERVER.getLibertyServer(),
                                      PI44611_APP_NAME + ".war");
        SHARED_SERVER.getLibertyServer().addInstalledAppForValidation(PI44611_APP_NAME);

        ShrinkHelper.defaultDropinApp(SHARED_SERVER.getLibertyServer(),
                                      PI59436_APP_NAME + ".war");
        SHARED_SERVER.getLibertyServer().addInstalledAppForValidation(PI59436_APP_NAME);

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
        this.verifyResponse("/TestJSP2.3/SimpleTestServlet", "Hello World");
    }

        /**
     * Test JSP 2.3 Resolution of Variables and their Properties
     *
     * @throws Exception
     *                       if something goes wrong
     */
    // No need to run against cdi-2.0 since this test does not use CDI at all.
    @SkipForRepeat("CDI-2.0")
    @Test
    public void testJSP23ResolutionVariableProperties() throws Exception {
        WebBrowser browser = this.createWebBrowserForTestCase();
        this.verifyResponse(browser, "/TestJSP2.3/ResolutionVariablesPropertiesServlet",
                            new String[] { "class org.apache.el.stream.StreamELResolverImpl",
                                           "class javax.el.StaticFieldELResolver",
                                           "class javax.el.MapELResolver",
                                           "class javax.el.ResourceBundleELResolver",
                                           "class javax.el.ListELResolver",
                                           "class javax.el.ArrayELResolver",
                                           "class javax.el.BeanELResolver",
                                           "The order and number of ELResolvers from the CompositeELResolver are correct!",
                                           "Testing StaticFieldELResolver with Boolean.TRUE (Expected: true): true",
                                           "Testing StaticFieldELResolver with Integer.parseInt (Expected: 86): 86",
                                           "Testing StreamELResolver with distinct method (Expected: [1, 4, 3, 2, 5]): [1, 4, 3, 2, 5]",
                                           "Testing StreamELResolver with filter method (Expected: [4, 3, 5, 3]): [4, 3, 5, 3]"
                            });
    }


    /**
     * Test Servlet 3.1 request/response API
     *
     * @throws Exception
     *                       if something goes wrong
     */
    // No need to run against cdi-2.0 since this test does not use CDI at all.
    @SkipForRepeat("CDI-2.0")
    @Test
    public void testServlet31RequestResponse() throws Exception {
        WebBrowser browser = this.createWebBrowserForTestCase();
        this.verifyResponse(browser,
                            "/TestJSP2.3/Servlet31RequestResponseTest.jsp?firstName=John&lastName=Smith",
                            new String[] {
                                           "JSP to test Servlet 3.1 Request and Response",
                                           "Testing BASIC_AUTH static field from HttpServletRequest (Expected: BASIC): BASIC",
                                           "Testing request.getParameterNames method (Expected: [firstName, lastName]): [lastName, firstName]",
                                           "Testing request.getParameter method (Expected: John): John",
                                           "Testing request.getParameter method (Expected: Smith): Smith",
                                           "Testing request.getQueryString method (Expected: firstName=John&lastName=Smith): firstName=John&lastName=Smith",
                                           "Testing request.getContextPath method (Expected: /TestJSP2.3): /TestJSP2.3",
                                           "Testing request.getRequestURI method (Expected: /TestJSP2.3/Servlet31RequestResponseTest.jsp): /TestJSP2.3/Servlet31RequestResponseTest.jsp",
                                           "Testing request.getMethod method (Expected: GET): GET",
                                           "Testing request.getContentLengthLong method (Expected: -1): -1",
                                           "Testing request.getProtocol method (Expected: HTTP/1.1): HTTP/1.1",
                                           "Testing SC_NOT_FOUND static field from HttpServletResponse (Expected: 404): 404",
                                           "Testing response.getStatus method (Expected: 200): 200",
                                           "Testing response.getBufferSize method (Expected: 4096): 4096",
                                           "Testing response.getCharacterEncoding method (Expected: ISO-8859-1): ISO-8859-1",
                                           "Testing response.getContentType method (Expected: text/html; charset=ISO-8859-1): text/html; charset=ISO-8859-1",
                                           "Testing response.containsHeader method (Expected: true): true",
                                           "Testing response.isCommitted method (Expected: false): false"
                            });
    }

    // No need to run against cdi-2.0 since this test does not use CDI at all.
    @SkipForRepeat("CDI-2.0")
    @Mode(TestMode.FULL)
    @Test
    public void testPI44611() throws Exception {
        this.verifyResponse(createWebBrowserForTestCase(), "/PI44611/PI44611.jsp", "Test passed!");
    }

    /**
     * This test makes a request to a jsp page and expects no NullPointerExceptions,
     * and the text "Test passed." to be output.
     *
     * @throws Exception
     */
    // No need to run against cdi-2.0 since this test does not use CDI at all.
    @SkipForRepeat("CDI-2.0")
    @Mode(TestMode.FULL)
    @Test
    public void testPI59436() throws Exception {
        this.verifyResponse(createWebBrowserForTestCase(), "/PI59436/PI59436.jsp", "Test passed.");
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