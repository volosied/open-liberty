/*******************************************************************************
 * Copyright (c) 2017, 2023 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package io.openliberty.jsf.fat.selenium.util.internal;

import java.net.URL;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.ibm.websphere.simplicity.log.Log;

import componenttest.topology.impl.LibertyServer;

/**
 * A utility class for JSF tests. Based off existing utility classes in other JSF FATs
 */
public class JSFUtils {

    public static String createSeleniumURLString(LibertyServer server, String contextRoot, String path) {

        StringBuilder sb = new StringBuilder();
        sb.append("http://")
                        .append("host.testcontainers.internal")
                        .append(":")
                        .append(server.getHttpDefaultPort())
                        .append("/")
                        .append(contextRoot)
                        .append("/")
                        .append(path);

        return sb.toString();
    }

}
