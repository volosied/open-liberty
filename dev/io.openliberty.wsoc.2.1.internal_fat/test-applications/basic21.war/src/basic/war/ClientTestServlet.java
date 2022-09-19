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
package basic.war;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.security.SecureRandom;

import javax.net.ssl.SSLContext;

import jakarta.servlet.http.HttpServlet;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.WebSocketContainer;
import jakarta.websocket.ClientEndpointConfig;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.servlet.ServletException;

public class ClientTestServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        boolean success = true;
        String reasonForFailure = "";
        
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getDefault();
            sslContext.init(null, null, new SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
            success = false; 
            reasonForFailure = e.getMessage();
        }

        ClientEndpointConfig cec  = ClientEndpointConfig.Builder.create().sslContext(sslContext).build();
        try{
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(new EchoClientEP(), cec, buildFullURI(req));
        } catch(Exception ex){
            ex.printStackTrace();
            success = false; 
            reasonForFailure = ex.getMessage();
        }

        PrintWriter printWriter = resp.getWriter();

        if(success){
            printWriter.print("SUCCESS!");
        } else {
            printWriter.print("FAILURE! -> " + reasonForFailure);
        }

        printWriter.close();
    }

    /* Copied from com.ibm.ws.wsoc.HandshakeProcessor.java  */
    private URI buildFullURI(HttpServletRequest req) throws Exception {
        StringBuilder builder = new StringBuilder();

        String url = req.getRequestURL().toString();
        String https = "https";
        String http = "http";

        if (url.startsWith(https)) {
            url = "wss" + url.substring(https.length(), url.length());
        } else if (url.startsWith(http)) {
            url = "ws" + url.substring(http.length(), url.length());
        }

        if (!(url.startsWith("ws:") || url.startsWith("wss:"))) {
            throw new Exception("Scheme is not of type ws or wss.");
        }

        //Server Endpoint is echo 
        url = url.replace("ClientTestServlet", "echo");

        builder.append(url);

        return new URI(builder.toString());

    }

}
