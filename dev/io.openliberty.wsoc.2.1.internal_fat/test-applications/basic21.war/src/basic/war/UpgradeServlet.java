
/*******************************************************************************
 * Copyright (c) 2014 IBM Corporation and others.
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.server.ServerContainer;

import jakarta.websocket.DeploymentException;

/**
 * Upgrade to ws using the Websocket 2.1 ServerContainer#upgradeHttpToWebSocket method
 */
public class UpgradeServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
                    throws ServletException, IOException {

        ServerContainer container = (ServerContainer) req.getServletContext().getAttribute("jakarta.websocket.server.ServerContainer");
        try{
            container.upgradeHttpToWebSocket(req, resp, new UpgradeServletPathEndpointConfig(), Collections.emptyMap());
        } catch(DeploymentException ex){

        }

        // Our response should be committed by now
        if (resp.getStatus() != 101) {
            resp.setStatus(500);
        }
    }

}
