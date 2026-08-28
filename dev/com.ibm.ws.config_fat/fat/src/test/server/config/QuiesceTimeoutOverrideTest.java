/*******************************************************************************
 * Copyright (c) 2026 IBM Corporation and others.
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

package test.server.config;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import componenttest.custom.junit.runner.FATRunner;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.impl.LibertyServerFactory;

/**
 * Tests that quiesceTimeout on the server element overrides chainQuiesceTimeout
 * on the channelfw element, and that the appropriate informational message is logged.
 */
@RunWith(FATRunner.class)
public class QuiesceTimeoutOverrideTest {

    private static LibertyServer server;

    @BeforeClass
    public static void setUp() throws Exception {
        server = LibertyServerFactory.getLibertyServer("com.ibm.ws.config.quiesce.override");
    }

    @After
    public void cleanupAfterTest() throws Exception {
        if (server != null && server.isStarted()) {
            // CWWKG0111W is expected by testServerQuiesceTimeoutBelowMinimumFallsBackAndDoesNotOverride;
            // passing it here is harmless for all other tests (ignored if absent).
            server.stopServer("CWWKG0111W");
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (server != null && server.isStarted()) {
            server.stopServer("CWWKG0111W");
        }
    }

    /**
     * Test that when both quiesceTimeout and chainQuiesceTimeout are configured,
     * the CWWKO0400I informational message is logged with the ignored chainQuiesceTimeout value,
     * and verify the actual value used is from quiesceTimeout (not chainQuiesceTimeout).
     */
    @Test
    public void testQuiesceTimeoutOverridesChainQuiesceTimeout() throws Exception {
        server.setServerConfigurationFile("quiesceoverride/quiesceTimeoutOverridesChainQuiesceTimeout.xml");
        server.startServer(true); // Enable trace

        // Wait for server to be ready
        assertNotNull("Server did not start successfully",
                      server.waitForStringInLog("CWWKF0011I")); // Server is ready

        // Verify the override message appears showing the ignored chainQuiesceTimeout value of 10 seconds
        assertNotNull("Expected CWWKO0400I message indicating quiesceTimeout overrides chainQuiesceTimeout",
                      server.waitForStringInLog("CWWKO0400I.*chainQuiesceTimeout.*10 seconds"));
    }

    /**
     * Test that when only quiesceTimeout is configured (and chainQuiesceTimeout is set to the default value),
     * no override message is logged, because we assume chainQuiesceTimeout wasn't configured.
     */
    @Test
    public void testQuiesceTimeoutOnly() throws Exception {
        server.setServerConfigurationFile("quiesceoverride/quiesceTimeoutOnly.xml");
        server.startServer(true); // Enable trace

        // Wait for server to be ready
        assertNotNull("Server did not start successfully",
                      server.waitForStringInLog("CWWKF0011I")); // Server is ready

        // Verify the override message does NOT appear when chainQuiesceTimeout is not explicitly configured
        assertNull("CWWKO0400I message should not appear when only quiesceTimeout is configured",
                   server.waitForStringInLog("CWWKO0400I", 5000));
    }

    /**
     * Test that when only chainQuiesceTimeout is configured (no quiesceTimeout on server element),
     * no override message is logged because there's no server-level quiesceTimeout to perform the override.
     */
    @Test
    public void testChainQuiesceTimeoutOnly() throws Exception {
        server.setServerConfigurationFile("quiesceoverride/chainQuiesceTimeoutOnly.xml");
        server.startServer(true); // Enable trace

        // Wait for server to be ready
        assertNotNull("Server did not start successfully",
                      server.waitForStringInLog("CWWKF0011I")); // Server is ready

        // Verify the override message does NOT appear
        assertNull("CWWKO0400I message should not appear when only chainQuiesceTimeout is configured",
                   server.waitForStringInLog("CWWKO0400I", 5000));
    }
    /**
     * Test that when both quiesceTimeout and chainQuiesceTimeout are configured to the SAME value,
     * no override message is logged because there's no conflict to report.
     * The override still happens, but silently since both values are identical.
     */
    @Test
    public void testQuiesceTimeoutSameAsChainQuiesceTimeout() throws Exception {
        server.setServerConfigurationFile("quiesceoverride/quiesceTimeoutSameAsChainQuiesceTimeout.xml");
        server.startServer(true); // Enable trace

        // Wait for server to be ready
        assertNotNull("Server did not start successfully",
                      server.waitForStringInLog("CWWKF0011I")); // Server is ready

        // Verify the override message does NOT appear when both timeouts are the same
        assertNull("CWWKO0400I message should not appear when both timeouts are set to the same value",
                   server.waitForStringInLog("CWWKO0400I", 5000));
    }

    /**
     * Test that chainQuiesceTimeout is respected as-is when no quiesceTimeout is set on
     * the server element (the core regression case fixed by the XMLConfigParser bug fix).
     * <p>
     * Before the fix, setQuiesceTimeoutFromMetatype() called setQuiesceTimeoutMillis() which
     * incorrectly set isQuiesceTimeoutExplicitlyConfigured=true, causing the override path
     * in ChannelFrameworkConfigImpl to fire and silently discard the user's chainQuiesceTimeout.
     */
    @Test
    public void testChainQuiesceTimeoutIsRespectedWhenNoServerLevelOverride() throws Exception {
        server.setServerConfigurationFile("quiesceoverride/chainQuiesceTimeoutIsRespected.xml");
        server.startServer(true);

        assertNotNull("Server did not start successfully",
                      server.waitForStringInLog("CWWKF0011I"));

        // No server-level quiesceTimeout - the override message must NOT appear
        assertNull("CWWKO0400I must not appear when quiesceTimeout is absent from the server element",
                   server.waitForStringInLog("CWWKO0400I", 5000));

        // Trace confirms the correct value was picked up (trace.log, requires trace spec enabled in bootstrap.properties)
        assertNotNull("Expected final chainQuiesceTimeout=119000ms in trace",
                      server.waitForStringInTrace("setDefaultChainQuiesceTimeout: final chainQuiesceTimeout=119000ms"));
    }

    /**
     * Test that a quiesceTimeout on the server element below the 30s minimum is rejected
     * with a CWWKG0111W warning, falls back to the default 30s, and — crucially — does NOT
     * set isQuiesceTimeoutExplicitlyConfigured=true.  As a result the channelfw
     * chainQuiesceTimeout="60s" in the config should still take effect and the
     * CWWKO0400I override message should NOT be logged.
     */
    @Test
    public void testServerQuiesceTimeoutBelowMinimumFallsBackAndDoesNotOverride() throws Exception {
        server.setServerConfigurationFile("quiesceoverride/serverQuiesceTimeoutBelowMinimum.xml");
        server.startServer(true);

        assertNotNull("Server did not start successfully",
                      server.waitForStringInLog("CWWKF0011I"));

        // Invalid quiesceTimeout="22s" must produce a warning
        assertNotNull("Expected CWWKG0111W warning for below-minimum quiesceTimeout",
                      server.waitForStringInLog("CWWKG0111W.*22"));

        // Because the invalid value was rejected, the server-level override must NOT fire
        assertNull("CWWKO0400I must not appear when server quiesceTimeout was rejected as invalid",
                   server.waitForStringInLog("CWWKO0400I", 5000));

        // chainQuiesceTimeout="60s" should win - confirmed via trace
        assertNotNull("Expected final chainQuiesceTimeout=60000ms in trace",
                      server.waitForStringInTrace("setDefaultChainQuiesceTimeout: final chainQuiesceTimeout=60000ms"));
    }

    /**
     * Test that placing chainQuiesceTimeout directly on the server element (not on channelfw)
     * is silently ignored by the parser, which only reads the "quiesceTimeout" attribute.
     * The channelfw chainQuiesceTimeout="90s" in the config should take effect and
     * no CWWKO0400I override message should appear.
     */
    @Test
    public void testChainQuiesceTimeoutOnServerElementIsIgnored() throws Exception {
        server.setServerConfigurationFile("quiesceoverride/chainQuiesceTimeoutOnServerElement.xml");
        server.startServer(true);

        assertNotNull("Server did not start successfully",
                      server.waitForStringInLog("CWWKF0011I"));

        // chainQuiesceTimeout on <server> is not recognised - no override message expected
        assertNull("CWWKO0400I must not appear when chainQuiesceTimeout is misplaced on the server element",
                   server.waitForStringInLog("CWWKO0400I", 5000));

        // The channelfw value should be used - confirmed via trace
        assertNotNull("Expected final chainQuiesceTimeout=90000ms in trace",
                      server.waitForStringInTrace("setDefaultChainQuiesceTimeout: final chainQuiesceTimeout=90000ms"));
    }
}
