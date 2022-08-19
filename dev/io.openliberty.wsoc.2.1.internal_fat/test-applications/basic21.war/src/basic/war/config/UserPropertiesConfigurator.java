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
package basic.war.config;

import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;
import java.util.Map;

/* 
 * Based off TCK: https://github.com/eclipse-ee4j/jakartaee-tck/pull/783
 */
public class UserPropertiesConfigurator extends ServerEndpointConfig.Configurator {

  public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {

    Map<String, Object> userProperties = sec.getUserProperties();

    if (userProperties.size() != 1) {
      throw new IllegalStateException("User properties map size differs. Expected: 1, Actual: " + userProperties.size());
    }

    if (!userProperties.containsKey("SERVER-1")) {
      throw new IllegalStateException("User properties map is missing key: SERVER-1");
    }

    userProperties.put("MODIFY-1", new Object());
  }

}
