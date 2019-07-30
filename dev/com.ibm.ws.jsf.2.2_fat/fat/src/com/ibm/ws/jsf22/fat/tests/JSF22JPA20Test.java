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

import static org.junit.Assert.assertNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

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
 * This test class will use the jsf22jpa20Server to ensure that the two features can be configured together.
 *
 * The JPA 2.0 feature must be able to be used with all EE7 features such as JSF 2.2.
 */
@Mode(TestMode.FULL)
@RunUnlessFeatureBeingTested("jsf-2.3")
@MinimumJavaLevel(javaLevel = 7)
@RunWith(FATRunner.class)
public class JSF22JPA20Test {
    @Rule
    public TestName name = new TestName();
    
        protected static final Class<?> c = JSF22JPA20Test.class;
    
        @Server("jsf22jpa20Server")
        public static LibertyServer jsf22jpa20Server;
    
        @BeforeClass
        public static void setup() throws Exception {
            jsf22jpa20Server.startServer(JSF22JPA20Test.class.getSimpleName() + ".log");
        }
    

    @AfterClass
    public static void tearDown() throws Exception {
            // Stop the server
            if (jsf22jpa20Server != null && jsf22jpa20Server.isStarted()) {
                    //CWWKF0001E added for the meantime. jpa-2.0 is not found in OL. 
                    jsf22jpa20Server.stopServer("CWWKF0001E");
            }
    }

    /**
     * Test to ensure that the jsf-2.2 feature and jpa-2.0 feature can be used together.
     *
     * @throws Exception
     */
    @Test
    public void testJSF22AndJPA20Features() throws Exception {
        String msgToSearchFor = "CWWKE0702E";

        // Check the logs to see if the jsf module could be resolved.  If the string is found that means there
        // was an issue resolving jsf-2.2 when the jpa-2.0 feature is included.
        // The default timeout is 4 minutes, setting timeout to 10 seconds so this test case does not always take 4 minutes
        // to complete on a good server. We expect this test to pass except for in rare cases where someone breaks the
        // compatibility between the two features.  The test seems to fail very quickly in a failing scenario.
        assertNull("The following message was found in the logs and should not have been: " + msgToSearchFor,
                jsf22jpa20Server.waitForStringInLog(msgToSearchFor, 10 * 1000));
    }
}