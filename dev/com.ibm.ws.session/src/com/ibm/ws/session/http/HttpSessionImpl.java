/*******************************************************************************
 * Copyright (c) 1997, 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.session.http;

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;

import javax.servlet.http.HttpSessionContext;

import com.ibm.ws.session.utils.LoggingUtil;
import com.ibm.ws.session.AbstractSessionData;
import com.ibm.wsspi.session.ISession;

/**
 * This class provides the adapted version of the ISession.
 * It simply wrappers the session and proxies any of its method calls to
 * the underlying ISession object.
 * <p> 
 * Updated for Servlet 6.0:
 * This class now provides specific API up to servlet 5.0.  
 * Common APIs are moved up into the AbstractHttpSession
 */
public class HttpSessionImpl extends AbstractSessionData {

    private static final String methodClassName = "HttpSessionImpl";
    /*
     * For logging the CMVC file version once.
     */
    private static boolean _loggedVersion = false;

    /*
     * A reference to the HttpSessionContext singleton that will
     * be returned for all http sessions.
     * 
     * @deprecated
     */
    private static final HttpSessionContext _httpSessionContext = new HttpSessionContextImpl();

    /*
     * Adding a message for when IllegalStateException is thrown
     */
    private static final String iseMessage = "The method is called on an invalidated session: ";

    /**
     * Class Constructor
     * <p>
     * Note: This method receives an instance of IManagedSession as an argument, but it also needs to be an instance of ISession. If not, this implementation will be broken.
     * 
     * @param session
     */
    protected HttpSessionImpl(ISession session) {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && LoggingUtil.SESSION_LOGGER_CORE.isLoggable(Level.FINE)) {
            if (!_loggedVersion) {
                LoggingUtil.SESSION_LOGGER_CORE.logp(Level.FINE, methodClassName, "", "CMVC Version 1.6 10/16/08 11:56:10");
                _loggedVersion = true;
            }
        }
        _iSession = session;
    }

    // ----------------------------------------
    // Public Methods
    // ----------------------------------------


    /**
     * Method getSessionContext
     * <p>
     * 
     * @deprecated
     * @see javax.servlet.http.HttpSession#getSessionContext()
     */
    public HttpSessionContext getSessionContext() {
        return _httpSessionContext;
    }

    /**
     * Method getValue
     * <p>
     * 
     * @deprecated
     * @see javax.servlet.http.HttpSession#getValue(java.lang.String)
     */
    public Object getValue(String attributeName) {
        return this.getAttribute(attributeName);
    }

    /**
     * Method getValueNames
     * <p>
     * 
     * @deprecated
     * @see javax.servlet.http.HttpSession#getValueNames()
     */
    public String[] getValueNames() {
        if (!_iSession.isValid())
            throw new IllegalStateException(iseMessage+_iSession.getId());
        Enumeration enumeration = this.getAttributeNames();
        Vector valueNames = new Vector();
        String name = null;
        while (enumeration.hasMoreElements()) {
            name = (String) enumeration.nextElement();
            valueNames.add(name);
        }
        String[] names = new String[valueNames.size()];
        return (String[]) valueNames.toArray(names);
    }

    /**
     * Method putValue
     * <p>
     * 
     * @deprecated
     * @see javax.servlet.http.HttpSession#putValue(java.lang.String, java.lang.Object)
     */
    public void putValue(String attributeName, Object value) {
        this.setAttribute(attributeName, value);

    }

    /**
     * Method removeValue
     * <p>
     * 
     * @deprecated
     * @see javax.servlet.http.HttpSession#removeValue(java.lang.String)
     */
    public void removeValue(String attributeName) {
        this.removeAttribute(attributeName);
    }

    /**
     * Method toString
     * <p>
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("# HttpSessionImpl # \n { ").append("\n _iSession=").append(_iSession).append("\n _httpSessionContext=").append(_httpSessionContext).append("\n } \n");
        return sb.toString();
    }
}