package com.ibm.ws.wsoc.outbound;

import java.net.URI;

import com.ibm.wsspi.http.channel.outbound.HttpAddress;

public interface WsocAddress extends HttpAddress {
    
    String getChainKey();

    void validateURI();
        
    boolean isSecure();
        
    URI getURI();
        
    String getPath();
}
