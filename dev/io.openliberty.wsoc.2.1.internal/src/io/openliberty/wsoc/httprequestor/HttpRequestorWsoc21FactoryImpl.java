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
package io.openliberty.wsoc.httprequestor;

import com.ibm.ws.wsoc.outbound.HttpRequestorFactory;

import com.ibm.ws.wsoc.outbound.HttpRequestor;

import com.ibm.ws.wsoc.outbound.WsocAddress;
import jakarta.websocket.ClientEndpointConfig;
import com.ibm.ws.wsoc.ParametersOfInterest;

public class HttpRequestorWsoc21FactoryImpl implements HttpRequestorFactory {
    
    public HttpRequestor getHttpRequestor(WsocAddress endpointAddress, ClientEndpointConfig config, ParametersOfInterest things){
        System.out.println("Returning new HttpRequestorWsoc21 object");
        return new HttpRequestorWsoc21(endpointAddress, config, things);
    }
}
