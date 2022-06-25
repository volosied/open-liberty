/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.wsoc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;

import org.osgi.service.component.annotations.Reference;

import com.ibm.websphere.channelfw.osgi.CHFWBundle;
import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.runtime.metadata.ModuleMetaData;
import com.ibm.ws.wsoc.injection.InjectionProvider;
import com.ibm.ws.wsoc.injection.InjectionProvider12;
import com.ibm.ws.wsoc.injection.InjectionService;
import com.ibm.ws.wsoc.injection.InjectionService12;
import com.ibm.wsspi.bytebuffer.WsByteBufferPoolManager;
import com.ibm.wsspi.channelfw.ChannelFramework;
import com.ibm.wsspi.channelfw.ChannelFrameworkFactory;
import com.ibm.wsspi.injectionengine.InjectionEngine;
import com.ibm.wsspi.injectionengine.ReferenceContext;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;

import com.ibm.ws.wsoc.servercontainer.ServerContainerHandler;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

import com.ibm.ws.wsoc.servercontainer.ServletContainerFactory;
import com.ibm.ws.wsoc.servercontainer.ServerContainerExt;
/**
 *
 */
@Component(name="com.ibm.ws.wsoc.ServiceManager", configurationPid="com.ibm.ws.wsoc.ServiceManager", configurationPolicy=ConfigurationPolicy.REQUIRE, property={"service.vendor=IBM","type:String=web"})
public class ServiceManager {

    private static final TraceComponent tc = Tr.register(ServiceManager.class);

    /** CHFWBundle service reference -- required */
    private static final AtomicServiceReference<CHFWBundle> cfwBundleRef = new AtomicServiceReference<CHFWBundle>("chfwBundle");

    private static final AtomicServiceReference<InjectionService> injectionServiceSRRef = new AtomicServiceReference<InjectionService>("injectionService");

    private static final AtomicServiceReference<InjectionService12> injectionService12SRRef = new AtomicServiceReference<InjectionService12>("injectionService12");

    private static final AtomicServiceReference<InjectionEngine> injectionEngineSRRef = new AtomicServiceReference<InjectionEngine>("injectionEngine");

    /** Reference for delayed activation of the dispatcher */
    // private static final AtomicServiceReference<ServerContainerHandler> serverContainerHandlerSRRef = new AtomicServiceReference<ServerContainerHandler>("serverContainerHandler");
    private static final AtomicServiceReference<ServletContainerFactory> servletContainerFactorySRRef = new AtomicServiceReference<ServletContainerFactory>("servletContainerFactory");

    //JDK's Executor service to be used in SessionIdleTimeout functionality
    private static final AtomicServiceReference<ScheduledExecutorService> scheduledExecSvcRef =
                    new AtomicServiceReference<ScheduledExecutorService>("scheduledExecutorService");

    private static final AtomicServiceReference<ExecutorService> executorServiceRef =
                    new AtomicServiceReference<ExecutorService>("executorService");

    /**
     * DS method for activating this component.
     * 
     * @param context
     */
    protected synchronized void activate(ComponentContext context) {
        System.out.println("Activating ServiceManager");
        cfwBundleRef.activate(context);
        scheduledExecSvcRef.activate(context);
        injectionServiceSRRef.activate(context);
        injectionService12SRRef.activate(context);
        executorServiceRef.activate(context);
        injectionEngineSRRef.activate(context);
        servletContainerFactorySRRef.activate(context);
    }

