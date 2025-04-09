/*******************************************************************************
 * Copyright (c) 2021, 2023 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package io.openliberty.checkpoint.fat;

import org.junit.BeforeClass;
import org.junit.ClassRule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ibm.ws.fat.util.FatLogHandler;
import componenttest.custom.junit.runner.FATRunner;

import componenttest.custom.junit.runner.AlwaysPassesTest;
import componenttest.topology.impl.LibertyServer;

import componenttest.rules.repeater.EmptyAction;
import componenttest.rules.repeater.FeatureReplacementAction;
import componenttest.rules.repeater.RepeatTests;

@RunWith(Suite.class)
@SuiteClasses({
                AlwaysPassesTest.class,
                JSF22ClientWindowTests.class,
                JSF23CDIGeneralTests.class

})

public class FATSuite {
    @ClassRule
    public static RepeatTests repeat;

    private static final boolean isWindows = System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("win");

    static {
            // If we are running with a Java version less than 11, have EE9 be the lite mode test to run.
            repeat = RepeatTests.with(new EmptyAction().fullFATOnly())
                            .andWith(FeatureReplacementAction.EE9_FEATURES().conditionalFullFATOnly(FeatureReplacementAction.GREATER_THAN_OR_EQUAL_JAVA_11))
                            .andWith(FeatureReplacementAction.EE10_FEATURES().conditionalFullFATOnly(FeatureReplacementAction.GREATER_THAN_OR_EQUAL_JAVA_17))
                            .andWith(FeatureReplacementAction.EE11_FEATURES());
    }

    /**
     * @see {@link FatLogHandler#generateHelpFile()}
     */
    @BeforeClass
    public static void generateHelpFile() {
        FatLogHandler.generateHelpFile();
    }
}
