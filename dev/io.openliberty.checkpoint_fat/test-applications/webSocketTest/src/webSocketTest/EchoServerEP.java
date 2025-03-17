package webSocketTest;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.SendHandler;
import javax.websocket.SendResult;
import javax.websocket.Session;

import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/echo")
public class EchoServerEP {

    @OnOpen
    public void onOpen(final Session session) {

    }

    @OnMessage
    public void onMsg(String msg, Session session) {
        System.out.println("echo: " + msg);
        session.getBasicRemote().send("echo: " + msg);
    }

}

