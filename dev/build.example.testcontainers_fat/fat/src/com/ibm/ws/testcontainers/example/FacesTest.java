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
/**
 * Example test class showing how to setup a GenericContainer
 */
@RunWith(FATRunner.class)
public class FacesTest extends FATServletClient {

    public static final String APP_NAME = "Simple";

    public static RemoteWebDriver driver;

    @Server("build.example.testcontainers")
    public static LibertyServer server;

    @ClassRule
    public static BrowserWebDriverContainer<?> chrome = new BrowserWebDriverContainer<>(DockerImageName.parse("selenium/standalone-chrome:110.0"))
    .withCapabilities(new ChromeOptions());


    @BeforeClass
    public static void setUp() throws Exception {
        ShrinkHelper.defaultDropinApp(server, APP_NAME+ ".war");


        driver = chrome.getWebDriver();

        System.out.println(chrome.getSeleniumAddress() );
        System.out.println(chrome.getPort() );

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

    public static String createHttpUrlString(LibertyServer server, String contextRoot, String path) {

        StringBuilder sb = new StringBuilder();
        sb.append("http://")
          .append(server.getHostname())
          .append(":")
          .append(server.getHttpDefaultPort())
          .append("/")
          .append(contextRoot)
          .append("/")
          .append(path);

        return sb.toString();
    }
}
