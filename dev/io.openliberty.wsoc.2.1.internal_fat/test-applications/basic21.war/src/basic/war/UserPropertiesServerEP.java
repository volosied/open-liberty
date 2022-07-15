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

import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import java.io.IOException;
import java.util.Map;

/*
 * Based off TCK: https://github.com/eclipse-ee4j/jakartaee-tck/pull/783
 */
public class UserPropertiesServerEP extends Endpoint implements MessageHandler.Whole<String> {

  Session session;

  public void onOpen(Session session, EndpointConfig config) {
    this.session = session;
    // UserPropertiesServerEP extends MessageHandler so we can reference session from onMessage
    session.addMessageHandler(this);
  }

  public void onMessage(String msg) {

    Map<String, Object> userProperties = this.session.getUserProperties();
    if (userProperties.size() != 3) {
      throw new IllegalStateException("User properties map size differs. Expected: 3, Actual: " + userProperties.size());
    }

    checkKey(userProperties, "SERVER-1");
    checkKey(userProperties, "SERVER-2");
    checkKey(userProperties, "MODIFY-1");

    try {
      this.session.getBasicRemote().sendText("PASSES");
    } catch (Exception e) {
      e.printStackTrace();
    }

    // Modify user properties to test if they do not affect other endpoint sessions
    userProperties.remove("SERVER-2");
    userProperties.put("MODIFY-2", new Object());
  }

  private void checkKey(Map<String, Object> map, String key) {
    if (!map.containsKey(key))
      throw new IllegalStateException("User properties map is missing entry with key [" + key + "]");
  }

  public void onError(Session session, Throwable error) {
    error.printStackTrace();
  }
}
