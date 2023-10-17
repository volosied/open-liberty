package io.openliberty.test.tld;

import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;

import com.ibm.wsspi.jsp.taglib.config.GlobalTagLibConfig;
import com.ibm.wsspi.jsp.taglib.config.TldPathConfig;

public class TestJarGlobalConfig extends GlobalTagLibConfig {
    public TestJarGlobalConfig() {
        super();

        setJarName("tld2-sample.jar");


        addtoTldPathList(new TldPathConfig("META-INF/f.tld", "http://sastruts.seasar.org/functions", null));

        

        setClassloader(AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
            public ClassLoader run() {
                return TestJarGlobalConfig.class.getClassLoader();
            }
        }));

        setJarURL(AccessController.doPrivileged(new PrivilegedAction<URL>() {
            public URL run() {
                return getClassloader().getResource("META-INF/tld2-sample.jar");
            }
        }));
    }

    @SuppressWarnings("unchecked")
    private void addtoTldPathList(TldPathConfig tldPathConfig) {
        getTldPathList().add(tldPathConfig);
    }
}
