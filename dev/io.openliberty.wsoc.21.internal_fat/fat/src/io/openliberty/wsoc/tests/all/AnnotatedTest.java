/*******************************************************************************
 * Copyright (c) 2013, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package io.openliberty.wsoc.tests.all;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import io.openliberty.wsoc.common.Constants;
import io.openliberty.wsoc.common.Utils;
import io.openliberty.wsoc.endpoints.client.basic.AnnotatedClientEP;
import io.openliberty.wsoc.util.wsoc.TestWsocContainer;
import io.openliberty.wsoc.util.wsoc.WsocTest;
import junit.framework.Assert;

/**
 * Tests WebSocket Stuff
 *
 * @author unknown
 */
public class AnnotatedTest {

    private WsocTest wsocTest = null;

    public AnnotatedTest(WsocTest test) {
        this.wsocTest = test;
    }

    /*
     * ServerEndpoint - @see AnnotatedServerEP - ByteBufferTest
     */
    public void testAnnotatedByteArraySuccess() throws Exception {

        byte[][] data = Utils.getRandomBinaryByteArray(5, 100);
        byte[][] orig = Utils.duplicateByteArray(data);
        //server endpoint uri is /annotatedByteArray/{boolean-var}
        String uri = "/basic/annotatedByteArray/true";
        wsocTest.runEchoTest(new AnnotatedClientEP.ByteArrayTest(data), uri, orig);

    }
}