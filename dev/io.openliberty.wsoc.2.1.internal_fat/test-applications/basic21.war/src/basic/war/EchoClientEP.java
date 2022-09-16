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

import jakarta.websocket.Endpoint;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.EndpointConfig;

import io.openliberty.wsoc.common.Utils;

/*
 * Echos messages sent to this endpoint.
 */
// @ClientEndpoint
public class EchoClientEP extends Endpoint {

    Session session = null;

    // public void onOpen(final Session session) {
    
    //     try {
    //         session.getBasicRemote().sendText("hello");
    //     } catch(Exception ex){

    //     }
    // }

    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
        session.addMessageHandler(String.class, new MessageHandler.Whole<String>() {
            public void onMessage(String text) {
                try {
                    
                    System.out.println("Recieved " + text);
                    session.close();
                    //session.getBasicRemote();.sendString("Got your message (" + text + "). Thanks !");
                } catch (IOException ioe) {
                    // handle send failure here
                }
            }
        });

        try {
            session.getBasicRemote().sendText("sending hello");
            //session.getBasicRemote();.sendString("Got your message (" + text + "). Thanks !");
        } catch (IOException ioe) {
            // handle send failure here
        }
    }

}
