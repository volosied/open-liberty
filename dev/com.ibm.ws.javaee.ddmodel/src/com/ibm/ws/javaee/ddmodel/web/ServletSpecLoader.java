/*******************************************************************************
 * Copyright (c) 2011, 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.javaee.ddmodel.web;

import java.io.InputStream;
import java.util.Properties;

public final class ServletSpecLoader {

    private static Integer version = null;

    public static Integer getServletSpecLevel() {

        if (version != null) {
            try (InputStream input = ServletSpecLoader.class.getClassLoader().getResourceAsStream("com/ibm/ws/webcontainer/speclevel/servletSpecLevel.properties")) {

                if (input != null) {
                    Properties prop = new Properties();
                    prop.load(input);
                    version = Integer.parseInt(prop.getProperty("version"));
                } else {
                    return null;
                }

            } catch (Exception ex) {
                return null;
            }
        }
        return version;
    }
}
