/*
 * Copyright (c) 2012, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 */
package com.ibm.ws.fat.jsf;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ibm.ws.fat.jsf.tests.CDIConfigByACPTests;
import com.ibm.ws.fat.jsf.tests.CDIFacesInMetaInfTests;
import com.ibm.ws.fat.jsf.tests.CDIFacesInWebXMLTests;
import com.ibm.ws.fat.jsf.tests.CDIFlowsTests;
import com.ibm.ws.fat.jsf.tests.CDITests;
// import com.ibm.ws.fat.jsf.tests.JSF22AparTests;
import com.ibm.ws.fat.jsf.tests.JSF22AppConfigPopTests;
import com.ibm.ws.fat.jsf.tests.JSF22BeanValidationTests;
import com.ibm.ws.fat.jsf.tests.JSF22ClientWindowTests;
import com.ibm.ws.fat.jsf.tests.JSF22ComponentRendererTests;
import com.ibm.ws.fat.jsf.tests.JSF22ComponentTesterTests;
import com.ibm.ws.fat.jsf.tests.JSF22FlashEventsTests;
import com.ibm.ws.fat.jsf.tests.JSF22FlowsTests;
import com.ibm.ws.fat.jsf.tests.JSF22IncludeTest;
import com.ibm.ws.fat.jsf.tests.JSF22InputFileTests;
import com.ibm.ws.fat.jsf.tests.JSF22JPA20Test;
import com.ibm.ws.fat.jsf.tests.JSF22JSF20SingletonFeatureTest;
import com.ibm.ws.fat.jsf.tests.JSF22LocalizationTesterTests;
import com.ibm.ws.fat.jsf.tests.JSF22MiscLifecycleTests;
import com.ibm.ws.fat.jsf.tests.JSF22MiscellaneousTests;
import com.ibm.ws.fat.jsf.tests.JSF22ResetValuesAndAjaxDelayTests;
import com.ibm.ws.fat.jsf.tests.JSF22ResourceLibraryContractHtmlUnit;
import com.ibm.ws.fat.jsf.tests.JSF22StatelessViewTests;
import com.ibm.ws.fat.jsf.tests.JSF22ViewActionAndPhaseIdTests;
import com.ibm.ws.fat.jsf.tests.JSF22ViewPoolingTests;
import com.ibm.ws.fat.jsf.tests.JSFCompELTests;
import com.ibm.ws.fat.jsf.tests.JSFDummyTest;
import com.ibm.ws.fat.jsf.tests.JSFHtml5Tests;
import com.ibm.ws.fat.jsf.tests.JSFHtmlUnit;//
import com.ibm.ws.fat.jsf.tests.JSFSimpleHtmlUnit;
import com.ibm.ws.fat.jsf.tests.JSFServerTest;
import com.ibm.ws.fat.util.FatLogHandler;

/**
 * JSF 2.2 Tests
 * 
 * The tests for both features should be included in this test component.
 * 
 * Make sure to add any new test classes to the @SuiteClasses
 * annotation.
 * 
 * Make sure to distinguish full mode tests using
 * <code>@Mode(TestMode.FULL)</code>. Tests default to
 * use lite mode (<code>@Mode(TestMode.LITE)</code>).
 * 
 * By default only lite mode tests are run. To also run
 * full mode tests a property must be specified to ant:
 * 
 * Select the target build file (usually "build-test.xml").
 * Right click and chose "Run As>Ant Build…". Add
 * "fat.test.mode=full" to the properties tab, then launch the
 * build.
 * 
 * Alternatively, for a command line launch, add "-Dfat.test.mode=full".
 * 
 * For additional information see:
 * 
 * http://was.pok.ibm.com/xwiki/bin/view/Liberty/Test-FAT
 */
@RunWith(Suite.class)
@SuiteClasses({
                // JSF22ResourceLibraryContractHtmlUnit.class,
                // JSFCompELTests.class,
                // JSF22ViewActionAndPhaseIdTests.class,
                // JSF22ResetValuesAndAjaxDelayTests.class,
                // JSF22IncludeTest.class,
                // JSFDummyTest.class,
                // JSFServerTest.class,
                // JSFHtmlUnit.class,
                // JSFSimpleHtmlUnit.class,
                // JSF22FlowsTests.class,
                // JSFHtml5Tests.class,
                // JSF22StatelessViewTests.class,
                // JSF22ViewPoolingTests.class,
                // JSF22FlashEventsTests.class,
                // JSF22MiscLifecycleTests.class,
                // CDITests.class,
                // JSF22InputFileTests.class,
                // JSF22ComponentTesterTests.class,
                // JSF22ClientWindowTests.class,
                // JSF22ComponentRendererTests.class,
                // CDIFlowsTests.class,
                // JSF22MiscellaneousTests.class,
                // JSF22AppConfigPopTests.class,
                // CDIConfigByACPTests.class
                // CDIFacesInMetaInfTests.class,
                // CDIFacesInWebXMLTests.class,
                // JSF22JPA20Test.class, // jpa-2.0 is not found..
                // JSF22BeanValidationTests.class,

                JSF22LocalizationTesterTests.class,
                //JSF22JSF20SingletonFeatureTest.class

                // JSF22AparTests.class
})
public class FATSuite {

    /**
     * @see {@link FatLogHandler#generateHelpFile()}
     */
    @BeforeClass
    public static void generateHelpFile() {
        FatLogHandler.generateHelpFile();
    }
}
