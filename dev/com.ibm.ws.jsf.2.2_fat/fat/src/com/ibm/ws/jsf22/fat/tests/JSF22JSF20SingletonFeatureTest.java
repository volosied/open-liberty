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

import static org.junit.Assert.assertNotNull;

import java.util.logging.Logger;
import com.ibm.websphere.simplicity.log.Log;

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

/**
 * Tests to execute on the jsf22jsf20Server.
 * 
 * This test is to ensure that both the jsf-2.0 and jsf-2.2 features are singleton features
 * and only one can be loaded at a time. If both features are specified in a server.xml
 * the server should log an error.
 *
 * The RunUnlessFeatureBeingTested was added because we do not want this test to run in the com.ibm.ws.jsf.2.2_fat_jsf.2.3 toleration bucket.
 * It was failing after trying to update the test class file to use jsf-2.3 instead of jsf-2.2.  The test is duplicated (with the updated feature)
 * in the com.ibm.ws.jsf.2.3_fat bucket.
 */

@Mode(TestMode.FULL)
@RunWith(FATRunner.class)
@RunUnlessFeatureBeingTested("jsf-2.3")
@MinimumJavaLevel(javaLevel = 7)
public class JSF22JSF20SingletonFeatureTest {
    @Rule
    public TestName name = new TestName();
    
        String contextRoot = "TestJSFEL";
    
        protected static final Class<?> c = JSF22JSF20SingletonFeatureTest.class;
    
        @Server("jsf22jsf20Server")
        public static LibertyServer jsf22jsf20Server;
    
        @BeforeClass
        public static void setup() throws Exception {
            jsf22jsf20Server.startServer(JSF22JSF20SingletonFeatureTest.class.getSimpleName() + ".log");
        }
    

    @AfterClass
    public static void tearDown() throws Exception {
            // Stop the server
            if (jsf22jsf20Server != null && jsf22jsf20Server.isStarted()) {
                jsf22jsf20Server.stopServer("CWWKF0033E");
            }
    }

    /**
     * CWWKF0033E: The singleton features jsf-2.0 and jsf-2.2 cannot be loaded at the same time.
     * The configured features jsf-2.0 and jsf-2.2 include one or more features that cause
     * the conflict.
     * 
     * The above error must be seen if both the jsf-2.0 and the jsf-2.2 features are enabled at the
     * same time. This test will ensure the error exists in this error case.
     * 
     * @throws Exception
     */
    @Test
    public void testEnsureJSF20FeatureAndJSF22FeatureAreSingletonFeatures() throws Exception {
        // Make sure the test framework knows that CWWKF0033E is expected 
        //jsf22jsf20Server.setExpectedErrors("CWWKF0033E:.*");

        String msgToSearchFor = "CWWKF0033E: The singleton features jsf-2.0 and jsf-2.2 cannot be loaded at the same time.  " +
                                "The configured features jsf-2.0 and jsf-2.2 include one or more features that cause the conflict.";

        // Search for the message in the logs to ensure that the correct exception was logged
        String areBothJSFFeaturesSingletons = jsf22jsf20Server.waitForStringInLog(msgToSearchFor);

        assertNotNull("The following message was not found in the log: " + msgToSearchFor, areBothJSFFeaturesSingletons);
    }

}
