/*******************************************************************************
 * Copyright (c) 2021, 2022 IBM Corporation and others.
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
package com.ibm.ws.testcontainers.example;

import java.net.Inet4Address;
import java.time.Duration;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.containers.wait.strategy.Wait;

import com.ibm.websphere.simplicity.ShrinkHelper;
import org.testcontainers.utility.DockerImageName;

import componenttest.annotation.Server;
import componenttest.annotation.TestServlet;
import componenttest.containers.SimpleLogConsumer;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.utils.FATServletClient;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.*;
import org.testcontainers.containers.Network;
import componenttest.containers.SimpleLogConsumer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
/**
 * Example test class showing how to setup a GenericContainer
 */
@RunWith(FATRunner.class)
public class FacesTest {

    public static final String APP_NAME = "Simple";

    public static RemoteWebDriver driver;

    @Server("build.example.testcontainers")
    public static LibertyServer server;

    public static BrowserWebDriverContainer<?> chrome;

    private static final int SELENIUM_PORT = 4444;

    private static final String BUILD_PROP_SERVER_ORIGIN = "fat.test.server.origin";

    @BeforeClass
    public static void setUp() throws Exception {
        System.out.println("IN SETUP");
        ShrinkHelper.defaultDropinApp(server, APP_NAME+ ".war");

        server.startServer();
        org.testcontainers.Testcontainers.exposeHostPorts(8010);

        chrome = new BrowserWebDriverContainer<>(DockerImageName.parse("selenium/standalone-chrome:110.0"))
        .withCapabilities((new ChromeOptions()).addArguments("--no-sandbox", "--headless", "--disable-dev-shm-usage"))
        .withNetwork(Network.SHARED)
        // .withNetworkMode("host")
        .withAccessToHost(true)
        .withLogConsumer(new SimpleLogConsumer(FacesTest.class, "selenium"));

        // chrome.waitingFor(Wait.forLogMessage(".*Started Selenium Standalone.*\\n", 1)).start();
        chrome.start();

        org.testcontainers.Testcontainers.exposeHostPorts(8010);
        
        driver = chrome.getWebDriver();

    }

    @Test
    public void testSimpleFacelet() throws Exception {
        System.out.println("Running testSimpleFacelet");

        String url = createHttpUrlString(server, "Simple", "SimpleTest.xhtml");
        driver.get(url);

        System.out.println(driver.getPageSource()); 

    }

    @AfterClass
    public static void tearDown() throws Exception {
        chrome.stop();
        server.stopServer();
    }

    public static String createHttpUrlString(LibertyServer server, String contextRoot, String path) {
        StringBuilder sb = new StringBuilder();
        try{
            sb.append("http://")
            .append(Inet4Address.getLocalHost().getHostAddress())
            .append(":")
            .append("8010")
            .append("/")
            .append(contextRoot)
            .append("/")
            .append(path);
        } catch (Exception e) {

        }



        return sb.toString();
    }
}
