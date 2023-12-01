/*******************************************************************************
 * Copyright (c) 2012, 2023 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package com.ibm.ws.jsp23.fat;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ibm.ws.fat.util.FatLogHandler;
import com.ibm.ws.jsp23.fat.tests.JSPTests;

import componenttest.rules.repeater.EmptyAction;
import componenttest.rules.repeater.FeatureReplacementAction;
import componenttest.rules.repeater.RepeatTests;
import componenttest.topology.impl.JavaInfo;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.impl.LibertyServerFactory;

/**
 * JSP 2.3 Tests
 *
 * The tests for both features should be included in this test component.
 */
@RunWith(Suite.class)
@SuiteClasses({
                JSPTests.class,
                // JSPExceptionTests.class,
                // JSPSkipMetaInfTests.class,
                // JSPJava8Test.class,
                // JSPCdiTest.class,
                // JSP23JSP22ServerTest.class,
                // JSPPrepareJSPThreadCountNonDefaultValueTests.class,
                // JSPPrepareJSPThreadCountDefaultValueTests.class,
                // JSTLTests.class
})
public class FATSuite {

    private static final LibertyServer server = LibertyServerFactory.getLibertyServer("jstlServer");

    /**
     * @see {@link FatLogHandler#generateHelpFile()}
     */
    @BeforeClass
    public static void generateHelpFile() throws Exception {
        FatLogHandler.generateHelpFile();

                // Copy user feature bundle to Liberty
                server.copyFileToLibertyInstallRoot("usr/extension/lib/", "bundles/io.openliberty.test.tld.jar");
        
                // Add user feature mf file to Liberty
                server.copyFileToLibertyInstallRoot("usr/extension/lib/features/", "features/globaltld-1.0.mf");

                // System.out.println("SLEEP");
                // Thread.sleep(1000000);

    

    }

    /**
     * Run the tests again with the cdi-2.0 feature. Tests should be skipped where appropriate
     * using @SkipForRepeat("CDI-2.0").
     */
    @ClassRule
    public static RepeatTests repeat;

    static {
        // if (JavaInfo.JAVA_VERSION >= 11) {
        //     repeat = RepeatTests
        //                     .with(new EmptyAction().fullFATOnly())
        //                     .andWith(new FeatureReplacementAction("cdi-1.2", "cdi-2.0")
        //                                     .withID("CDI-2.0")
        //                                     .forceAddFeatures(false)
        //                                     .fullFATOnly())
        //                     .andWith(FeatureReplacementAction.EE9_FEATURES().fullFATOnly())
        //                     .andWith(FeatureReplacementAction.EE10_FEATURES());
        // } else {
        //     repeat = RepeatTests
        //                     .with(new EmptyAction().fullFATOnly())
        //                     .andWith(new FeatureReplacementAction("cdi-1.2", "cdi-2.0")
        //                                     .withID("CDI-2.0")
        //                                     .forceAddFeatures(false)
        //                                     .fullFATOnly())
        //                     .andWith(FeatureReplacementAction.EE9_FEATURES());
        // }
    }
}
