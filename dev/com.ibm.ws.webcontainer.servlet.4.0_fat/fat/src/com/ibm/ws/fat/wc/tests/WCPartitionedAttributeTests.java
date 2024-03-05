package com.ibm.ws.fat.wc.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.logging.Logger;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ibm.websphere.simplicity.ShrinkHelper;
import com.ibm.websphere.simplicity.config.HttpEndpoint;
import com.ibm.websphere.simplicity.config.SameSite;
import com.ibm.websphere.simplicity.config.ServerConfiguration;

import componenttest.annotation.Server;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.custom.junit.runner.Mode;
import componenttest.custom.junit.runner.Mode.TestMode;
import componenttest.topology.impl.LibertyServer;

/**
 * Tests for CHIPS (Partitioned Cookies)
 *
 */
@RunWith(FATRunner.class)
public class WCPartitionedAttributeTests {
 
    private static final Logger LOG = Logger.getLogger(WCSameSiteCookieAttributeTests.class.getName());
    private static final String APP_NAME = "PartitionedTest";

    @Server("servlet40_partitioned")
    public static LibertyServer server;

    @BeforeClass
    public static void before() throws Exception {
        // Create the PartitionedTest.war application
        ShrinkHelper.defaultDropinApp(server, APP_NAME + ".war", "partitioned.servlets");

        // Start the server and use the class name so we can find logs easily.
        sameSiteServer.startServer(WCSameSiteCookieAttributeTests.class.getSimpleName() + ".log");
    }

    @AfterClass
    public static void tearDown() throws Exception {
        // Stop the server
        if (server != null && server.isStarted()) {
            sameSiteServer.stopServer();
        }
    }  

    /**
     * Drive a request to a Servlet that prints all of the Set-Cookie headers in the
     * HttpServletResponse. A filter should be invoked before the Servlet that adds
     * a Set-Cookie header, we should verify that it contains those headers.
     *
     * The Filter performs two actions:
     * ((HttpServletResponse) response).setHeader("Set-Cookie", "MySameSiteCookieNameSetHeader=MySameSiteCookieValueSetHead; Secure; SameSite=None");
     * ((HttpServletResponse) response).addHeader("Set-Cookie", "MySameSiteCookieNameAddHeader=MySameSiteCookieValueAddHeader; Secure; SameSite=None");
     *
     * We also need to ensure that the response contains that header at the client.
     *
     * @throws Exception
     * 
    @Test
    public void testSameSiteSetCookie() throws Exception {
        String expectedSetHeader = "Set-Cookie: MySameSiteCookieNameSetHeader=MySameSiteCookieValueSetHeader; Secure; SameSite=None";
        String expectedAddHeader = "Set-Cookie: MySameSiteCookieNameAddHeader=MySameSiteCookieValueAddHeader; Secure; SameSite=None";
        String expectedResponse = "Welcome to the SameSiteSetCookieServlet!";
        boolean foundAddHeader = false;
        boolean foundSetHeader = false;
        boolean splitSameSiteHeaderFound = false;

        String url = "http://" + sameSiteServer.getHostname() + ":" + sameSiteServer.getHttpDefaultPort() + "/" + APP_NAME_SAMESITE + "/TestSetCookie";
        LOG.info("url: " + url);

        HttpGet getMethod = new HttpGet(url);

        try (final CloseableHttpClient client = HttpClientBuilder.create().build()) {
            try (final CloseableHttpResponse response = client.execute(getMethod)) {
                String responseText = EntityUtils.toString(response.getEntity());
                LOG.info("\n" + "Response Text:");
                LOG.info("\n" + responseText);

                Header[] headers = response.getHeaders("Set-Cookie");
                LOG.info("\n" + "Set-Cookie headers contained in the response:");

                // Verify that the expected Set-Cookie headers were found by the client.
                String headerValue;
                for (Header header : headers) {
                    headerValue = header.toString();
                    LOG.info("\n" + headerValue);
                    if (headerValue.equals(expectedSetHeader)) {
                        foundSetHeader = true;
                    } else if (headerValue.equals(expectedAddHeader)) {
                        foundAddHeader = true;
                    } else if (isSplitSameSiteSetCookieHeader(headerValue, "SameSite=None")) {
                        splitSameSiteHeaderFound = true;
                    }
                }

                assertTrue("The response did not contain the following String: " + expectedResponse, responseText.contains(expectedResponse));
                assertTrue("The response did not contain the expected Set-Cookie header: " + expectedSetHeader, foundSetHeader);
                assertTrue("The response did not contain the expected Set-Cookie header: " + expectedAddHeader, foundAddHeader);
                assertFalse("The response contained a split SameSite Set-Cookie header and it should not have.", splitSameSiteHeaderFound);
            }
        }
    }
     */

}
