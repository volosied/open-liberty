/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation and others.
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

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import io.openliberty.wsoc.common.Utils;

// import test.common.zos.ZFatNativeHelper;

/**
 *
 */
public class AnnotatedServerEP {

    @ServerEndpoint(value = "/annotatedByteArray/{boolean-var}")
    public static class ByteArrayTest extends AnnotatedServerEP {
        //test which shows boolean pair, session, @PathParam and actual message. Parameters can be in any order
        @OnMessage
        public byte[] echoText(boolean last, Session session, @PathParam("boolean-var") boolean booleanVar, byte[] data) { //session, msg and last can be at different param index
            if (session != null && last && booleanVar) {
                return data;
            } else {
                return null;
            }
        }

    }
}
