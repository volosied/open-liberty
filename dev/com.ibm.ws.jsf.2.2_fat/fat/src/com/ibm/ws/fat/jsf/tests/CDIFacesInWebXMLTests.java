/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 */
package com.ibm.ws.fat.jsf.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import com.ibm.ws.fat.jsf.CDITestBase;

import componenttest.annotation.MinimumJavaLevel;
import componenttest.custom.junit.runner.Mode;
import componenttest.custom.junit.runner.Mode.TestMode;
import componenttest.custom.junit.runner.RunUnlessFeatureBeingTested;

import componenttest.annotation.Server;
import componenttest.topology.impl.LibertyServer;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.annotation.MinimumJavaLevel;
import componenttest.custom.junit.runner.Mode;
import componenttest.custom.junit.runner.Mode.TestMode;

import org.jboss.shrinkwrap.api.spec.WebArchive;
import com.ibm.websphere.simplicity.ShrinkHelper;

/**
 * This is one of four CDI test applications, with configuration loaded in the following manner:
 * CDIFacesInWebXML - web.xml param that points to two faces-config files.
 *
 * We're extending CDITestBase, which has common test code.
 *
 * NOTE: These tests should not run with jsf-2.3 feature because constructor injection is not supported.
 * As a result, these tests were modified to run in the JSF 2.3 FAT bucket without constructor injection.
 */
@Mode(TestMode.FULL)
@RunUnlessFeatureBeingTested("jsf-2.3")
@MinimumJavaLevel(javaLevel = 7)
@RunWith(FATRunner.class)
public class CDIFacesInWebXMLTests extends CDITestBase {
    @Rule
    public TestName name = new TestName();
    
        String contextRoot = "TestJSFEL";
    
        protected static final Class<?> c = CDIFacesInWebXMLTests.class;
    
        @Server("jsfCDIFacesInWebXMLServer")
        public static LibertyServer jsfCDIFacesInWebXMLServer;
    
        @BeforeClass
        public static void setup() throws Exception {

            WebArchive war  = ShrinkHelper.buildDefaultApp("CDIFacesInWebXML.war", "com.ibm.ws.jsf22.fat.cdicommon.*");

            ShrinkHelper.exportDropinAppToServer(jsfCDIFacesInWebXMLServer, war);

            jsfCDIFacesInWebXMLServer.startServer(CDIFacesInWebXMLTests.class.getSimpleName() + ".log");

        }
    
    @AfterClass
    public static void tearDown() throws Exception {
            // Stop the server
            if (jsfCDIFacesInWebXMLServer != null && jsfCDIFacesInWebXMLServer.isStarted()) {
                jsfCDIFacesInWebXMLServer.stopServer();
            }
        }

    /**
     * Test to ensure that CDI 1.2 injection works for an custom action listener
     * Field, Method and Constructor Injection, and Interceptors. Also
     * tested are use of request and session scope and use of qualifiers.
     *
     * We have a single source location of test-application-common for all application classes. build-test.xml contains the logic that
     * compiles this source directory and copies half the managed classes into the app's jar file and half into the app's war files.
     *
     * @throws Exception. Content of the response should show if a specific injection failed.
     *
     */
    @Test
    public void testActionListenerInjection_CDIFacesInWebXML() throws Exception {
        testActionListenerInjectionByApp("CDIFacesInWebXML", jsfCDIFacesInWebXMLServer);
    }

    /**
     * Test to ensure that CDI 1.2 injection works for a custom Navigation Handler
     * Field, Method and Constructor Injection, and Interceptors. Also
     * tested are use of request and session scope and use of qualifiers.
     *
     * We have a single source location of test-application-common for all application classes. build-test.xml contains the logic that
     * compiles this source directory and copies half the managed classes into the app's jar file and half into the app's war files.
     *
     * @throws Exception. Content of the response should show if a specific injection failed.
     *
     */
    @Test
    public void testNavigationHandlerInjection_CDIFacesInWebXML() throws Exception {
        testNavigationHandlerInjectionByApp("CDIFacesInWebXML", jsfCDIFacesInWebXMLServer);
    }

    /**
     * Test to ensure that CDI 1.2 injection works for a custom EL Resolver
     * Field, Method and Constructor Injection, and Interceptors. Also
     * tested are use of request scope and use of qualifiers.
     *
     * We have a single source location of test-application-common for all application classes. build-test.xml contains the logic that
     * compiles this source directory and copies half the managed classes into the app's jar file and half into the app's war files.
     *
     * @throws Exception. Content of the response should show if a specific injection failed.
     *
     */
    @Test
    public void testELResolverInjection_CDIFacesInWebXML() throws Exception {
        testELResolverInjectionByApp("CDIFacesInWebXML", jsfCDIFacesInWebXMLServer);
    }

    /**
     * Test method and field injection for Custom resource handler. No intercepter or constructor injection on this.
     *
     * Would like to do something more than look for message in logs, a future improvement.
     *
     * We have a single source in test-application-common for all application classes. build-test.xml contains the logic that
     * compiles this source directory and splits the copying managec classes the class files into app's jar/war files.
     *
     * @throws Exception
     */
    @Test
    public void testCustomResourceHandlerInjections_CDIFacesInWebXML() throws Exception {
        testCustomResourceHandlerInjectionsByApp("CDIFacesInWebXML", jsfCDIFacesInWebXMLServer);

    }

    /**
     * Test method and field injection on custom state manager. No intercepter or constructor tests on this.
     *
     * Would like to do something more than look for message in logs, a future improvement.
     *
     * We have a single source in test-application-common for all application classes. build-test.xml contains the logic that
     * compiles this source directory and splits the copying managec classes the class files into app's jar/war files.
     *
     * @throws Exception
     */
    @Test
    public void testCustomStateManagerInjections_CDIFacesInWebXML() throws Exception {
        testCustomStateManagerInjectionsByApp("CDIFacesInWebXML", jsfCDIFacesInWebXMLServer);
    }

    /**
     * Test that hits most of the managed factory classes, and system-event listener, and phase-listener. See faces-config.xml for details.
     * Most factories use delegate constructor method, so they are limited to tested basic field and method injection. Tests also use app scope as
     * request/session are not available to these managed classes that I can tell.
     *
     * We have a single source location of test-application-common for all application classes. build-test.xml contains the logic that
     * compiles this source directory and copies half the managed classes into the app's jar file and half into the app's war files.
     *
     * @throws Exception
     */
    @Test
    public void testFactoryAndOtherScopeInjections_CDIFacesInWebXML() throws Exception {
        testFactoryAndOtherAppScopedInjectionsByApp("CDIFacesInWebXML", jsfCDIFacesInWebXMLServer);
    }
}
