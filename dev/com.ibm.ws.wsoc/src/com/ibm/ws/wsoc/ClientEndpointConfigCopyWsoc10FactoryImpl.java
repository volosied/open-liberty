package com.ibm.ws.wsoc;

import javax.websocket.ClientEndpointConfig;

public class ClientEndpointConfigCopyWsoc10FactoryImpl implements ClientEndpointConfigCopyFactory {
    
    public ClientEndpointConfig getClientEndpointConfig(ClientEndpointConfig cec){
        return new ClientEndpointConfigCopyPerSession10(cec);
    }
}
