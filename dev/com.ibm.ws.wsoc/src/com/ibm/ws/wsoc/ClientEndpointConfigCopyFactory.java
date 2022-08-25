package com.ibm.ws.wsoc;

import javax.websocket.ClientEndpointConfig;

public interface ClientEndpointConfigCopyFactory {

    ClientEndpointConfig getClientEndpointConfig(ClientEndpointConfig cec);
    
}
