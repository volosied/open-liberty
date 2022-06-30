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
package basic.war;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;

import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;

import jakarta.websocket.server.ServerEndpoint;

import io.openliberty.wsoc.common.Utils;

@ServerEndpoint(value = "/zeroTimeout")
public class ZeroTimeOutServerEP {

    Session session;

    @OnOpen
    public void onOpen(final Session session) {
        if (session != null) {
            this.session = session;
            System.out.println("Timeout Server EP");
            session.setMaxIdleTimeout(0);
        }
    }

    @OnMessage
    public String echo(String input) {
        
        return String.valueOf(this.session.getMaxIdleTimeout());
    }

}