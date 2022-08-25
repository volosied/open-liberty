package com.ibm.ws.wsoc.outbound;

import com.ibm.ws.wsoc.outbound.WsocAddress;
import javax.websocket.ClientEndpointConfig;
import com.ibm.ws.wsoc.ParametersOfInterest;

public interface HttpRequestorFactory {
    
    HttpRequestor getHttpRequestor(WsocAddress endpointAddress, ClientEndpointConfig config, ParametersOfInterest things);
}
