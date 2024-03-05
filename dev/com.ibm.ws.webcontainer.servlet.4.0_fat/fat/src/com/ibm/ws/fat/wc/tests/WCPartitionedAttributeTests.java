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
        server.startServer(WCSameSiteCookieAttributeTests.class.getSimpleName() + ".log");
    }

    @AfterClass
    public static void tearDown() throws Exception {
        // Stop the server
        if (server != null && server.isStarted()) {
            server.stopServer();
        }
    }  

    /**
     * Tested Set-Cookie via addCookie, setHeader, and addHeader
     * 
     *      
     */
    @Test
    public void testPartitionedCookie_Basic() throws Exception {
        String expectedSetHeader = "Set-Cookie: PartitionedCookieName_SetHeader=PartitionedCookieValue_SetHeader; Secure; SameSite=None; Partitioned";
        String expectedAddHeader = "Set-Cookie: PartitionedCookieName_AddHeader=PartitionedCookieValue_AddHeader; Secure; SameSite=None; Partitioned";
        String expectedAddCookie = "Set-Cookie: PartitionedCookieName_AddCookie=PartitionedCookieValue_AddCookie; Secure; SameSite=None; Partitioned";

        String expectedResponse = "Welcome to the TestPartitionedCookieServlet!";
        boolean setHeaderFound = false;
        boolean addHeaderFound = false;
        boolean addCookieFound = false;

        String url = "http://" + server.getHostname() + ":" + server.getHttpDefaultPort() + "/" + APP_NAME + "/TestPartitionedCookie";
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
                int cookieCount = 0;
                String headerValue;
                for (Header header : headers) {
                    headerValue = header.toString();
                    LOG.info("\n" + headerValue);
                    if(headerValue.contains("Set-Cookie:")){
                        cookieCount++;
                    }
                    if (headerValue.equals(expectedSetHeader)) {
                        setHeaderFound = true;
                    } else if (headerValue.equals(expectedAddHeader)) {
                        addHeaderFound = true;
                    } else if (headerValue.equals(expectedAddCookie)) {
                        addCookieFound = true;
                    }
                }

                assertTrue("The response did not contain the following String: " + expectedResponse, responseText.contains(expectedResponse));
                assertTrue("The response did not contain the expected Set-Cookie header: " + expectedSetHeader, setHeaderFound);
                assertTrue("The response did not contain the expected Set-Cookie header: " + expectedAddHeader, addHeaderFound);
                assertTrue("The response did not contain the expected Set-Cookie header: " + expectedAddCookie, addCookieFound);
                assertTrue("The response did not contain the expected number of cookie headers" + expectedAddHeader, cookieCount == 3);
            }
        }
    }

}
