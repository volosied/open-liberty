/*******************************************************************************
 * Copyright (c) 2013, 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.wsoc;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

import java.io.InputStream;
import java.util.Properties;

import com.ibm.websphere.channelfw.osgi.CHFWBundle;
import com.ibm.ws.wsoc.external.WebSocketFactory;
import com.ibm.wsspi.bytebuffer.WsByteBufferPoolManager;
import com.ibm.wsspi.channelfw.ChannelFramework;
import com.ibm.wsspi.channelfw.ChannelFrameworkFactory;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;

import com.ibm.ws.wsoc.servercontainer.v10.ServerContainerImplFactory10;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;

import com.ibm.ws.wsoc.servercontainer.ServletContainerFactory;
import com.ibm.ws.wsoc.servercontainer.ServerContainerExt;
/**
 *
 */
public class WebSocketVersionServiceManager {

    private static final TraceComponent tc = Tr.register(WebSocketVersionServiceManager.class);

    /** CHFWBundle service reference -- required */
    private static final AtomicServiceReference<CHFWBundle> cfwBundleRef = new AtomicServiceReference<CHFWBundle>("chfwBundle");

    //websocket 1.1 SessionExt for WebSocket 1.1 API support
    private static final AtomicServiceReference<WebSocketFactory> websocketFactoryServiceRef =
                    new AtomicServiceReference<WebSocketFactory>("websocketFactoryService");

    private static final AtomicServiceReference<ServletContainerFactory> servletContainerFactorySRRef = new AtomicServiceReference<ServletContainerFactory>("servletContainerFactoryService");
    
    private static final WebSocketFactory DEFAULT_WEBSOCKET_FACTORY = new WebSocketFactoryImpl();
   
    private static final ServletContainerFactory DEFAULT_SERVLET_CONTAINER_FACTORY = new ServerContainerImplFactory10();

    public static String LOADED_SPEC_LEVEL = loadWsocVersion();

    private static String DEFAULT_VERSION = "1.0";

    /**
     * DS method for activating this component.
     * 
     * @param context
     */
    protected synchronized void activate(ComponentContext context) {
        cfwBundleRef.activate(context);
        websocketFactoryServiceRef.activate(context);
        servletContainerFactorySRRef.activate(context);
        System.out.println("Activating");

    }

    /**
     * DS method for deactivating this component.
     * 
     * @param context
     */
    protected synchronized void deactivate(ComponentContext context) {
        cfwBundleRef.deactivate(context);
        websocketFactoryServiceRef.deactivate(context);
        servletContainerFactorySRRef.deactivate(context);
    }

    /**
     * DS method for setting the event reference.
     * 
     * @param service
     */
    protected void setChfwBundle(ServiceReference<CHFWBundle> service) {
        cfwBundleRef.setReference(service);
    }

    /**
     * DS method for removing the event reference.
     * 
     * @param service
     */
    protected void unsetChfwBundle(ServiceReference<CHFWBundle> service) {
        cfwBundleRef.unsetReference(service);
    }

    /**
     * @return ChannelFramework associated with the CHFWBundle service.
     */
    public static ChannelFramework getCfw() {
        return cfwBundleRef.getServiceWithException().getFramework();
    }

    /**
     * Access the current reference to the bytebuffer pool manager from channel frame work.
     * 
     * @return WsByteBufferPoolManager
     */
    public static WsByteBufferPoolManager getBufferPoolManager() {
        if (cfwBundleRef.getService() != null) {
            return cfwBundleRef.getService().getBufferManager();
        }
        return ChannelFrameworkFactory.getBufferManager();
    }

    public static WebSocketFactory getWebSocketFactory() {
        //if websocket 1.1 feature is enabled, then get WebSocketFactoryV11 instance, else use the default
        //WebSocketFactoryV10 instance
        WebSocketFactory webSocketFactory = websocketFactoryServiceRef.getService();
        if (webSocketFactory == null) {
            return DEFAULT_WEBSOCKET_FACTORY;
        }
        System.out.println("getWebSocketFactory - " +webSocketFactory );
        return webSocketFactory;
    }

    protected static ServerContainerExt createServerContainerExt() {

        ServletContainerFactory servletContainerFactory = servletContainerFactorySRRef.getService();
        if (servletContainerFactory != null) {
            return servletContainerFactory.getServletContainer();
        }
        return DEFAULT_SERVLET_CONTAINER_FACTORY.getServletContainer();
    }

    protected void setServletContainerFactoryService(ServiceReference<ServletContainerFactory> service) {
        servletContainerFactorySRRef.setReference(service);
    }

    protected void unsetServletContainerFactoryService(ServiceReference<ServletContainerFactory> service) {
        servletContainerFactorySRRef.unsetReference(service);
    }

    protected void setWebsocketFactoryService(ServiceReference<WebSocketFactory> ref) {
        websocketFactoryServiceRef.setReference(ref);
    }

    protected void unsetWebsocketFactoryService(ServiceReference<WebSocketFactory> ref) {
        websocketFactoryServiceRef.unsetReference(ref);
    }

    private static synchronized String loadWsocVersion(){

        try (InputStream input = WebSocketVersionServiceManager.class.getClassLoader().getResourceAsStream("io/openliberty/wsoc/speclevel/wsocSpecLevel.properties")) {

            if(input != null){
                Properties prop = new Properties();
                prop.load(input);
                String version = prop.getProperty("version");
                Tr.debug(tc, "Loading WebSocket version " + version + " from wsocSpecLevel.propertie");
                return version;
            } else {
                if (tc.isDebugEnabled()) {
                    Tr.debug(tc, "InputStream was null for wsocSpecLevel.properties");
                }
            }

        } catch (Exception ex) {
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "Exception occured: " + ex.getCause());
            }
        }

        // logger.logp(Level.WARNING, CLASS_NAME, "loadWsocVersion", "wsoc.feature.not.loaded.correctly");

        Tr.error(tc, "wsoc.feature.not.loaded.correctly");

        return WebSocketVersionServiceManager.DEFAULT_VERSION;
    }

    public static boolean isWsoc21rHigher(){
        if(Double.parseDouble(WebSocketVersionServiceManager.LOADED_SPEC_LEVEL) >= 2.1) {
            return true;
        }
        return false;
    }

    public static boolean isWsoc20orBelow(){
        if(Double.parseDouble(WebSocketVersionServiceManager.LOADED_SPEC_LEVEL) <= 2.0) {
            return true;
        }
        return false;
    }

}
