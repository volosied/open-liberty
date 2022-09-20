package com.ibm.ws.channel.ssl.internal.wsoc;

import javax.net.ssl.SSLContext;

public interface SSLEnabledAddress {

    SSLContext getSSLContext();
    
}
