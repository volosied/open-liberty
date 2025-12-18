/*******************************************************************************
 * Copyright (c) 2013,  IBM Corporation and others.
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
package trace.war;

import java.util.logging.Logger;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 */
@ServerEndpoint(value = "/multiText")
public class MultiServerEP {

    private static final Logger LOG = Logger.getLogger(MultiServerEP.class.getName());
    private Session _curSess = null;

    @OnMessage
    public String echoText(String text, Session session) {
        LOG.info("MultiServerEP received message: '" + text + "' from session: " + session.getId() +
                " (session open: " + session.isOpen() + ")");
        String response = text;
        LOG.info("MultiServerEP returning (echoing back): '" + response + "' to session: " + session.getId());
        return response;

        /*
         * getOpenSessions currently not implemented, try again later.
         * Set<Session> sessions = _curSess.getOpenSessions();
         * for (Session sess : sessions) {
         * try {
         * System.out.println("PRINTING IT");
         * sess.getBasicRemote().sendText(text);
         * } catch (IOException e) {
         * e.printStackTrace();
         * }
         * }
         * return text;
         */
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        LOG.info("MultiServerEP onClose called for session: " + session.getId() + ", reason: " + reason);
    }

    @javax.websocket.OnError
    public void onError(Session session, Throwable throwable) {
        LOG.severe("MultiServerEP onError called for session: " + session.getId() +
                  ", error: " + throwable.getClass().getName() + ": " + throwable.getMessage());
        throwable.printStackTrace();
    }

    @OnOpen
    public void onOpen(final Session session, EndpointConfig ec) {
        LOG.info("MultiServerEP onOpen called for session: " + session.getId());
        _curSess = session;
    }

}
