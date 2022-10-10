package com.ibm.ws.wsoc.outbound;

import com.ibm.ws.wsoc.outbound.HttpRequestorFactory;

import com.ibm.ws.wsoc.outbound.HttpRequestor;

import com.ibm.ws.wsoc.outbound.WsocAddress;
import javax.websocket.ClientEndpointConfig;
import com.ibm.ws.wsoc.ParametersOfInterest;

public class HttpRequestorWsoc10FactoryImpl implements HttpRequestorFactory {
    
    public HttpRequestor getHttpRequestor(WsocAddress endpointAddress, ClientEndpointConfig config, ParametersOfInterest things){
        return new HttpRequestorWsoc10(endpointAddress, config, things);
    }
}
