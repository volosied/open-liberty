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

import componenttest.annotation.Server;
import componenttest.annotation.TestServlet;
import componenttest.containers.SimpleLogConsumer;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.utils.FATServletClient;
import web.generic.ContainersTestServlet;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.*;
/**
 * Example test class showing how to setup a GenericContainer
 */
@RunWith(FATRunner.class)
public class ContainersTest extends FATServletClient {

    public static final String APP_NAME = "Simple";

    public static RemoteWebDriver driver;

    @Server("build.example.testcontainers")
    @TestServlet(servlet = ContainersTestServlet.class, contextRoot = APP_NAME)
    public static LibertyServer server;

    @Rule
    public static BrowserWebDriverContainer chrome = new BrowserWebDriverContainer()
            .withCapabilities(new ChromeOptions()); 

    // public static final String POSTGRES_DB = "test";
    // public static final String POSTGRES_USER = "test";
    // public static final String POSTGRES_PASSWORD = "test";
    // public static final int POSTGRE_PORT = 5432;

    /**
     * When using a generic container you will need to provide all the information needed
     * to run that container. This is equivalent of constructing a docker run command.
     * <br>
     *
     * This is annotated as a ClassRule which will call start/stop on the container automatically
     *
     * <pre>
     * ~~Common settings~~
     * Constructor: accepts image name in form user/container:version
     * - withExposedPorts: what ports does that container use that need to be exposed
     * - withEnv: evironment variables that can be set on the container
     * - withCommand: replace docker CMD with a custom command
     * - withLogConsumer: redirect stout/sterr from container to a log consumer
     * Use the SimpleLogConsumer from fattest.simplicity to redirect those logs to output.txt
     * - waitingFor: defines a wait strategy to know when container has started
     * </pre>
     *
     * NOTE: the testcontainers project has a pre-configured PostgreSQLContainer class that could
     * have been used here. This is just an example of how to setup a GenericContainer.
     */
    @ClassRule
    public static GenericContainer<?> container = new GenericContainer<>("selenium/standalone-chrome:110.0")
                    .withExposedPorts(4444, 8010)
                    .withLogConsumer(new SimpleLogConsumer(ContainersTest.class, "selenium"));
                    // .waitingFor(new LogMessageWaitStrategy()
                    //                 .withRegEx(".*database system is ready to accept connections.*\\s")
                    //                 .withTimes(2)
                    //                 .withStartupTimeout(Duration.ofSeconds(60)));

    @BeforeClass
    public static void setUp() throws Exception {
        ShrinkHelper.defaultApp(server, APP_NAME, "simple");


        driver = chrome.getWebDriver();

        System.out.println("Running setUp");

        server.startServer();

        // runTest(server, APP_NAME, "setupDatabase");
    }

    @Test
    public void testSimpleFacelet() {
        System.out.println("Running testSimpleFacelet");
        // System.out.println(getPage("http://localhost:8010/Simple/SimpleTest.xhml").getPageSource());

        // RemoteWebDriver driver = new RemoteWebDriver(chrome.getSeleniumAddress(), new ChromeOptions());
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));

        String address = container.getHost() + ":" + container.getMappedPort(8010);

        driver.get(address + "/Simple/SimpleTest.xhml");
        System.out.println(driver.getPageSource());

    }

    @AfterClass
    public static void tearDown() throws Exception {
        server.stopServer();
    }

    // protected WebPage getPage(String url) {
    //     driver.get(url);
    //     WebPage webPage = new WebPage(driver);
    //     // Sometimes it takes longer until the first page is loaded after container startup
    //     webPage.waitForPageToLoad(Duration.ofSeconds(120));
    //     return webPage;
    // }
}
