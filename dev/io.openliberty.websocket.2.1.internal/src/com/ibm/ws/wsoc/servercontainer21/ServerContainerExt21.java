/*******************************************************************************
 * Copyright (c) 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.wsoc.servercontainer21;

import java.io.IOException;
import java.util.Map;

import java.lang.UnsupportedOperationException;
import java.lang.IllegalStateException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.websocket.server.ServerEndpointConfig;

import com.ibm.websphere.wsoc.WsWsocServerContainer;
import com.ibm.ws.wsoc.servercontainer.ServerContainerExt;

import org.osgi.service.component.annotations.Component;

import com.ibm.ws.webcontainer.servlet.WsocHandler;

import jakarta.websocket.server.ServerContainer;

import jakarta.websocket.*;

import jakarta.websocket.DeploymentException;
import org.osgi.service.component.annotations.Component;


public class ServerContainerExt21 extends ServerContainerExt implements ServerContainer {

    /*
     * Since Websocket 2.1
     */
   // public void doUpgrade(HttpServletRequest request, HttpServletResponse response, ServerEndpointConfig endpointConfig, Map<String, String> pathParams) throws ServletException, IOException {

    @Override
    public void upgradeHttpToWebSocket(Object httpServletRequest, Object httpServletResponse, ServerEndpointConfig sec,
            Map<String, String> pathParameters) throws IOException, DeploymentException {

        wsocUpgradeHandler.handleRequest( ((HttpServletRequest) httpServletRequest), ((HttpServletResponse) httpServletResponse), sec, pathParameters, true);
        
        if (!((HttpServletResponse) httpServletResponse).isCommitted()) {
            ((HttpServletResponse) httpServletResponse).getOutputStream().close();
        }

    }

    @Override
    public void addEndpoint(Class<?> endpointClass) throws DeploymentException {
        super.addEndpoint(endpointClass);
    }

    @Override
    public void addEndpoint(ServerEndpointConfig serverConfig) throws DeploymentException {
        super.addEndpoint(serverConfig);
    };

}
