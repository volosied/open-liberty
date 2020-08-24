/*******************************************************************************
 * Copyright (c) 2015, 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.jsf.shared.cdi;

import jakarta.faces.application.Application;

public interface CDIJSFInitializer
{
    public void initializeCDIJSFELContextListenerAndELResolver(Application application);

    public void initializeCDIJSFViewHandler(Application application);
}
