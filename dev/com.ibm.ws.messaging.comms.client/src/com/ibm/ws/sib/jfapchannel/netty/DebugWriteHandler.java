/*******************************************************************************
 * Copyright (c) 2022, 2025 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package com.ibm.ws.sib.jfapchannel.netty;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.jfapchannel.buffer.WsByteBuffer;
import com.ibm.ws.sib.jfapchannel.richclient.buffer.impl.RichByteBufferImpl;
import com.ibm.ws.sib.utils.ras.SibTr;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.nio.ByteBuffer;

/**
 * Debug handler to log all outbound writes before they go to the encoder.
 * This helps identify if messages are being written correctly and in what order.
 * Placed BEFORE the encoder to see WsByteBuffer objects.
 */
public class DebugWriteHandler extends ChannelOutboundHandlerAdapter {
    
    private static int messageCounter = 0;
    
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        String threadName = Thread.currentThread().getName();
        int msgNum = ++messageCounter;
        
        if (msg instanceof WsByteBuffer) {
            WsByteBuffer wsBuffer = (WsByteBuffer) msg;
            
            // Get the underlying ByteBuffer
            ByteBuffer byteBuffer = null;
            int length = 0;
            if (wsBuffer instanceof RichByteBufferImpl) {
                com.ibm.wsspi.bytebuffer.WsByteBuffer wrappedBuffer =
                    (com.ibm.wsspi.bytebuffer.WsByteBuffer)((RichByteBufferImpl)wsBuffer).getUnderlyingBuffer();
                byteBuffer = wrappedBuffer.getWrappedByteBuffer();
                length = byteBuffer.remaining();
            }
            
            StringBuilder output = new StringBuilder(256);
            output.append("WRITE_DEBUG: [").append(threadName).append("] ========== OUTBOUND WRITE #").append(msgNum).append(" ==========\n");
            output.append("WRITE_DEBUG: [").append(threadName).append("] Channel: ").append(ctx.channel().id().asShortText()).append("\n");
            output.append("WRITE_DEBUG: [").append(threadName).append("] Message type: WsByteBuffer\n");
            output.append("WRITE_DEBUG: [").append(threadName).append("] Bytes to write: ").append(length).append("\n");
            
            // Peek at first 16 bytes to check for JFAP eyecatcher
            if (byteBuffer != null && length >= 2) {
                int savedPos = byteBuffer.position();
                byte[] peek = new byte[Math.min(16, length)];
                byteBuffer.get(peek);
                byteBuffer.position(savedPos); // Reset position
                
                output.append("WRITE_DEBUG: [").append(threadName).append("] First ").append(peek.length).append(" bytes: ");
                for (int i = 0; i < peek.length; i++) {
                    output.append(String.format("%02X ", peek[i]));
                }
                output.append("\n");
                
                // Check for JFAP eyecatcher
                int eyecatcher = ((peek[0] & 0xFF) << 8) | (peek[1] & 0xFF);
                if (eyecatcher == 0xBEEF) {
                    output.append("WRITE_DEBUG: [").append(threadName).append("] ✓ JFAP message (eyecatcher 0xBEEF)\n");
                    
                    // Extract packet number if available (at offset 6-7 in JFAP header)
                    if (peek.length >= 8) {
                        int packetNum = ((peek[6] & 0xFF) << 8) | (peek[7] & 0xFF);
                        output.append("WRITE_DEBUG: [").append(threadName).append("] Packet number: ").append(packetNum).append("\n");
                    }
                } else {
                    output.append("WRITE_DEBUG: [").append(threadName).append("] ⚠️  NON-JFAP data (eyecatcher: 0x")
                          .append(Integer.toHexString(eyecatcher).toUpperCase()).append(")\n");
                }
            }
            
            output.append("WRITE_DEBUG: [").append(threadName).append("] ==========================================");
            System.out.println(output.toString());
            
            // Add listener to track write completion
            promise.addListener(future -> {
                if (future.isSuccess()) {
                    System.out.println("WRITE_DEBUG: [" + threadName + "] Write #" + msgNum + " completed successfully");
                } else {
                    System.err.println("WRITE_DEBUG: [" + threadName + "] Write #" + msgNum + " FAILED: " + future.cause());
                }
            });
        } else if (msg instanceof ByteBuf) {
            // Fallback for ByteBuf (shouldn't happen if placed before encoder)
            ByteBuf buf = (ByteBuf) msg;
            
            StringBuilder output = new StringBuilder(256);
            output.append("WRITE_DEBUG: [").append(threadName).append("] ========== OUTBOUND WRITE #").append(msgNum).append(" ==========\n");
            output.append("WRITE_DEBUG: [").append(threadName).append("] Channel: ").append(ctx.channel().id().asShortText()).append("\n");
            output.append("WRITE_DEBUG: [").append(threadName).append("] ByteBuf class: ").append(buf.getClass().getName()).append("\n");
            output.append("WRITE_DEBUG: [").append(threadName).append("] Bytes to write: ").append(buf.readableBytes()).append("\n");
            output.append("WRITE_DEBUG: [").append(threadName).append("] isDirect: ").append(buf.isDirect()).append("\n");
            output.append("WRITE_DEBUG: [").append(threadName).append("] refCnt: ").append(buf.refCnt()).append("\n");
            
            // Peek at first 16 bytes to check for JFAP eyecatcher
            int peekSize = Math.min(16, buf.readableBytes());
            if (peekSize >= 2) {
                byte[] peek = new byte[peekSize];
                buf.getBytes(buf.readerIndex(), peek);
                
                output.append("WRITE_DEBUG: [").append(threadName).append("] First ").append(peekSize).append(" bytes: ");
                for (int i = 0; i < peekSize; i++) {
                    output.append(String.format("%02X ", peek[i]));
                }
                output.append("\n");
                
                // Check for JFAP eyecatcher
                int eyecatcher = ((peek[0] & 0xFF) << 8) | (peek[1] & 0xFF);
                if (eyecatcher == 0xBEEF) {
                    output.append("WRITE_DEBUG: [").append(threadName).append("] ✓ JFAP message (eyecatcher 0xBEEF)\n");
                    
                    // Extract packet number if available (at offset 6-7 in JFAP header)
                    if (peekSize >= 8) {
                        int packetNum = ((peek[6] & 0xFF) << 8) | (peek[7] & 0xFF);
                        output.append("WRITE_DEBUG: [").append(threadName).append("] Packet number: ").append(packetNum).append("\n");
                    }
                } else {
                    output.append("WRITE_DEBUG: [").append(threadName).append("] Non-JFAP data (eyecatcher: 0x")
                          .append(Integer.toHexString(eyecatcher).toUpperCase()).append(")\n");
                }
            }
            
            output.append("WRITE_DEBUG: [").append(threadName).append("] ==========================================");
            System.out.println(output.toString());
            
            // Add listener to track write completion
            promise.addListener(future -> {
                if (future.isSuccess()) {
                    System.out.println("WRITE_DEBUG: [" + threadName + "] Write #" + msgNum + " completed successfully");
                } else {
                    System.err.println("WRITE_DEBUG: [" + threadName + "] Write #" + msgNum + " FAILED: " + future.cause());
                }
            });
        }
        
        // Pass the write to the next handler
        super.write(ctx, msg, promise);
    }
    
    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        String threadName = Thread.currentThread().getName();
        System.out.println("WRITE_DEBUG: [" + threadName + "] ========== FLUSH called ==========");
        super.flush(ctx);
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("WRITE_DEBUG: Exception in write handler: " + cause.getMessage());
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }
}

// Made with Bob