    /**
     * DS method for deactivating this component.
     * 
     * @param context
     */
    protected synchronized void deactivate(ComponentContext context) {
        cfwBundleRef.deactivate(context);
        scheduledExecSvcRef.deactivate(context);
        injectionServiceSRRef.deactivate(context);
        injectionService12SRRef.deactivate(context);
        executorServiceRef.deactivate(context);
        injectionEngineSRRef.deactivate(context);
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

    protected void setInjectionService(ServiceReference<InjectionService> ref) {
        if (tc.isDebugEnabled()) {
            Tr.debug(tc, "InjectionService is getting set: " + ref);
        }
        injectionServiceSRRef.setReference(ref);
    }

    protected void setInjectionEngine(ServiceReference<InjectionEngine> ref) {
        injectionEngineSRRef.setReference(ref);
    }

    protected void unsetInjectionEngine(ServiceReference<InjectionEngine> ref) {
        injectionEngineSRRef.unsetReference(ref);
    }

    @Reference(service=ServletContainerFactory.class, name="servletContainerFactory")
    protected void setServletContainerFactory(ServiceReference<ServletContainerFactory> ref) {
        System.out.println("setServerContainerHandler");
        servletContainerFactorySRRef.setReference(ref);
    }

    protected void unsetServletContainerFactory(ServiceReference<ServletContainerFactory> ref) {
        System.out.println("unsetServerContainerHandler");
        servletContainerFactorySRRef.unsetReference(ref);
    }

    public static ReferenceContext getInjectionServiceReferenceContext(ModuleMetaData mmd) {
        if (injectionEngineSRRef.getService() != null) {

            ReferenceContext referenceContext = injectionEngineSRRef.getService().getCommonReferenceContext(mmd);
            return referenceContext;
        }

        return null;
    }

    protected void unsetInjectionService(ServiceReference<InjectionService> ref) {
        injectionServiceSRRef.unsetReference(ref);
    }

    public static InjectionProvider getInjectionProvider() {

        if (injectionServiceSRRef.getService() != null) {
            return injectionServiceSRRef.getService().getInjectionProvider();
        }

        return null;
    }

    protected void setInjectionService12(ServiceReference<InjectionService12> ref) {

        if (tc.isDebugEnabled()) {
            Tr.debug(tc, "InjectionService12 is getting set: " + ref);
        }
        injectionService12SRRef.setReference(ref);
    }

    protected void unsetInjectionService12(ServiceReference<InjectionService12> ref) {
        injectionService12SRRef.unsetReference(ref);
    }

    public static InjectionProvider12 getInjectionProvider12() {

        if (injectionService12SRRef.getService() != null) {
            return injectionService12SRRef.getService().getInjectionProvider();
        }

        return null;
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

    /**
     * Declarative Services method for setting the (unmanaged) scheduled executor service reference
     * 
     * @param ref reference to the service
     */
    protected void setScheduledExecutorService(ServiceReference<ScheduledExecutorService> ref) {
        scheduledExecSvcRef.setReference(ref);
    }

    /**
     * Declarative Services method for unsetting the (unmanaged) scheduled executor service reference
     * 
     * @param ref reference to the service
     */
    protected void unsetScheduledExecutorService(ServiceReference<ScheduledExecutorService> ref) {
        scheduledExecSvcRef.unsetReference(ref);
    }

    public static ScheduledExecutorService getExecutorService() {
        return scheduledExecSvcRef.getService();
    }

    /**
     * Declarative Services method for setting the (unmanaged) scheduled executor service reference
     * 
     * @param ref reference to the service
     */
    protected void setExecutorService(ServiceReference<ExecutorService> ref) {
        executorServiceRef.setReference(ref);
    }

    /**
     * Declarative Services method for unsetting the (unmanaged) scheduled executor service reference
     * 
     * @param ref reference to the service
     */
    protected void unsetExecutorService(ServiceReference<ExecutorService> ref) {
        executorServiceRef.unsetReference(ref);
    }

    public static ExecutorService getExecutorThreadService() {
        return executorServiceRef.getService();
    }


    protected static ServerContainerExt createServerContainerExt() {

        ServletContainerFactory servletContainerFactory = servletContainerFactorySRRef.getService();
        if (servletContainerFactory != null) {
            return servletContainerFactory.getServletContainer();
        }
        return null;
    }

}
