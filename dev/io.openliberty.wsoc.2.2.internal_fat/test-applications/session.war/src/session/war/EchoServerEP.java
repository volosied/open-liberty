/*******************************************************************************
 * Copyright (c) 2024 IBM Corporation and others.
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
package session.war;

import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.SendHandler;
import jakarta.websocket.SendResult;
import jakarta.websocket.Session;

import jakarta.websocket.server.ServerEndpoint;

/*
 * Echos messages sent to this endpoint.
 */
@ServerEndpoint(value = "/echo")
public class EchoServerEP {

    @OnOpen
    public void onOpen(final Session session) {

    }

    // asynchronous text message delivery using a callback

    @OnMessage
    public void onMsg(String msg, Session session) {
        System.out.println("MSG SESSION: " + session);
        //Thread.dumpStack();
        session.getAsyncRemote().sendText("got your message ", new SendHandler() {
            @Override
            public void onResult(SendResult result) {
               System.out.println("RESULT SESSION: " + result.getSession());
               //Thread.dumpStack();
            }
        });
    }

}