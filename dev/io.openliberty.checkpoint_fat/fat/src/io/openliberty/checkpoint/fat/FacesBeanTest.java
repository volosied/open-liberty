/*******************************************************************************
 * Copyright (c) 2022 IBM Corporation and others.
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

import static io.openliberty.checkpoint.fat.FATSuite.getTestMethod;
import static io.openliberty.checkpoint.fat.FATSuite.getTestMethodNameOnly;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.net.HttpURLConnection;

import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import com.ibm.websphere.simplicity.log.Log;

import com.ibm.websphere.simplicity.ShrinkHelper;
import com.ibm.websphere.simplicity.ShrinkHelper.DeployOptions;

import componenttest.annotation.Server;
import componenttest.annotation.CheckpointTest;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.custom.junit.runner.Mode.TestMode;
import componenttest.rules.repeater.MicroProfileActions;
import componenttest.rules.repeater.RepeatTests;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.utils.FATServletClient;
import componenttest.topology.utils.HttpUtils;
import io.openliberty.checkpoint.spi.CheckpointPhase;

@RunWith(FATRunner.class)
@CheckpointTest
public class FacesBeanTest extends FATServletClient {

    public static final String FACES_BEAN_SERVER_NAME = "checkpointFaces-beanServer";
    public static final String FACES_APP_NAME = "facesApp";

    @Rule
    public TestName testName = new TestName();

    protected static final Class<?> c = FacesBeanTest.class;

    // TODO additional work needed to run this test on EE10/MP6
    @ClassRule
    public static RepeatTests repeatTests = MicroProfileActions.repeat(FACES_BEAN_SERVER_NAME, TestMode.LITE,
                                                                       MicroProfileActions.MP41, MicroProfileActions.MP50 /* , MicroProfileActions.MP60 */);

    @Server(FACES_BEAN_SERVER_NAME)
    public static LibertyServer facesBeanServer;

    @Before
    public void setUp() throws Exception {
        WebArchive facesBeanApp = ShrinkHelper.buildDefaultApp(FACES_APP_NAME, "facesapp");
        facesBeanApp = (WebArchive) ShrinkHelper.addDirectory(facesBeanApp, "test-applications/" + FACES_APP_NAME + "/resources");
        // Add Myfaces or Mojarra provider libraries when testing facesContainer-x.y; e.g.:
        //facesBeanApp = FATSuite.addMyFaces(facesApp);
        ShrinkHelper.exportDropinAppToServer(facesBeanServer, facesBeanApp, DeployOptions.SERVER_ONLY, DeployOptions.OVERWRITE);

        TestMethod testMethod = getTestMethod(TestMethod.class, testName);

        facesBeanServer.setCheckpoint(CheckpointPhase.AFTER_APP_START, true,
                                      server -> {
                                          assertNotNull("'SRVE0169I: Loading Web Module: " + FACES_APP_NAME + "' message not found in log before restore",
                                                        server.waitForStringInLogUsingMark("SRVE0169I: .*" + FACES_APP_NAME, 0));
                                          assertNotNull("'CWWKZ0001I: Application " + FACES_APP_NAME + " started' message not found in log.",
                                                        server.waitForStringInLogUsingMark("CWWKZ0001I: .*" + FACES_APP_NAME, 0));
                                      });
        facesBeanServer.startServer(getTestMethodNameOnly(testName) + ".log");
    }

    @After
    public void stopServer() throws Exception {
        facesBeanServer.stopServer();
    }

    @AfterClass
    public static void removeWebApp() throws Exception {
        ShrinkHelper.cleanAllExportedArchives();
    }

    @Test
    public void testFacesBeanCdi() throws Exception {
        // Doc: getHttpConnection(URL url, int expectedResponseCode, int connectionTimeout) 
        HttpURLConnection conn = HttpUtils.getHttpConnection(facesBeanServer, '/' + FACES_APP_NAME + "/TestBean.jsf",
            200, 30);

        String bodyResponse = readConnection(conn);

        Log.info(c, testName.getMethod(), bodyResponse);

        assertTrue("Message #1 Not Found!",bodyResponse.contains(":CDIBean::PostConstructCalled::EJB-injected::Resource-injected:"));
        
        // Original Test
        // HttpUtils.findStringInReadyUrl(facesBeanServer, '/' + FACES_APP_NAME + "/TestBean.jsf",
        //                                "CDI Bean value:",
        //                                ":CDIBean::PostConstructCalled::EJB-injected::Resource-injected:");
    }

    @Test
    public void testFacesBean() throws Exception {
        // Doc: getHttpConnection(URL url, int expectedResponseCode, int connectionTimeout) 
        HttpURLConnection conn = HttpUtils.getHttpConnection(facesBeanServer, '/' + FACES_APP_NAME + "/TestBean.jsf",
            200, 30);

        String bodyResponse = readConnection(conn);

        Log.info(c, testName.getMethod(), bodyResponse);

        assertTrue("Message #1 Not Found!",bodyResponse.contains(":JSFBean::PostConstructCalled::EJB-injected::Resource-injected:"));
        
        // HttpUtils.findStringInReadyUrl(facesBeanServer, '/' + FACES_APP_NAME + "/TestBean.jsf",
        //                                "JSF Bean value:",
        //                                ":JSFBean::PostConstructCalled::EJB-injected:");
    }


    /*
     * Copied from OpenAPIConnection
     */
    private String readConnection(HttpURLConnection connection) throws Exception {
        BufferedReader output = HttpUtils.getResponseBody(connection);
        StringBuilder contents = new StringBuilder();

        for (int i = 0; i != -1; i = output.read()) {
            char c = (char) i;
            if (!Character.isISOControl(c)) {
                contents.append((char) i);
            }
            if (c == '\n') {
                contents.append('\n');
            }
        }

        String urlContent = contents.toString();
        return urlContent;
    }

    static enum TestMethod {
        testFacesBeanCdi,
        testFacesBean,
        unknown;
    }
}