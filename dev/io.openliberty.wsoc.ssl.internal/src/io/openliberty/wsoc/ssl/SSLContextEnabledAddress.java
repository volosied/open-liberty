package io.openliberty.wsoc.ssl;

import javax.net.ssl.SSLContext;

public interface SSLContextEnabledAddress {

    SSLContext getSSLContext();
    
}
