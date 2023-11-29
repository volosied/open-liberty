package io.openliberty.test.tld;

import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;

import com.ibm.wsspi.jsp.taglib.config.GlobalTagLibConfig;
import com.ibm.wsspi.jsp.taglib.config.TldPathConfig;

public class TestGlobalConfig extends GlobalTagLibConfig {
    public TestGlobalConfig() {
        super();

        setJarName("test-tld.jar");


        addtoTldPathList(new TldPathConfig("WEB-INF/tld/test-1.tld", "io.tld", null));

        

        setClassloader(AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
                return TestGlobalConfig.class.getClassLoader();
            }
        }));

        setJarURL(AccessController.doPrivileged(new PrivilegedAction<URL>() {
            public URL run() {
                return getClassloader().getResource("WEB-INF/tld/test-1.tld");
            }
        }));
    }

    @SuppressWarnings("unchecked")
    private void addtoTldPathList(TldPathConfig tldPathConfig) {
        getTldPathList().add(tldPathConfig);
    }
}
