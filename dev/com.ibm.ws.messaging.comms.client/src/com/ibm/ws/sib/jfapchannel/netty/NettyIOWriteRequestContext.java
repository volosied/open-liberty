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

import java.io.IOException;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.sib.jfapchannel.JFapChannelConstants;
import com.ibm.ws.sib.jfapchannel.buffer.WsByteBuffer;
import com.ibm.ws.sib.jfapchannel.framework.IOWriteCompletedCallback;
import com.ibm.ws.sib.jfapchannel.framework.IOWriteRequestContext;
import com.ibm.ws.sib.jfapchannel.framework.NetworkConnection;
import com.ibm.ws.sib.jfapchannel.impl.Connection;
import com.ibm.ws.sib.jfapchannel.impl.NettyConnectionWriteCompletedCallback;
import com.ibm.ws.sib.utils.ras.SibTr;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

/**
 * An implementation of com.ibm.ws.sib.jfapchannel.framework.IOReadRequestContext. It
 * basically wraps NettyNetworkConnection code making use of the
 * underlying Channel object for running write requests.
 *
 * @see com.ibm.ws.sib.jfapchannel.framework.IOReadRequestContext
 *
 */
public class NettyIOWriteRequestContext extends NettyIOBaseContext implements IOWriteRequestContext{

	/** Trace */
	private static final TraceComponent tc = SibTr.register(NettyIOWriteRequestContext.class,
			JFapChannelConstants.MSG_GROUP,
			JFapChannelConstants.MSG_BUNDLE);

	private WsByteBuffer buffer = null;

	/** Log class info on load */
	static
	{
		if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(tc, "@(#) SIB/ws/code/sib.jfapchannel.client.rich.impl/src/com/ibm/ws/sib/netty/jfapchannel/NettyIOWriteRequestContext.java, SIB.comms, WASX.SIB, uu1215.01 1.5");
	}


	/**
	 * @param conn
	 */
	public NettyIOWriteRequestContext(NettyNetworkConnection conn)
	{
		super(conn);

		if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "<init>", new Object[] { conn });
		if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(tc, "<init>");
	}


	public NetworkConnection write(WsByteBuffer buffer, final NettyConnectionWriteCompletedCallback completionCallback) {
		if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "write",
				new Object[]{buffer, completionCallback});
		NetworkConnection retConn = null;
		final IOWriteRequestContext me = this;

		Channel chan = this.conn.getVirtualConnection();
		
		if(chan.isActive()) {
			
			// FIX: Ensure writes are serialized through the EventLoop to prevent packet reordering
			// This is critical for Epoll which can have concurrent writes from multiple threads
			if (chan.eventLoop().inEventLoop()) {
				// Already in the EventLoop thread, write directly
				ChannelFuture future = chan.writeAndFlush(buffer, chan.newPromise().addListener(f -> {
					if (f.isDone() && f.isSuccess()) {
						if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "Succesful write for "+chan);
						completionCallback.complete(getNetworkConnectionInstance(chan), me);
					} else {
						if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "Unsuccesful write", new Object[]{chan, f.cause()});
						completionCallback.error(getNetworkConnectionInstance(chan), me, new IOException(f.cause()));
						if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "Unsuccesful write");
					}
				}));

				if(future.isDone()) {
					retConn = getNetworkConnectionInstance(chan);
				}
			} else {
				// Not in EventLoop thread, submit write to EventLoop to ensure ordering
				chan.eventLoop().execute(() -> {
					chan.writeAndFlush(buffer, chan.newPromise().addListener(f -> {
						if (f.isDone() && f.isSuccess()) {
							if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) SibTr.debug(this, tc, "Succesful write for "+chan);
							completionCallback.complete(getNetworkConnectionInstance(chan), me);
						} else {
							if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "Unsuccesful write", new Object[]{chan, f.cause()});
							completionCallback.error(getNetworkConnectionInstance(chan), me, new IOException(f.cause()));
							if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "Unsuccesful write");
						}
					}));
				});
				// When submitting to EventLoop, we can't return a connection synchronously
				// The write will complete asynchronously via the callback
			}
			
		}else {
			// Channel is not active - this can happen during normal shutdown when TCP channels
			// are stopped before messaging engine completes shutdown.
			// During shutdown, the channel may be closed before the Connection is linked to it,
			// or the connection may already be closed. In either case, don't generate FFDC.
			Connection connection = chan.attr(NettyNetworkConnectionFactory.CONNECTION).get();
			
			// Debug logging to understand the state
			if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
				SibTr.debug(this, tc, "Channel is not active. Channel: " + chan +
					", Connection: " + connection +
					", Connection.isClosed(): " + (connection != null ? connection.isClosed() : "N/A"));
			}
			
			// Check if this is a shutdown scenario:
			// 1. Connection is null (channel closed during shutdown before connection was linked)
			// 2. Connection exists and is closed (normal shutdown sequence)
			if (connection == null || connection.isClosed()) {
				// This is expected during shutdown - just log at debug level
				if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
					SibTr.debug(this, tc, "Write skipped - channel is not active during shutdown. " +
						"Connection: " + (connection == null ? "null (not yet linked)" : "closed") +
						", Channel: " + chan);
				}
				// Don't call error callback to avoid FFDC during normal shutdown
			} else {
				// Unexpected inactive channel during normal operation - report the error
				if (TraceComponent.isAnyTracingEnabled() && tc.isDebugEnabled()) {
					SibTr.debug(this, tc, "Reporting error - connection exists but is not closed (unexpected state)");
				}
				completionCallback.error(getNetworkConnectionInstance(chan), me, new IOException("Write was attempted on a channel that is not active!! " + chan));
			}
		}

		if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "write", retConn);
		return retConn;

	}

	/**
	 *
	 * @see com.ibm.ws.sib.jfapchannel.framework.IOWriteRequestContext#write(int, com.ibm.ws.sib.jfapchannel.framework.IOWriteCompletedCallback, boolean, int)
	 */
	public NetworkConnection write(int amountToWrite, final IOWriteCompletedCallback completionCallback,
			boolean queueRequest, int timeout)
	{
		if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.entry(this, tc, "write",
				new Object[]{amountToWrite, completionCallback, queueRequest, timeout});
		if (TraceComponent.isAnyTracingEnabled() && tc.isEntryEnabled()) SibTr.exit(this, tc, "write: Not supported for Netty");
		throw new UnsupportedOperationException("Not currently supported for Netty. Please use other write method.");

	}

	public WsByteBuffer getBuffer(){
		return this.buffer;
	}

	public void setBuffer(WsByteBuffer buffer){
		this.buffer = buffer;
	}

}
