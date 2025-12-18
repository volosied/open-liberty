/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
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
package com.ibm.ws.wsoc;

import java.io.IOException;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ras.annotation.Sensitive;
import com.ibm.websphere.ras.annotation.Trivial;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.wsspi.bytebuffer.WsByteBuffer;
import com.ibm.wsspi.tcpchannel.TCPWriteRequestContext;

public class MessageWriter {

    private static final TraceComponent tc = Tr.register(MessageWriter.class);

    WsByteBuffer[] messageBuffers = null;
    FrameWriteProcessor frameWriteProcessor = null;

    WsocWriteCallback callback = null;
    TCPWriteRequestContext tcpWriteContext = null;
    boolean shouldMaskData = false;

    @Trivial
    public static enum WRITE_TYPE {
        SYNC, ASYNC
    };

    public MessageWriter() {}

    public void initialize(TCPWriteRequestContext _wrc, WsocWriteCallback _cb, boolean _shouldMaskData) {
        tcpWriteContext = _wrc;
        callback = _cb;
        shouldMaskData = _shouldMaskData;
    }

    public void WriteMessage(@Sensitive WsByteBuffer buffer, OpcodeType opcode, int timeout, WRITE_TYPE type) throws IOException {

        WsByteBuffer[] buffers = null;
        if (buffer != null) {
            buffers = new WsByteBuffer[1];
            buffers[0] = buffer;
        }

        WriteMessage(buffers, opcode, timeout, type);
    }

    @FFDCIgnore(IOException.class)
    public void WriteMessage(@Sensitive WsByteBuffer[] buffers, OpcodeType opcode, int timeout, WRITE_TYPE type) throws IOException {

        if ((buffers == null) && (timeout != TCPWriteRequestContext.IMMED_TIMEOUT)) {
            System.out.println("MessageWriter.WriteMessage: buffers is null, returning early");
            return;
        }

        int totalBytes = 0;
        if (buffers != null) {
            for (WsByteBuffer buf : buffers) {
                if (buf != null) {
                    totalBytes += buf.remaining();
                }
            }
        }
        System.out.println("MessageWriter.WriteMessage: opcode=" + opcode + ", type=" + type + ", timeout=" + timeout +
                ", bufferCount=" + (buffers != null ? buffers.length : 0) + ", totalBytes=" + totalBytes);

        if (frameWriteProcessor == null) {
            frameWriteProcessor = new FrameWriteProcessor();
        }

        messageBuffers = frameWriteProcessor.formatForFrameMessage(buffers, opcode, shouldMaskData);

        tcpWriteContext.setBuffers(messageBuffers);

        if (type == WRITE_TYPE.SYNC) {
            // sync write
            try {
                WriteMessageSync(timeout);
                System.out.println("MessageWriter.WriteMessage: SYNC write completed successfully");

            } catch (IOException x) {
                System.out.println("MessageWriter.WriteMessage: SYNC write failed with IOException: " + x.getMessage());
                x.printStackTrace();
                frameCleanup();
                throw x;
            }

            frameCleanup();

        } else {
            // async write
            System.out.println("MessageWriter.WriteMessage: Starting ASYNC write");
            WriteMessageASync(timeout);
            // clean up processor in write complete/error callback
        }
    }

    public void frameCleanup() {
        frameWriteProcessor.cleanup();
    }

    public void cancelMessageAsync() throws IOException {

        // sending a write with an "immediate timeout" will attempt to immediately timeout the outstanding write.
        // we then need to be smart enough here and elsewhere to realize this is not a real "timeout" but a cancel.

        tcpWriteContext.write(0, TCPWriteRequestContext.IMMED_TIMEOUT);

    }

    private void WriteMessageSync(int timeout) throws IOException {

        // write all data, will get a timeout/IOException if not all data has been written before the timeout

        // Special Debug Utils.printOutBuffers(tcpWriteContext.getBuffers());

        System.out.println("MessageWriter.WriteMessageSync: calling tcpWriteContext.write with timeout=" + timeout);
        tcpWriteContext.write(TCPWriteRequestContext.WRITE_ALL_DATA, timeout);
        System.out.println("MessageWriter.WriteMessageSync: tcpWriteContext.write completed");
    }

    private void WriteMessageASync(int timeout) {

        if (callback == null) {
            callback = new WsocWriteCallback();
        }

        System.out.println("MessageWriter.WriteMessageASync: calling tcpWriteContext.write with timeout=" + timeout);
        // write all data, set forceQueue to true, so response will always come back on a different thread via the callback complete/error
        tcpWriteContext.write(TCPWriteRequestContext.WRITE_ALL_DATA, callback, true, timeout);
    }

}
