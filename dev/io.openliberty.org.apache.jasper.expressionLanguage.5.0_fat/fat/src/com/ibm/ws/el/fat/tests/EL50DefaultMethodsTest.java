/*******************************************************************************
 * Copyright (c) 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.el.fat.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.ibm.websphere.simplicity.ShrinkHelper;
import io.openliberty.el50.fat.servlets.EL50DefaultMethodsServlet;

import componenttest.annotation.Server;
import componenttest.annotation.TestServlet;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.utils.FATServletClient;

/**
 * Tests for default methods in EL 5.0
 * https://github.com/jakartaee/expression-language/issues/43
 */
@RunWith(FATRunner.class)
public class EL50DefaultMethodsTest extends FATServletClient {

    @Server("elServer")
    @TestServlet(servlet = EL50DefaultMethodsServlet.class, contextRoot = "TestEL5.0")
    public static LibertyServer elServer;

    @BeforeClass
    public static void setup() throws Exception {
        ShrinkHelper.defaultDropinApp(elServer, "TestEL5.0.war", "io.openliberty.el50.fat.servlets");

        elServer.startServer(EL50DefaultMethodsTest.class.getSimpleName() + ".log");
    }

    @AfterClass
    public static void tearDown() throws Exception {
        // Stop the server
        if (elServer != null && elServer.isStarted()) {
            elServer.stopServer();
        }
    }
}
