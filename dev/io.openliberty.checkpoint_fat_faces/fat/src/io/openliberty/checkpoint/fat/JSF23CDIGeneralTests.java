/*******************************************************************************
 * Copyright (c) 2017, 2025 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package io.openliberty.checkpoint.fat;

import static componenttest.annotation.SkipForRepeat.EE10_OR_LATER_FEATURES;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.Arrays;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.ibm.websphere.simplicity.ShrinkHelper;
import com.ibm.websphere.simplicity.ShrinkHelper.DeployOptions;
import com.ibm.websphere.simplicity.log.Log;
import io.openliberty.checkpoint.fat.JSFUtils;

import io.openliberty.checkpoint.spi.CheckpointPhase;
import componenttest.annotation.CheckpointTest;
import componenttest.annotation.ExpectedFFDC;
import componenttest.annotation.Server;
import componenttest.annotation.SkipForRepeat;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.custom.junit.runner.Mode;
import componenttest.custom.junit.runner.Mode.TestMode;
import componenttest.rules.repeater.JakartaEEAction;
import componenttest.topology.impl.LibertyServer;
import junit.framework.Assert;

import io.openliberty.checkpoint.spi.CheckpointPhase;
import componenttest.annotation.CheckpointTest;

/**
 * General JSF 2.3 test cases the also require CDI.
 * 
 * Copied from com.ibm.ws.jsf.2.3_fat.2 
 */
@CheckpointTest
@RunWith(FATRunner.class)
public class JSF23CDIGeneralTests {

    protected static final Class<?> c = JSF23CDIGeneralTests.class;

    private static boolean isEE10;

    @Rule
    public TestName name = new TestName();

    @Server("jsf23CDIGeneralServer")
    public static LibertyServer server;

    @BeforeClass
    public static void setup() throws Exception {
        isEE10 = JakartaEEAction.isEE10OrLaterActive();

        ShrinkHelper.defaultDropinApp(server, "PostRenderViewEvent.war", "com.ibm.ws.jsf23.fat.postrenderview.events");
        ShrinkHelper.defaultDropinApp(server, "CDIManagedProperty.war", "com.ibm.ws.jsf23.fat.cdi.managedproperty");
        ShrinkHelper.defaultDropinApp(server, "ConvertDateTime.war", "com.ibm.ws.jsf23.fat.convertdatetime.beans");
        ShrinkHelper.defaultDropinApp(server, "ConverterValidatorBehaviorInjectionTarget.war", "com.ibm.ws.jsf23.fat.converter.validator.behavior.injection.beans");
        ShrinkHelper.defaultDropinApp(server, "CDIIntegrationTest.war",
                                      "com.ibm.ws.jsf23.fat.cdi.integration.application",
                                      "com.ibm.ws.jsf23.fat.cdi.integration.beans",
                                      "com.ibm.ws.jsf23.fat.cdi.integration.viewhandler");

        WebArchive elImplicitObjectsViaCDIApp = ShrinkWrap.create(WebArchive.class, "ELImplicitObjectsViaCDI.war");
        elImplicitObjectsViaCDIApp.addPackage("com.ibm.ws.jsf23.fat.elimplicit.cdi.beans");
        ShrinkHelper.addDirectory(elImplicitObjectsViaCDIApp, "test-applications/" + "ELImplicitObjectsViaCDI.war" + "/resources");
        ShrinkHelper.addDirectory(elImplicitObjectsViaCDIApp,
                                  "test-applications/" + "ELImplicitObjectsViaCDI.war" + (isEE10 ? "/resourcesFaces40" : "/resourcesJSF23"));
        ShrinkHelper.exportDropinAppToServer(server, elImplicitObjectsViaCDIApp);

        server.setCheckpoint(CheckpointPhase.AFTER_APP_START, true, null);

        // Start the server and use the class name so we can find logs easily.
        // Many tests use the same server
        server.startServer(c.getSimpleName() + ".log");
    }

