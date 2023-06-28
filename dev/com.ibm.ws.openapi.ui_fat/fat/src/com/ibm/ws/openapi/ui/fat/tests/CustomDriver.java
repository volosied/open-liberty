package com.ibm.ws.openapi.ui.fat.tests;
/*
 * Copyright (c) 2023 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License v. 2.0 are satisfied: GPL-2.0 with Classpath-exception-2.0 which
 * is available at https://openjdk.java.net/legal/gplv2+ce.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0 or Apache-2.0
 */

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

// import org.apache.commons.io.output.NullOutputStream;
import org.openqa.selenium.By;
// import org.openqa.selenium.Capabilities;
// import org.openqa.selenium.Credentials;
// import org.openqa.selenium.JavascriptExecutor;
// import org.openqa.selenium.OutputType;
// import org.openqa.selenium.Pdf;
// import org.openqa.selenium.ScriptKey;
// import org.openqa.selenium.SearchContext;
// import org.openqa.selenium.TimeoutException;
// import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
// import org.openqa.selenium.chrome.ChromeDriver;
// import org.openqa.selenium.chrome.ChromeDriverLogLevel;
// import org.openqa.selenium.chrome.ChromeDriverService;
// import org.openqa.selenium.chrome.ChromeOptions;
// import org.openqa.selenium.chromium.ChromiumNetworkConditions;
// import org.openqa.selenium.devtools.DevTools;
// import org.openqa.selenium.devtools.v108.network.Network;
// import org.openqa.selenium.devtools.v108.network.model.Request;
// import org.openqa.selenium.devtools.v108.network.model.RequestId;
// import org.openqa.selenium.devtools.v108.network.model.ResponseReceived;
// import org.openqa.selenium.devtools.v108.network.model.TimeSinceEpoch;
// import org.openqa.selenium.html5.LocalStorage;
// import org.openqa.selenium.html5.Location;
// import org.openqa.selenium.html5.SessionStorage;
// import org.openqa.selenium.interactions.Sequence;
// import org.openqa.selenium.logging.EventType;
// import org.openqa.selenium.mobile.NetworkConnection;
// import org.openqa.selenium.print.PrintOptions;
// import org.openqa.selenium.remote.CommandExecutor;
// import org.openqa.selenium.remote.CommandPayload;
// import org.openqa.selenium.remote.ErrorHandler;
// import org.openqa.selenium.remote.FileDetector;
// import org.openqa.selenium.remote.SessionId;
// import org.openqa.selenium.virtualauthenticator.VirtualAuthenticator;
// import org.openqa.selenium.virtualauthenticator.VirtualAuthenticatorOptions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * Extended driver which we need for getting the http response code and the http response without having to revert to
 * proxy solutions
 *
 * <p>
 * We need access top the response body and response code from always the last access
 *
 * <p>
 * We use the chrome dev tools to access the data but we isolate the new functionality in an interface, so other drivers
 * must apply something different to get the results
 *
 * @see also https://medium.com/codex/selenium4-a-peek-into-chrome-devtools-92bca6de55e0
 */
@SuppressWarnings("unused")
public class CustomDriver implements ExtendedWebDriver {

    RemoteWebDriver driver;

    public CustomDriver(RemoteWebDriver driver){
        this.driver = driver;
    }

    @Override
    public String getPageText() {
        String head = this.driver.findElement(By.tagName("head")).getAttribute("innerText").replaceAll("[\\s\\n ]", " ");
        String body = this.driver.findElement(By.tagName("body")).getAttribute("innerText").replaceAll("[\\s\\n ]", " ");
        return head + " " + body;
    }

    @Override
    public String getPageTextReduced() {
        String head = this.driver.findElement(By.tagName("head")).getAttribute("innerText");
        String body = this.driver.findElement(By.tagName("body")).getAttribute("innerText");
        // handle blanks and nbsps
        return (head + " " + body).replaceAll("[\\s\\u00A0]+", " ");
    }

    @Override
    public WebDriver.Options manage() {
        return driver.manage();
    }

    @Override
    public WebDriver.Navigation navigate() {
        return driver.navigate();
    }
    
    public Set<String> getWindowHandles() {
        return driver.getWindowHandles();
    }

    public String getWindowHandle() {
        return driver.getWindowHandle();
    }

    public WebDriver.TargetLocator switchTo() {
        return driver.switchTo();
    }


    public void close() {
        driver.close();
    }

    public void quit() {
        driver.quit();
    }

    public void get(String url) {
        driver.get(url);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public String getPageSource() {
        return driver.getPageSource();
    }


    public WebElement findElement(By by) {
        return driver.findElement(by);
    }

    public List<WebElement> findElements(By by) {
        return driver.findElements(by);
    }

}