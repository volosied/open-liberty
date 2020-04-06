/*
 * Copyright (c) 1997, 2020 Oracle and/or its affiliates and others.
 * All rights reserved.
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jakarta.el;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.util.Properties;
import java.util.ServiceLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;

class FactoryFinder {

    private static final boolean IS_SECURITY_ENABLED = (System.getSecurityManager() != null);

    private static final String SERVICE_RESOURCE_NAME = "META-INF/services/jakarta.el.ExpressionFactory";

    private static final String PROPERTY_NAME = "jakarta.el.ExpressionFactory";
    
    private static final String SEP;
    private static final String PROPERTY_FILE;

    static {
        if (IS_SECURITY_ENABLED) {
            SEP = AccessController.doPrivileged(
                            new PrivilegedAction<String>() {
                                @Override
                                public String run() {
                                    return System.getProperty("file.separator");
                                }

                            }
                            );
            PROPERTY_FILE = AccessController.doPrivileged(
                            new PrivilegedAction<String>() {
                                @Override
                                public String run() {
                                    return System.getProperty("java.home") + SEP +
                                           "lib" + SEP + "el.properties";
                                }

                            }
                            );
        } else {
            SEP = System.getProperty("file.separator");
            PROPERTY_FILE = System.getProperty("java.home") + SEP + "lib" +
                            SEP + "el.properties";
        }
    }

    /**
     * Creates an instance of the specified class using the specified <code>ClassLoader</code> object.
     *
     * @exception ELException if the given class could not be found or could not be instantiated
     */
    private static Object newInstance(String className, ClassLoader classLoader, Properties properties) {
        try {
            Class<?> instance = null;
            if (classLoader == null) {
                instance = Class.forName(className);
            } else {
                instance = classLoader.loadClass(className);
            }

            if (properties != null) {
                Constructor<?> constr = null;
                try {
                    constr = instance.getConstructor(Properties.class);
                } catch (Exception ex) {
                }

                if (constr != null) {
                    return constr.newInstance(properties);
                }
            }
            return instance;
        } catch (ClassNotFoundException x) {
            throw new ELException("Provider " + className + " not found", x);
        } catch (Exception x) {
            throw new ELException("Provider " + className + " could not be instantiated: " + x, x);
        }
    }

    /**
     * Finds the implementation <code>Class</code> object for the given factory. The following search order is used:
     * <ol>
     * <li>{@link ServiceLoader} lookup using <code>serviceClass</code></li>
     * <li>Property file located as <code>$java.home/lib/el.properties</code></li>
     * <li>System property lookup using <code>factoryId</code></li>
     * <li>Create an instance of <code>fallbackClassName</code></li>
     * </ol>
     * This method is package private so that this code can be shared.
     *
     * @return the <code>Class</code> object of the specified message factory; may not be <code>null</code>
     *
     * @param serviceClass The class to use when searching for the factory using the ServiceLoader mechanism
     * @param factoryId the name of the factory to find, which is a system property
     * @param fallbackClassName the implementation class name, which is to be used only if nothing else is found;
     * <code>null</code> to indicate that there is no fallback class name
     * @exception ELException if there is an error
     */
    static Object find(Class<?> serviceClass, Properties properties) {
        ClassLoader classLoader;
        try {
            if (System.getSecurityManager() != null) {
                classLoader = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
                    @Override
                    public ClassLoader run() {
                        return Thread.currentThread().getContextClassLoader();
                    }
                });
            } else {
                classLoader = Thread.currentThread().getContextClassLoader();
            }

        } catch (Exception x) {
            throw new ELException(x.toString(), x);
        }

        String className = discoverClassName(classLoader);

        return newInstance(className, classLoader, properties);
    }

    private static String discoverClassName(ClassLoader tccl) {
        String className = null;

        // First services API
        className = getClassNameServices(tccl);
        if (className == null) {
            if (IS_SECURITY_ENABLED) {
                className = AccessController.doPrivileged(
                                new PrivilegedAction<String>() {
                                    @Override
                                    public String run() {
                                        return getClassNameJreDir();
                                    }
                                }
                                );
            } else {
                // Second el.properties file
                className = getClassNameJreDir();
            }
        }
        if (className == null) {
            if (IS_SECURITY_ENABLED) {
                className = AccessController.doPrivileged(
                                new PrivilegedAction<String>() {
                                    @Override
                                    public String run() {
                                        return getClassNameBySysProp();
                                    }
                                }
                                );
            } else {
                // Third system property
                className = getClassNameBySysProp();
            }
        }
        if (className == null) {
            // Fourth - default
            className = "org.apache.el.ExpressionFactoryImpl";
        }
        return className;
    }

    private static String getClassNameServices(ClassLoader tccl) {
        InputStream is = null;

        if (tccl == null) {
            is = ClassLoader.getSystemResourceAsStream(SERVICE_RESOURCE_NAME);
        } else {
            is = tccl.getResourceAsStream(SERVICE_RESOURCE_NAME);
        }

        if (is != null) {
            String line = null;
            try (InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                            BufferedReader br = new BufferedReader(isr)) {
                line = br.readLine();
                if (line != null && line.trim().length() > 0) {
                    return line.trim();
                }
            } catch (UnsupportedEncodingException e) {
                // Should never happen with UTF-8
                // If it does - ignore & return null
            } catch (IOException e) {
                throw new ELException("Failed to read " + SERVICE_RESOURCE_NAME,
                                e);
            } finally {
                try {
                    is.close();
                } catch (IOException ioe) {/* Ignore */
                }
            }
        }

        return null;
    }

    private static String getClassNameJreDir() {
        File file = new File(PROPERTY_FILE);
        if (file.canRead()) {
            try (InputStream is = new FileInputStream(file)) {
                Properties props = new Properties();
                props.load(is);
                String value = props.getProperty(PROPERTY_NAME);
                if (value != null && value.trim().length() > 0) {
                    return value.trim();
                }
            } catch (FileNotFoundException e) {
                // Should not happen - ignore it if it does
            } catch (IOException e) {
                throw new ELException("Failed to read " + PROPERTY_FILE, e);
            }
        }
        return null;
    }

    private static final String getClassNameBySysProp() {
        String value = System.getProperty(PROPERTY_NAME);
        if (value != null && value.trim().length() > 0) {
            return value.trim();
        }
        return null;
    }
}