    @Before
    public void startServer() throws Exception {
        if (server != null && !server.isStarted()) {
            server.startServer(c.getSimpleName() + ".log");
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        // Stop the server
        if (server != null && server.isStarted()) {
            server.stopServer();
        }
    }

    /**
     * This test case drives a request to an application that has a PhaseListener that will
     * log a message before and after the RENDER_RESPONSE phase of the JSF lifecycle.
     *
     * During the RENDER_RESSPONE phase the PreRenderView and PostRenderView events should
     * be fired in order. We check the trace log to ensure that the logged messages are output
     * in the correct order.
     *
     * @throws Exception
     */
    @Mode(TestMode.FULL)
    @Test
    public void testPostRenderViewEvent() throws Exception {
        String contextRoot = "PostRenderViewEvent";
        try (WebClient webClient = new WebClient()) {

            // Construct the URL for the test
            URL url = JSFUtils.createHttpUrl(server, contextRoot, "");

            HtmlPage page = (HtmlPage) webClient.getPage(url);

            // Log the page for debugging if necessary in the future.
            Log.info(c, name.getMethodName(), page.asText());
            Log.info(c, name.getMethodName(), page.asXml());

            // Ensure the messages are output in the correct order to verify the events are fired
            // in the correct order.
            String beforeRenderResponse = "Before Render Response Phase";
            String afterRenderResponse = "After Render Response Phase";
            String preRenderView = "Processing PreRenderViewEvent";
            String postRenderView = "Processing PostRenderViewEvent";

            assertNotNull("The following String was not found in the trace log: " + beforeRenderResponse,
                          server.waitForStringInTraceUsingLastOffset(beforeRenderResponse));

            assertNotNull("The following String was not found in the trace log: " + preRenderView,
                          server.waitForStringInTraceUsingLastOffset(preRenderView));

            assertNotNull("The following String was not found in the trace log: " + postRenderView,
                          server.waitForStringInTraceUsingLastOffset(postRenderView));

            assertNotNull("The following String was not found in the trace log: " + afterRenderResponse,
                          server.waitForStringInTraceUsingLastOffset(afterRenderResponse));
        }

    }

    /**
     * This test is run on a server that has an application deployed that contains a
     * faces-config.xml with a version element of 2.3. The PostRenderViewEvent.war
     * has a faces-config.xml in it with the 2.3 version.
     *
     * This test will ensure the application with the faces-config.xml we are testing
     * has been started.
     *
     * The test will ensure that the following exception is not found in the trace.log:
     *
     * CWWKC2262E: The server is unable to process the 2.3 version and the
     * http://xmlns.jcp.org/xml/ns/javaee namespace in the /WEB-INF/faces-config.xml
     * deployment descriptor on line 5
     *
     * @throws Exception
     */
    @Mode(TestMode.FULL)
    @Test
    public void testFacesConfigVersion23() throws Exception {
        String appStarted = server.waitForStringInLog("CWWKZ0001I.*" + "Application PostRenderViewEvent", server.getConsoleLogFile());

        assertTrue("The PostRenderViewEvent application did not start.", appStarted != null);
        assertTrue("The CWWKC2262E exception was found in the trace.log when it should not have been.",
                   server.findStringsInTrace("CWWKC2262E").isEmpty());
    }

    /**
     * This is a test for the CDI @ManagedProperty. The test drives a request to a page
     * and ensures that the proper initial values are found.
     *
     * Then the values are updated and the test checks to ensure we get the proper
     * updated values.
     *
     * The beans that are used are testing that @ManagedPropery works for multiple different
     * types including arrays, parameterized List, and primitive
     *
     * See the CDIManagedProperty.war for more details.
     *
     * @throws Exception
     */
    @Test
    public void testCDIManagedProperty() throws Exception {
        String contextRoot = "CDIManagedProperty";
        try (WebClient webClient = new WebClient()) {

            String initalValue = "numberManagedProperty = 0 textManagedProperty = zero "
                                 + "listManagedProperty = zero stringArrayManagedProperty = "
                                 + "zero bean = com.ibm.ws.jsf23.fat.cdi.managedproperty.TestBean";

            String finalValue = "numberManagedProperty = 1 textManagedProperty = 2 "
                                + "listManagedProperty = 3 stringArrayManagedProperty = 4 bean = "
                                + "com.ibm.ws.jsf23.fat.cdi.managedproperty.TestBean";

            // Construct the URL for the test
            URL url = JSFUtils.createHttpUrl(server, contextRoot, "");

            HtmlPage page = (HtmlPage) webClient.getPage(url);

            // Log the page for debugging if necessary in the future.
            Log.info(c, name.getMethodName(), page.asText());
            Log.info(c, name.getMethodName(), page.asXml());

            HtmlTextInput input1 = (HtmlTextInput) page.getElementById("in1");
            HtmlTextInput input2 = (HtmlTextInput) page.getElementById("in2");
            HtmlTextInput input3 = (HtmlTextInput) page.getElementById("in3");
            HtmlTextInput input4 = (HtmlTextInput) page.getElementById("in4");

            String output = page.getElementById("out1").asText();

            // Assert the initial values of out1.
            assertTrue("The initial values were not correct. One or more of the @ManagedProperty injections failed.",
                       output.substring(0, output.indexOf("@")).equals(initalValue));

            // Now fill in the new values into the input fields
            input1.setValueAttribute("1");
            input2.setValueAttribute("2");
            input3.setValueAttribute("3");
            input4.setValueAttribute("4");

            // Now click the submit button
            page = page.getElementById("button1").click();

            Log.info(c, name.getMethodName(), page.asText());

            output = page.getElementById("out1").asText();

            // Assert the updated values of out1
            assertTrue("The updated values were not correct. One or more of the @ManagedProperty injections failed.",
                       output.substring(0, output.indexOf("@")).equals(finalValue));
        }
    }

    /**
     * Test to ensure that the EL Resolution of implicit objects works as expected
     * when CDI is being used.
     *
     * @throws Exception
     */
    @Test
    public void testELResolutionImplicitObjects() throws Exception {
        try (WebClient webClient = new WebClient()) {

            // Add a message to the header map.
            // Tested was changed to check for User-Agent in order to be consistent with Selenium Test above
            // webClient.addRequestHeader("headerMessage", "This is a test");

            // Construct the URL for the test
            String contextRoot = "ELImplicitObjectsViaCDI";
            URL url = JSFUtils.createHttpUrl(server, contextRoot, "implicit_objects.xhtml?message=Hello World");

            HtmlPage testELResolutionImplicitObjectsPage = (HtmlPage) webClient.getPage(url);

            // Log the page for debugging if necessary in the future.
            Log.info(c, name.getMethodName(), testELResolutionImplicitObjectsPage.asText());
            Log.info(c, name.getMethodName(), testELResolutionImplicitObjectsPage.asXml());

            // Verify that the page contains the expected messages.
            assertTrue(testELResolutionImplicitObjectsPage.asText().contains("JSF 2.3 EL resolution of implicit objects using CDI"));
            assertTrue(testELResolutionImplicitObjectsPage.asText().contains("Bean: com.ibm.ws.jsf23.fat.elimplicit.cdi.beans.ELImplicitObjectBean"));

            // MYFACES-4559
            if (isEE10) {
                assertTrue(testELResolutionImplicitObjectsPage.asText().contains("Application name: JSF23ELImplicitObjectsViaCDI"));
            } else {
                assertTrue(testELResolutionImplicitObjectsPage.asText().contains("Application project stage: Production"));
            }

            assertTrue(testELResolutionImplicitObjectsPage.asText().contains("ApplicationScope application name: ELImplicitObjectsViaCDI"));
            assertTrue(testELResolutionImplicitObjectsPage.asText().contains("Component getStyle: font-weight:bold"));
            assertTrue(testELResolutionImplicitObjectsPage.asText().contains("CompositeComponent label: Hello World"));
            assertTrue(testELResolutionImplicitObjectsPage.asText().contains("FacesContext project stage: Production"));
            assertTrue(testELResolutionImplicitObjectsPage.asText().contains("Flash isRedirect: false"));
            //Updated to check for User Agent
            assertTrue(testELResolutionImplicitObjectsPage.asText().contains("Header: Mozilla"));
            assertTrue(testELResolutionImplicitObjectsPage.asText().contains("HeaderValues: Mozilla"));

            assertTrue(testELResolutionImplicitObjectsPage.asText().contains("InitParam: ELImplicitObjectsViaCDI"));
            assertTrue(testELResolutionImplicitObjectsPage.asText().contains("Param: Hello World"));
            assertTrue(testELResolutionImplicitObjectsPage.asText().contains("ParamValues: Hello World"));
            assertTrue(testELResolutionImplicitObjectsPage.asText().contains("View viewId: /implicit_objects.xhtml "));
            assertTrue(testELResolutionImplicitObjectsPage.asText().contains("ViewScope isEmpty: true"));
            // See https://issues.apache.org/jira/projects/MYFACES/issues/MYFACES-4432
            // Note: The request & session objects are not resolved by CDI, but via ImplicitObjectResolver
            assertTrue(testELResolutionImplicitObjectsPage.asText().contains("Request contextPath: /ELImplicitObjectsViaCDI"));
            assertTrue(testELResolutionImplicitObjectsPage.asText().contains("HttpSession isNew: true"));
            // Additional Requirement (See section 5.6.3)
            assertTrue(testELResolutionImplicitObjectsPage.asText().contains("ExternalContext getApplicationContextPath: /ELImplicitObjectsViaCDI"));

        }
    }

    /**
     * Test the EL Resolution of implicit object #{flowScope}
     *
     * @throws Exception
     */
    @Test
    public void testELResolutionOfFlowScope() throws Exception {
        try (WebClient webClient = new WebClient()) {

            // Construct the URL for the test
            String contextRoot = "ELImplicitObjectsViaCDI";
            URL url = JSFUtils.createHttpUrl(server, contextRoot, "flow_index.xhtml");

            HtmlPage testELResolutionOfFlowScopePage = (HtmlPage) webClient.getPage(url);

            // Verify that the page contains the expected messages.
            assertTrue(testELResolutionOfFlowScopePage.asText().contains("This flow tests a basic configuration with a @FlowScoped bean. The flow is defined via simple-flow.xml"));

            // Get the form that we are dealing with
            HtmlForm form = testELResolutionOfFlowScopePage.getFormByName("form1");

            // Get the submit button to click
            HtmlSubmitInput submitButton = form.getInputByName("form1:simpleBean");

            // Now click the button and get the resulting page.
            HtmlPage resultPage = submitButton.click();

            // Log the page for debugging if necessary in the future.
            Log.info(c, name.getMethodName(), resultPage.asText());
            Log.info(c, name.getMethodName(), resultPage.asXml());

            // Verify that the page contains the expected messages.
            assertTrue(resultPage.asText().contains("FlowScope isEmpty: true"));
            assertTrue(resultPage.asText().contains("Flow map isEmpty: true"));
        }
    }

    /**
     * Test that a FacesConverter, a FacesValidator and a FacesBehavior can be injected in a Managed Bean
     *
     * @throws Exception
     */
    @Test
    public void testConverterValidatorBehaviorObjectInjection() throws Exception {
        try (WebClient webClient = new WebClient()) {

            // Construct the URL for the test
            String contextRoot = "ConverterValidatorBehaviorInjectionTarget";
            URL url = JSFUtils.createHttpUrl(server, contextRoot, "JSFArtifactsInjection.xhtml");

            HtmlPage page = (HtmlPage) webClient.getPage(url);

            // Log the page for debugging if necessary in the future.
            Log.info(c, name.getMethodName(), page.asText());
            Log.info(c, name.getMethodName(), page.asXml());

            // Verify that the page contains the expected response.
            // TestConverter, TestValidator and TestBehavior objects should have been injected
            assertTrue(page.asText().contains("JSF 2.3 support injection of JSF Managed Objects: FacesConverter, FacesValidator, FacesBehavior"));
            assertTrue(page.asText().contains("com.ibm.ws.jsf23.fat.converter.validator.behavior.injection.beans.TestConverter"));

            assertTrue(page.asText().contains("com.ibm.ws.jsf23.fat.converter.validator.behavior.injection.beans.TestValidator"));
            assertTrue(page.asText().contains("com.ibm.ws.jsf23.fat.converter.validator.behavior.injection.beans.TestBehavior"));
        }
    }

    /**
     * Test the Java Time Support by using f:convertDateTime element.
     *
     * Test the old types "date", "time" and "both" along with the new Java 8 types "localDate",
     * "localTime", "localDateTime", "offsetTime". "offsetDateTime" and "zonedDateTime".
     *
     * Verify that the response contains the expected messages with the correct date/time pattern.
     *
     * @throws Exception
     */
    @Test
    public void testJavaTimeSupport() throws Exception {
        try (WebClient webClient = new WebClient()) {

            // Construct the URL for the test
            String contextRoot = "ConvertDateTime";
            URL url = JSFUtils.createHttpUrl(server, contextRoot, "");

            HtmlPage page = (HtmlPage) webClient.getPage(url);

            String pageText = page.asText();

            // Log the page for debugging if necessary in the future.
            Log.info(c, name.getMethodName(), pageText);
            Log.info(c, name.getMethodName(), page.asXml());

            // Verify that the page contains the expected messages.
            assertTrue(pageText.contains("JSF 2.3 Java Time Support - f:convertDateTime"));

            // Get the form that we are dealing with
            HtmlForm form = page.getFormByName("form1");

            // Get the input text
            HtmlTextInput inputText = (HtmlTextInput) form.getInputByName("form1:input1");

            // Verify that the response contains the expected messages with the correct date/time
            assertTrue("Unexpected value in the input field",
                       inputText.getValueAttribute().equals("2017-06-01 10:30:45"));
            assertTrue(pageText.contains("Type date, dateStyle short: 6/1/17"));
            assertTrue(pageText.contains("Type date, dateStyle medium: Jun 1, 2017"));
            assertTrue(pageText.contains("Type date, dateStyle long: June 1, 2017"));
            assertTrue(pageText.contains("Type date, dateStyle full: Thursday, June 1, 2017"));
            assertTrue(pageText.contains("Type date, pattern MM-dd-yyyy: 06-01-2017"));

            assertTrue(pageText.contains("Type localDate, dateStyle short: 6/1/17"));
            assertTrue(pageText.contains("Type localDate, dateStyle medium: Jun 1, 2017"));
            assertTrue(pageText.contains("Type localDate, dateStyle long: June 1, 2017"));
            assertTrue(pageText.contains("Type localDate, dateStyle full: Thursday, June 1, 2017"));
            assertTrue(pageText.contains("Type localDate, pattern MM-dd-yyyy: 06-01-2017"));
            assertTrue(pageText.contains("Type localTime, pattern HH:mm:ss: 10:35:45"));

            assertTrue(pageText.contains("Type localDateTime, pattern MM-dd-yyyy HH:mm:ss: 06-01-2017 10:30:45"));
            assertTrue(pageText.contains("Type offsetTime: 10:30:45.5-07:00"));
            assertTrue(pageText.contains("Type offsetTime, pattern HH:mm:ss:SSS ZZZZ: 10:30:45:500 GMT-07:00"));
            assertTrue(pageText.contains("Type offsetDateTime: 2017-06-01T10:30:45.5-07:00"));
            assertTrue(pageText.contains("Type offsetDateTime, pattern MM-dd-yyyy HH:mm:ss:SSS ZZZZ: 06-01-2017 10:30:45:500 GMT-07:00"));
            assertTrue(pageText.contains("Type zonedDateTime: 2017-06-01T10:30:45.5-07:00[America/Los_Angeles] "));
            assertTrue(pageText.contains("Type zonedDateTime, pattern MM-dd-yyyy HH:mm:ss ZZZZ z: 06-01-2017 10:30:45:500 GMT-07:00 PDT"));

            // JAVA 20 Checks -- https://github.com/OpenLiberty/open-liberty/issues/24009
            char space = '\u0020';
            if (componenttest.topology.impl.JavaInfo.JAVA_VERSION >= 20) {
                space = '\u202F';
            }

            assertTrue(pageText.contains("Type time, timeStyle short: 10:30" + space + "AM"));
            assertTrue(pageText.contains("Type time, timeStyle medium: 10:30:45" + space + "AM"));
            assertTrue(pageText.contains("Type time, timeStyle long: 10:30:45" + space + "AM GMT"));
            assertTrue(pageText.contains("Type time, timeStyle full: 10:30:45" + space + "AM GMT") ||
                       pageText.contains("Type time, timeStyle full: 10:30:45" + space + "AM Greenwich Mean Time"));
            assertTrue(pageText.contains("Type both, dateStyle full, timeStyle medium: Thursday, June 1, 2017 10:30:45" + space + "AM") ||
                       pageText.contains("Type both, dateStyle full, timeStyle medium: Thursday, June 1, 2017, 10:30:45" + space + "AM"));

            assertTrue(pageText.contains("Type localTime, timeStyle short: 10:35" + space + "AM"));
            assertTrue(pageText.contains("Type localTime, timeStyle medium: 10:35:45" + space + "AM"));

            assertTrue(pageText.contains("Type localDateTime, dateStyle short, timeStyle short: 6/1/17 10:30" + space + "AM") ||
                       pageText.contains("Type localDateTime, dateStyle short, timeStyle short: 6/1/17, 10:30" + space + "AM"));
            assertTrue(pageText.contains("Type localDateTime, dateStyle medium, timeStyle medium: Jun 1, 2017 10:30:45" + space + "AM") ||
                       pageText.contains("Type localDateTime, dateStyle medium, timeStyle medium: Jun 1, 2017, 10:30:45" + space + "AM"));

        }
    }

    /**
     * Test the CDI-JSF integration.
     *
     * In this test we want make sure that a custom ViewHandler
     * and a custom Application can be used in an app.
     *
     * Also, make sure that the IBMViewHandler is used
     * when CDI is enabled.
     *
     * @throws Exception
     */
    @Test
    @Mode(TestMode.FULL)
    public void testCDIIntegration() throws Exception {
        try (WebClient webClient = new WebClient()) {

            // Construct the URL for the test
            String contextRoot = "CDIIntegrationTest";
            URL url = JSFUtils.createHttpUrl(server, contextRoot, "index.xhtml");

            HtmlPage page = (HtmlPage) webClient.getPage(url);

            String responseText = page.asText();

            // Log the page for debugging if necessary in the future.
            Log.info(c, name.getMethodName(), responseText);
            Log.info(c, name.getMethodName(), page.asXml());

            assertTrue("Page does not contain expected response.", responseText.contains("CDI Integration Test"));

            assertTrue("The Custom ApplicationFactory was not invoked.",
                       !server.findStringsInTrace("CustomApplicationFactory was invoked!").isEmpty());

            assertTrue("The Custom Application was not invoked.",
                       !server.findStringsInTrace("CustomApplication was invoked!").isEmpty());

            assertTrue("The Custom ViewHandler was not invoked.",
                       !server.findStringsInTrace("CustomViewHandler was invoked!").isEmpty());

            assertTrue("The IBMViewHandler was not used.",
                       !server.findStringsInTrace("set ViewHandler =.*IBMViewHandler").isEmpty());
        }
    }
}
