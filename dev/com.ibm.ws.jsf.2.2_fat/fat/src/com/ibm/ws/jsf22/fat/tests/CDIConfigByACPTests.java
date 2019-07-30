/*
 * Copyright (c) 2015, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 */
package com.ibm.ws.jsf22.fat.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import com.ibm.ws.fat.jsf.CDITestBase;

import componenttest.annotation.Server;
import componenttest.topology.impl.LibertyServer;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.annotation.MinimumJavaLevel;
import componenttest.custom.junit.runner.Mode;
import componenttest.custom.junit.runner.Mode.TestMode;
import componenttest.custom.junit.runner.RunUnlessFeatureBeingTested;

import org.jboss.shrinkwrap.api.spec.WebArchive;
import com.ibm.websphere.simplicity.ShrinkHelper;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

/**
 * This is one of four CDI test applications, with configuration loaded in the following manner:
 * CDIConfigByACP - Application Configuration Populator loading of the class files
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
public class CDIConfigByACPTests extends CDITestBase {
    @Rule
    public TestName name = new TestName();
    
        String contextRoot = "TestJSFEL";
    
        protected static final Class<?> c = CDIConfigByACPTests.class;
    
        @Server("jsfCDIConfigByACPServer")
        public static LibertyServer jsfCDIConfigByACPServer;
    
        @BeforeClass
        public static void setup() throws Exception {

        //    JavaArchive jar = ShrinkHelper.buildJavaArchive("CDIConfigByACP.jar", "com.ibm.ws.jsf22.fat.cdiconfigbyacp.jar");

            JavaArchive jar  = ShrinkHelper.buildJavaArchive("CDIConfigByACP.jar", "com.ibm.ws.jsf22.fat.cdiconfigbyacp.jar",
                                                                                    "com.ibm.ws.jsf22.fat.cdicommon.*");

            WebArchive war = ShrinkHelper.buildDefaultApp("CDIConfigByACP.war", "com.ibm.ws.jsf22.fat.cdiconfigbyacp");

            war.addAsLibraries(jar);

            ShrinkHelper.exportDropinAppToServer(jsfCDIConfigByACPServer, war);

            jsfCDIConfigByACPServer.startServer(JSF22AppConfigPopTests.class.getSimpleName() + ".log");

        }
    
    @AfterClass
    public static void tearDown() throws Exception {
            // Stop the server
            if (jsfCDIConfigByACPServer != null && jsfCDIConfigByACPServer.isStarted()) {
                jsfCDIConfigByACPServer.stopServer();
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
    public void testActionListenerInjection_CDIConfigByACP() throws Exception {
        testActionListenerInjectionByApp("CDIConfigByACP", jsfCDIConfigByACPServer);
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
    public void testNavigationHandlerInjection_CDIConfigByACP() throws Exception {
        testNavigationHandlerInjectionByApp("CDIConfigByACP", jsfCDIConfigByACPServer);
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
    public void testELResolverInjection_CDIConfigByACP() throws Exception {
        testELResolverInjectionByApp("CDIConfigByACP", jsfCDIConfigByACPServer);
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
    public void testCustomResourceHandlerInjections_CDIConfigByACP() throws Exception {
        testCustomResourceHandlerInjectionsByApp("CDIConfigByACP", jsfCDIConfigByACPServer);

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
    public void testCustomStateManagerInjections_CDIConfigByACP() throws Exception {
        testCustomStateManagerInjectionsByApp("CDIConfigByACP", jsfCDIConfigByACPServer);
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
    public void testFactoryAndOtherScopeInjections_CDIConfigByACP() throws Exception {
        testFactoryAndOtherAppScopedInjectionsByApp("CDIConfigByACP", jsfCDIConfigByACPServer);
    }
}
