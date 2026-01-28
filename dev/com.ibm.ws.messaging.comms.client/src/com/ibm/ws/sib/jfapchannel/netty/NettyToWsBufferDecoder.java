/*******************************************************************************
 * Copyright (c) 2022, 2023 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package com.ibm.ws.sib.jfapchannel.netty;

import java.nio.charset.StandardCharsets;
import java.util.List;

import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.jfapchannel.buffer.WsByteBuffer;
import com.ibm.ws.sib.jfapchannel.buffer.WsByteBufferPool;
import com.ibm.ws.sib.utils.ras.SibTr;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * Netty decoder for transforming incoming ByteBuf objects to WsByteBuffer objects
 * we work with in the JMS bundles.
 * 
 */
public class NettyToWsBufferDecoder extends ByteToMessageDecoder {

	/** Trace */
	private static final TraceComponent tc = SibTr.register(NettyToWsBufferDecoder.class,
			JFapChannelConstants.MSG_GROUP,
			JFapChannelConstants.MSG_BUNDLE);

	/** Log class info on load */
	static
	{
		if (tc.isDebugEnabled())
			SibTr.debug(tc,
					"@(#) SIB/ws/code/sib.jfapchannel.client.rich.impl/src/com/ibm/ws/sib/netty/jfapchannel/NettyToWsBufferDecoder.java, SIB.comms, WASX.SIB, uu1215.01 1.1");
	}


	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

		if (tc.isEntryEnabled())
			SibTr.entry(this, tc, "decode", ctx.channel());

		if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
			SibTr.debug(this, tc, "decode", ctx.channel().remoteAddress() + " decoding message [ " + in.toString(StandardCharsets.UTF_8) + " ] from Netty ByteBuf to WSByteBuffer");
		}

		// Read all available bytes from the input buffer
		int length = in.readableBytes();
		
		// Skip empty buffers
		if (length == 0) {
			if (tc.isEntryEnabled())
				SibTr.exit(this, tc, "decode", "empty buffer, skipping");
			return;
		}

		System.out.println("FIX_V6: NettyToWsBufferDecoder.decode() length=" + length);

		// Always copy to a new byte array to avoid buffer lifecycle issues
		// This ensures the data is preserved even after the ByteBuf is released by Netty
		byte[] bytes = new byte[length];
		in.readBytes(bytes);
		
		System.out.println("FIX_V6: Copied " + length + " bytes to new array");

		// Wrap the byte array in a WsByteBuffer
		WsByteBuffer wsBuffer = WsByteBufferPool.getInstance().wrap(bytes, 0, length);
		System.out.println("FIX_V6: Created WsByteBuffer with wrap(bytes, 0, " + length + ")");
		System.out.println("FIX_V6: Initial state - pos=" + wsBuffer.position() + ", lim=" + wsBuffer.limit() + ", cap=" + wsBuffer.capacity());
		
		// CRITICAL: NettyConnectionReadCompletedCallback calls flip() on the buffer!
		// We need to set it to "write mode" so flip() converts it to correct read mode
		// Write mode: pos=length, lim=capacity
		// After flip(): pos=0, lim=length (correct for reading)
		wsBuffer.position(length);
		wsBuffer.limit(wsBuffer.capacity());
		
		System.out.println("FIX_V6: Before flip (write mode) - pos=" + wsBuffer.position() + ", lim=" + wsBuffer.limit() + ", cap=" + wsBuffer.capacity());
		out.add(wsBuffer);

		if (tc.isEntryEnabled())
			SibTr.exit(this, tc, "decode", ctx.channel());
	}


	@Override
	public void decodeLast(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

		if (tc.isEntryEnabled())
			SibTr.entry(this, tc, "decodeLast", ctx.channel());

		if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
			SibTr.debug(this, tc, "decodeLast", ctx.channel().remoteAddress() + " calling decode ");
		}
		decode(ctx, in, out);
		if (tc.isEntryEnabled())
			SibTr.exit(this, tc, "decodeLast", ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (tc.isEntryEnabled())
			SibTr.entry(this, tc, "exceptionCaught", new Object[] {cause, ctx.channel()});
		super.exceptionCaught(ctx, cause);
		if (tc.isEntryEnabled())
			SibTr.exit(this, tc, "exceptionCaught", new Object[] {cause, ctx.channel()});
	}

}

// Made with Bob
