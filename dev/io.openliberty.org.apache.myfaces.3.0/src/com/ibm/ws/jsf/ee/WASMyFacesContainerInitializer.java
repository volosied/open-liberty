/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.jsf.ee;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.faces.application.ResourceDependencies;
import jakarta.faces.application.ResourceDependency;
import jakarta.faces.bean.ApplicationScoped;
import jakarta.faces.bean.CustomScoped;
import jakarta.faces.bean.ManagedBean;
import jakarta.faces.bean.ManagedProperty;
import jakarta.faces.bean.NoneScoped;
import jakarta.faces.bean.ReferencedBean;
import jakarta.faces.bean.RequestScoped;
import jakarta.faces.bean.SessionScoped;
import jakarta.faces.bean.ViewScoped;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.behavior.FacesBehavior;
import jakarta.faces.convert.FacesConverter;
import jakarta.faces.convert.Converter;
import jakarta.faces.event.ListenerFor;
import jakarta.faces.event.ListenersFor;
import jakarta.faces.event.NamedEvent;
import jakarta.faces.model.FacesDataModel;
import jakarta.faces.render.FacesBehaviorRenderer;
import jakarta.faces.render.FacesRenderer;
import jakarta.faces.render.Renderer;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.HandlesTypes;

import org.apache.myfaces.ee.MyFacesContainerInitializer;
import org.apache.myfaces.webapp.StartupServletContextListener;

import com.ibm.ws.jsf.shared.JSFConstants;
import com.ibm.ws.jsf.shared.JSFConstants.JSFImplEnabled;

/**
 *
 */
@HandlesTypes({
        ApplicationScoped.class,
        CustomScoped.class,
        FacesBehavior.class,
        FacesBehaviorRenderer.class,
        FacesComponent.class,
        FacesConverter.class,
        FacesRenderer.class,
        FacesValidator.class,
        FacesDataModel.class,
        ListenerFor.class,
        ListenersFor.class,
        ManagedBean.class,
        ManagedProperty.class,
        NamedEvent.class,
        NoneScoped.class,
        ReferencedBean.class,
        RequestScoped.class,
        ResourceDependencies.class,
        ResourceDependency.class,
        SessionScoped.class,
        ViewScoped.class,
        UIComponent.class,
        Converter.class,
        Renderer.class,
        Validator.class
    })
public class WASMyFacesContainerInitializer extends MyFacesContainerInitializer {

    private static final Logger log = Logger.getLogger(WASMyFacesContainerInitializer.class.getName());

    @Override
    public void onStartup(Set<Class<?>> clazzes, ServletContext servletContext) throws ServletException {
        super.onStartup(clazzes, servletContext);

        Boolean mappingAdded = (Boolean) servletContext.getAttribute(MyFacesContainerInitializer.FACES_SERVLET_ADDED_ATTRIBUTE);
        if (mappingAdded != null && mappingAdded) {
            /**
             * Add the myfaces lifecycle listener; this is necessary since the StartupServletContextListener registration
             * was moved from the myfaces_core.tld to a web-fragment.
             *
             * Currently, Liberty does not pick that web-fragment up, which is ok since we don't want every
             * application on the server to be JSF enabled. The JSFExtensionFactory will add the listener for applications
             * that define a FacesServlet and we'll add the listener here for applications that have a FacesServlet defined dynamically.
             */
            addLifecycleListener(servletContext);

            log.log(Level.INFO, "Added StartupServletContextListener to the servlet context");
        }
    }

    private void addLifecycleListener(ServletContext servletContext) {
        ServletContextListener startupServletContextListener = null;

        //initialize context listeners
        startupServletContextListener = new StartupServletContextListener();

        //register listeners with webapp classloader
        servletContext.addListener(startupServletContextListener);
        setJSFImplEnabled(servletContext, JSFImplEnabled.MyFaces);
    }

    private void setJSFImplEnabled(ServletContext servletContext, JSFImplEnabled impl) {
        servletContext.setAttribute(JSFConstants.JSF_IMPL_ENABLED_PARAM, impl);
    }
}
