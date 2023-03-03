/*******************************************************************************
 * Copyright (c) 2021 IBM Corporation and others.
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

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.testcontainers.containers.PostgreSQLContainer;

import componenttest.containers.TestContainerSuite;
import componenttest.rules.repeater.FeatureReplacementAction;
import componenttest.rules.repeater.RepeatTests;

@RunWith(Suite.class)
@SuiteClasses({
                FacesTest.class, //LITE
})
/**
 * Example FATSuite class to show how to setup suite level testcontainers and properties.
 *
 * The suite class MUST extend the TestContainerSuite class.
 * The TestContainerSuite will do important initialization and verification steps
 */
public class FATSuite extends TestContainerSuite {


}
