/*******************************************************************************
 * Copyright (c) 2013, 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package io.openliberty.wsoc.endpoints.client.basic;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.PongMessage;
import jakarta.websocket.SendHandler;
import jakarta.websocket.SendResult;
import jakarta.websocket.Session;

import io.openliberty.wsoc.util.wsoc.TestHelper;
import io.openliberty.wsoc.util.wsoc.WsocTestContext;
import io.openliberty.wsoc.common.Constants;

import io.openliberty.wsoc.common.Utils;
import io.openliberty.wsoc.tests.all.AnnotatedTest;

/**
 *
 */
public class TimeOutClientEP implements TestHelper {


    public WsocTestContext _wtr = null;
    private static final Logger LOG = Logger.getLogger(TimeOutClientEP.class.getName());
    protected boolean EXPECT_TIMEOUT_ERROR = false;
    private final String CLOSE_1006_ERROR_EXCEPTION = "org.eclipse.jetty.websocket.api.ProtocolException: Frame forbidden close status code: 1006";

    @ClientEndpoint
    public static class TimeOutTest extends TimeOutClientEP {

        public String[] _data = {};

        public TimeOutTest(String[] data) {
            _data = data;
            // this.EXPECT_TIMEOUT_ERROR = true;
        }

        @OnOpen
        public void onOpen(Session sess) {
            try {
                sess.getBasicRemote().sendText(_data[0]);
            } catch (Exception e) {
                //TODO: handle exception
            }
        }

        @OnMessage
        public String echoText(String data) {
    
            _wtr.addMessage(data);
    
            _wtr.terminateClient();
    
            return null;
        }



    }

    @OnError
    public void onError(Session session, java.lang.Throwable throwable) {
        // Client automatically throws an error when a 1006 response is added. Verify if this
        // is what is happening on this client and do not throw error when it does since as seen above
        // on the onClose we expect it to behave this way and need to verify it
        LOG.warning(throwable.toString());
        if (this.EXPECT_TIMEOUT_ERROR && throwable.toString().equals(CLOSE_1006_ERROR_EXCEPTION))
            LOG.info("Skipping error when receiving a 1006 response since it is expected.");
        else
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
