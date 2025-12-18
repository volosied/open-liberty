/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation and others.
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
package io.openliberty.wsoc.endpoints.client.trace;

import java.io.IOException;
import java.util.logging.Logger;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import io.openliberty.wsoc.util.wsoc.TestHelper;
import io.openliberty.wsoc.util.wsoc.WsocTestContext;

/**
 *
 */
public class MultiClientEP implements TestHelper {

    private static final Logger LOG = Logger.getLogger(MultiClientEP.class.getName());
    public WsocTestContext _wtr = null;

    @ClientEndpoint
    public static class SimpleReceiverTest extends MultiClientEP {

        @OnMessage
        public void echoText(String data) {
            LOG.info(_wtr + " SimpleReceiverTest received message: " + data +
                    " (count: " + (_wtr.getMessageCount() + 1) + ")");
            _wtr.addMessage(data);

            if (_wtr.limitReached()) {
                LOG.info(_wtr + " SimpleReceiverTest limit reached, terminating");
                _wtr.terminateClient();
            }
        }

        @OnOpen
        public void onOpen(Session sess) {
            LOG.info(_wtr + " SimpleReceiverTest onOpen called, session: " + sess.getId());
            _wtr.connected();
        }
    }

    @ClientEndpoint
    public static class SimplePublisherTest extends MultiClientEP {

        public String[] _data = {};
        public int _counter = 0;

        public SimplePublisherTest(String[] data) {
            _data = data;
        }

        @OnMessage
        public String echoText(String data, Session sess) {
            LOG.info(_wtr + " SimplePublisherTest received message: " + data +
                    " (count: " + (_wtr.getMessageCount() + 1) + ", counter: " + _counter +
                    ", session open: " + sess.isOpen() + ")");
            _wtr.addMessage(data);

            if (_wtr.limitReached()) {
                LOG.info(_wtr + " SimplePublisherTest limit reached, terminating");
                _wtr.terminateClient();
            }
            else {
                String nextMsg = _data[_counter++];
                LOG.info(_wtr + " SimplePublisherTest returning next message: " + nextMsg +
                        " (session still open: " + sess.isOpen() + ")");
                        // Thread.dumpStack();
                return nextMsg;
            }
            return null;
        }

        @OnOpen
        public void onOpen(Session sess) {
            LOG.info(_wtr + " SimplePublisherTest onOpen called, session: " + sess.getId());
            _wtr.connected();
            try {
                String s = _data[_counter++];
                LOG.info(_wtr + " SimplePublisherTest sending initial message: " + s);
                Thread.dumpStack();
                sess.getBasicRemote().sendText(s);
            } catch (Exception e) {
                LOG.severe(_wtr + " SimplePublisherTest error publishing initial message: " + e.getMessage());
                _wtr.addExceptionAndTerminate("Error publishing initial message", e);

            }
        }

    }

    @ClientEndpoint
    public static class NoPublishNoReceiveTest extends MultiClientEP {

        @OnOpen
        public void onOpen(Session sess) {
            _wtr.connected();

        }

    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        LOG.info(_wtr + " onClose called, reason: " + closeReason + ", session: " + session.getId());
        try {
            session.close();
        } catch (IOException e) {
            LOG.severe(_wtr + " Error closing session: " + e.getMessage());
            _wtr.addExceptionAndTerminate("Error closing session", e);
        }

    }

    @OnError
    public void onError(Session session, java.lang.Throwable throwable) {
        LOG.severe(_wtr + " onError called: " + throwable.getMessage());
        _wtr.addExceptionAndTerminate("Error during wsoc session", throwable);
    }

    @Override
    public void addTestResponse(WsocTestContext wtr) {
        _wtr = wtr;
    }

    @Override
    public WsocTestContext getTestResponse() {
        return _wtr;
    }

}
