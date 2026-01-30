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
import com.ibm.ws.sib.utils.ras.SibTr;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Debug handler to inspect SSL handler output before it reaches the decoder.
 * This helps identify if corruption occurs during SSL decryption or buffer handling.
 *
 * Place this handler in the pipeline AFTER the SSL handler and BEFORE NettyToWsBufferDecoder.
 */
public class DebugSSLOutputHandler extends ChannelInboundHandlerAdapter {
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf) msg;
            String threadName = Thread.currentThread().getName();
            StringBuilder output = new StringBuilder(512);
            
            // Capture buffer identity for tracking across handlers
            String bufferIdentity = System.identityHashCode(buf) + "@" +
                                    (buf.hasMemoryAddress() ? "0x" + Long.toHexString(buf.memoryAddress()) : "no-addr");
            
            output.append("SSL_DEBUG: [").append(threadName).append("] ========== SSL HANDLER OUTPUT ==========\n");
            output.append("SSL_DEBUG: [").append(threadName).append("] Channel: ").append(ctx.channel().id().asShortText()).append("\n");
            output.append("SSL_DEBUG: [").append(threadName).append("] Buffer Identity: ").append(bufferIdentity).append("\n");
            output.append("SSL_DEBUG: [").append(threadName).append("] ByteBuf class: ").append(buf.getClass().getName()).append("\n");
            output.append("SSL_DEBUG: [").append(threadName).append("] readerIndex: ").append(buf.readerIndex()).append("\n");
            output.append("SSL_DEBUG: [").append(threadName).append("] writerIndex: ").append(buf.writerIndex()).append("\n");
            output.append("SSL_DEBUG: [").append(threadName).append("] readable: ").append(buf.readableBytes()).append("\n");
            output.append("SSL_DEBUG: [").append(threadName).append("] capacity: ").append(buf.capacity()).append("\n");
            output.append("SSL_DEBUG: [").append(threadName).append("] isDirect: ").append(buf.isDirect()).append("\n");
            output.append("SSL_DEBUG: [").append(threadName).append("] refCnt: ").append(buf.refCnt()).append("\n");
            
            // Check if this is a slice or wrapped buffer
            String className = buf.getClass().getName();
            if (className.contains("Slice") || className.contains("Duplicate") ||
                className.contains("Wrapped") || className.contains("Unrel")) {
                output.append("SSL_DEBUG: [").append(threadName).append("] ⚠️  SLICE/WRAPPED BUFFER DETECTED!\n");
                output.append("SSL_DEBUG: [").append(threadName).append("] This may indicate incorrect buffer handling!\n");
            }
            
            // Check if readerIndex is not at start
            if (buf.readerIndex() != 0) {
                output.append("SSL_DEBUG: [").append(threadName).append("] ⚠️  BUFFER READER INDEX NOT AT START!\n");
                output.append("SSL_DEBUG: [").append(threadName).append("] Something already read from this buffer!\n");
            }
            
            // Peek at first 16 bytes WITHOUT advancing reader
            int peekSize = Math.min(16, buf.readableBytes());
            if (peekSize > 0) {
                byte[] peek = new byte[peekSize];
                buf.getBytes(buf.readerIndex(), peek);
                
                output.append("SSL_DEBUG: [").append(threadName).append("] First ").append(peekSize).append(" bytes: ");
                for (int i = 0; i < peekSize; i++) {
                    output.append(String.format("%02X ", peek[i]));
                }
                output.append("\n");
                
                // Check for JFAP eyecatcher (0xBEEF)
                if (peekSize >= 2) {
                    int eyecatcher = ((peek[0] & 0xFF) << 8) | (peek[1] & 0xFF);
                    if (eyecatcher == 0xBEEF) {
                        output.append("SSL_DEBUG: [").append(threadName).append("] ✓ Eyecatcher 0xBEEF found at position 0\n");
                        output.append("SSL_DEBUG: [").append(threadName).append("] SSL decryption output looks correct!\n");
                        output.append("SSL_DEBUG: [").append(threadName).append("] ==========================================");
                        System.out.println(output.toString());
                    } else {
                        // Corruption detected - build error output
                        StringBuilder errorOutput = new StringBuilder(512);
                        errorOutput.append(output); // Include all the info we've gathered
                        errorOutput.append("SSL_DEBUG: [").append(threadName).append("] ✗ EYECATCHER MISSING! Found: 0x")
                                  .append(Integer.toHexString(eyecatcher).toUpperCase()).append("\n");
                        errorOutput.append("SSL_DEBUG: [").append(threadName).append("] ⚠️  CORRUPTION DETECTED IN SSL OUTPUT!\n");
                        errorOutput.append("SSL_DEBUG: [").append(threadName).append("] Expected 0xBEEF at start of JFAP message\n");
                        
                        // Dump more bytes for analysis
                        int dumpSize = Math.min(32, buf.readableBytes());
                        byte[] dump = new byte[dumpSize];
                        buf.getBytes(buf.readerIndex(), dump);
                        errorOutput.append("SSL_DEBUG: [").append(threadName).append("] First 32 bytes:\n");
                        errorOutput.append("                                ");
                        for (int i = 0; i < dumpSize; i++) {
                            errorOutput.append(String.format("%02X ", dump[i]));
                            if ((i + 1) % 16 == 0 && i < dumpSize - 1) {
                                errorOutput.append("\n                                ");
                            }
                        }
                        errorOutput.append("\n");
                        
                        // Check if eyecatcher might be at a different offset
                        for (int offset = 1; offset < Math.min(16, dumpSize - 1); offset++) {
                            int offsetEyecatcher = ((dump[offset] & 0xFF) << 8) | (dump[offset + 1] & 0xFF);
                            if (offsetEyecatcher == 0xBEEF) {
                                errorOutput.append("SSL_DEBUG: [").append(threadName).append("] ⚠️  Found 0xBEEF at offset ")
                                          .append(offset).append("!\n");
                                errorOutput.append("SSL_DEBUG: [").append(threadName)
                                          .append("] Buffer slice may have wrong starting position!\n");
                                break;
                            }
                        }
                        errorOutput.append("SSL_DEBUG: [").append(threadName).append("] ==========================================");
                        System.err.println(errorOutput.toString());
                    }
                }
            } else {
                output.append("SSL_DEBUG: [").append(threadName).append("] Buffer is empty (0 readable bytes)\n");
                output.append("SSL_DEBUG: [").append(threadName).append("] ==========================================");
                System.out.println(output.toString());
            }
        }
        
        // Pass the message to the next handler (NettyToWsBufferDecoder)
        super.channelRead(ctx, msg);
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("SSL_DEBUG: Exception in SSL output handler: " + cause.getMessage());
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }
}

// Made with Bob
