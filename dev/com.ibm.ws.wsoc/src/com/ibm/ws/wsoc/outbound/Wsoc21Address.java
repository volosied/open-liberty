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
package com.ibm.ws.wsoc.outbound;

import java.net.InetSocketAddress;
import java.net.URI;

import javax.net.ssl.SSLContext;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.wsspi.http.channel.outbound.HttpAddress;

import com.ibm.ws.channel.ssl.internal.wsoc.SSLEnabledAddress;

/**
 *
 */
public class Wsoc21Address extends Wsoc10Address implements SSLEnabledAddress {

    private SSLContext context = null;

    public Wsoc21Address(URI path){
        super(path);
    }

    public void setSSLContext(SSLContext context){
        this.context = context;
    }

    public SSLContext getSSLContext(){
        return context;
    }

}
