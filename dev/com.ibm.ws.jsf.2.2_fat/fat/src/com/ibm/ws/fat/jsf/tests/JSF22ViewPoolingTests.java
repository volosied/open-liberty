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

import static org.junit.Assert.assertFalse;

import java.util.List;
import java.util.logging.Logger;

import junit.framework.Assert;

import componenttest.annotation.Server;
import componenttest.topology.impl.LibertyServer;
import componenttest.custom.junit.runner.FATRunner;

import org.jboss.shrinkwrap.api.spec.WebArchive;
import com.ibm.websphere.simplicity.ShrinkHelper;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import componenttest.annotation.MinimumJavaLevel;
import componenttest.custom.junit.runner.Mode;
import componenttest.custom.junit.runner.Mode.TestMode;
import org.junit.runner.RunWith;
import com.ibm.ws.fat.jsf.JSFUtils;
import java.net.URL;
import org.junit.rules.TestName;

/**
 *jsfTestServer2
 */
@Mode(TestMode.FULL)
@MinimumJavaLevel(javaLevel = 7)
@RunWith(FATRunner.class)
public class JSF22ViewPoolingTests {
    @Rule
    public TestName name = new TestName();
    
        String contextRoot = "JSF22ViewPooling";
    
        protected static final Class<?> c = JSFCompELTests.class;
    
        @Server("jsfTestServer2")
        public static LibertyServer jsfTestServer2;
    
        @BeforeClass
        public static void setup() throws Exception {
            ShrinkHelper.defaultDropinApp(jsfTestServer2, "JSF22ViewPooling.war");

            jsfTestServer2.startServer(JSF22ViewPoolingTests.class.getSimpleName() + ".log");
        }
    

    @AfterClass
    public static void tearDown() throws Exception {
            // Stop the server
            if (jsfTestServer2 != null && jsfTestServer2.isStarted()) {
                    //Ignore expected exception
                    jsfTestServer2.stopServer();
            }
    }

    /**
     * Check to make sure that no NullPointerException is thrown in ViewPoolProcessor.pushPartialView()
     * when View Pooling is enabled and oamEnableViewPool is set to false in a specific resource
     * 
     * @throws Exception
     */
    @Test
    public void JSF22ViewPooling_TestViewPoolingDisabled() throws Exception {

        WebClient webClient = new WebClient();
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

        URL url = JSFUtils.createHttpUrl(jsfTestServer2, contextRoot, "JSF22ViewPooling_Disabled.xhtml");
        HtmlPage page = (HtmlPage) webClient.getPage(url);

        if (page == null) {
            Assert.fail("JSF22ViewPooling_Disabled.xhtml did not render properly.");
        }

        String msgToSearchFor1 = "java.lang.NullPointerException";
        String msgToSearchFor2 = "ViewPoolProcessor.pushPartialView";

        List<String> msg1 = jsfTestServer2.findStringsInLogs(msgToSearchFor1);
        List<String> msg2 = jsfTestServer2.findStringsInLogs(msgToSearchFor2);

        assertFalse("NullPointerException and ViewPoolProcessor.pushPartialView found in logs", !msg1.isEmpty() && !msg2.isEmpty());

    }
}
