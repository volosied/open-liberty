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
import org.testcontainers.containers.wait.strategy.Wait;

/**
 * Example test class showing how to setup a GenericContainer
 */
@RunWith(FATRunner.class)
public class FacesTest {

    public static final String APP_NAME = "Simple";

    public static RemoteWebDriver driver;

    @Server("build.example.testcontainers")
    public static LibertyServer server;

    // static ChromeOptions options = new ChromeOptions();
    // static {
    //     options.addArguments("--no-sandbox");
    //     options.addArguments("--disable-web-security");
    //     options.addArguments("--allow-insecure-localhost");
    //     options.addArguments("--remote-allow-origins=*");
    //     options.addArguments("--ignore-urlfetcher-cert-requests");
    //     options.addArguments("--auto-open-devtools-for-tabs");
    //     options.addArguments("--disable-gpu");
    // }

    @ClassRule
    public static BrowserWebDriverContainer<?> chrome = new BrowserWebDriverContainer<>(DockerImageName.parse("selenium/standalone-chrome:110.0"))
    .withCapabilities((new ChromeOptions()).addArguments("--no-sandbox", "--allow-insecure-localhost", "--disable-web-security", "--remote-allow-origins=*", "--disable-dev-shm-usage"))
    .waitingFor(Wait.forLogMessage(".*Started Selenium Standalone.*", 1))
    .withAccessToHost(true)
    .withLogConsumer(new SimpleLogConsumer(FacesTest.class, "selenium"));

    @BeforeClass
    public static void setUp() throws Exception {
        ShrinkHelper.defaultDropinApp(server, APP_NAME+ ".war");

        driver = chrome.getWebDriver();

        org.testcontainers.Testcontainers.exposeHostPorts(8010);

        System.out.println("Running setUp");

        server.startServer();
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
        server.stopServer();
    }

    public static String createHttpUrlString(LibertyServer server, String contextRoot, String path) throws Exception {

        StringBuilder sb = new StringBuilder();
        sb.append("http://")
          .append(Inet4Address.getLocalHost().getHostAddress())
          .append(":")
          .append(8010)
          .append("/")
          .append(contextRoot)
          .append("/")
          .append(path);

        return sb.toString();
    }
}
