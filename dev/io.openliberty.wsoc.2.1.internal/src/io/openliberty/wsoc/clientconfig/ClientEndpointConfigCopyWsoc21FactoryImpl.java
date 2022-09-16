package io.openliberty.wsoc.clientconfig;

import com.ibm.ws.wsoc.ClientEndpointConfigCopyFactory;

import jakarta.websocket.ClientEndpointConfig;

public class ClientEndpointConfigCopyWsoc21FactoryImpl implements ClientEndpointConfigCopyFactory {
    
    public ClientEndpointConfig getClientEndpointConfig(ClientEndpointConfig cec){
        return new ClientEndpointConfigCopyPerSession21(cec);
    }
}
