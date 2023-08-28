package org.apache.taglibs.standard.tag.common.xml;

import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Utility to support system property org.apache.taglibs.standard.xml.accessExternalEntity which enhances security.
 */
public class XmlSecurityUtil {
    
    private static final String SP_ALLOWED_PROTOCOLS = "org.apache.taglibs.standard.xml.accessExternalEntity";
    private static final String ALLOWED_PROTOCOLS = initAllowedProtocols();
    
    private static String initAllowedProtocols() {
        final String defaultProtocols = "";
        if (System.getSecurityManager() == null) {
            return System.getProperty(SP_ALLOWED_PROTOCOLS, defaultProtocols);
        } else {
            try {
                return AccessController.doPrivileged(new PrivilegedAction<String>() {
                    public String run() {
                        return System.getProperty(SP_ALLOWED_PROTOCOLS, defaultProtocols);
                    }
                });
            } catch (AccessControlException e) {
                // Fall back to the default i.e. none.
                return defaultProtocols;
            }
        }
    }
    
    public static void checkProtocol(String uri) {
        if ("all".equalsIgnoreCase(ALLOWED_PROTOCOLS)) {
            return;
        }
        String protocol = getScheme(uri);
        for (String allowed : ALLOWED_PROTOCOLS.split(",")) {
            if (allowed.trim().equalsIgnoreCase(protocol)) {
                return;
            }
        }
        throw new AccessControlException("Access to external URI not allowed: " + uri);
    }
    
    private static String getScheme(CharSequence url) {
        StringBuilder scheme = new StringBuilder();
        for (int i = 0; i < url.length(); i++) {
            char ch = url.charAt(i);
            if (ch == ':') {
                String result = scheme.toString();
                if (!"jar".equals(result)) {
                    return result;
                }
            }
            scheme.append(ch);
        }
        throw new IllegalArgumentException("No scheme found: " + url);
    }
}