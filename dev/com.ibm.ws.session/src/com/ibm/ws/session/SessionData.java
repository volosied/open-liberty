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
package com.ibm.ws.session;

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;

import javax.servlet.ServletContext;

import com.ibm.ws.session.http.HttpSessionImpl;
import com.ibm.ws.session.utils.LoggingUtil;
import com.ibm.ws.util.ArrayEnumeration;
import com.ibm.wsspi.session.ISession;

/**
 * The WAS Http Session adaptation.  Extends the core HttpSessionImpl and adds WAS-specific function
 * <p>
 * Updated for Servlet 6.0 
 *      - common methods/APIs are moved to AbstractSessionData
 *      - Keep some methods (including supportive methods) here to override the HttpSessionImpl
 */
public class SessionData extends HttpSessionImpl {

    private static final long serialVersionUID = -76305717244905946L;
    //protected HttpSessionFacade _httpSessionFacade;
    private static final String methodClassName = "SessionData";

    public SessionData(ISession session, SessionContext sessCtx, ServletContext servCtx) {
        super(session);
        _httpSessionFacade = new HttpSessionFacade(this);
        _sessCtx = sessCtx;
        appName = _sessCtx.getAppName();
        setServletContext(servCtx);
        
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && LoggingUtil.SESSION_LOGGER_CORE.isLoggable(Level.FINER)) {
            LoggingUtil.SESSION_LOGGER_CORE.entering(methodClassName, "Constructor ; _httpSessionFacade [", _httpSessionFacade +"]");
        }
    }

    /*
     * @see javax.servlet.http.HttpSession#getValue(java.lang.String)
     */
    public Object getValue(String pName) {
        return getSessionValue(pName, false);
    }
    
    /* 
     * Updated for Servlet 6.0 
     *  - Keep here to support getValueNames(); looping if move into AbstractSessionData
     * 
     * @see javax.servlet.http.HttpSession#getAttributeNames()
     */
    public Enumeration getAttributeNames() {
        Enumeration attrNameEnum;
        if (!_hasSecurityInfo) {
            attrNameEnum = super.getAttributeNames();
        } else {
            String[] attrNames = getValueNames();
            attrNameEnum = new ArrayEnumeration(attrNames);
        }
        return attrNameEnum;
    }
  
    /*
     * @see javax.servlet.http.HttpSession#getValueNames()
     */
    public String[] getValueNames() {

        Enumeration enumeration = super.getAttributeNames();
        Vector valueNames = new Vector();
        String name = null;
        boolean securityPropFound = false;
        while (enumeration.hasMoreElements()) {
            name = (String) enumeration.nextElement();
            if (!name.equals(SECURITY_PROP_NAME)) {
                valueNames.add(name);
            } else {
                securityPropFound = true;
            }
        }
        _hasSecurityInfo = securityPropFound;
        String[] names = new String[valueNames.size()];
        return (String[]) valueNames.toArray(names);
    }

    /*
     * @see javax.servlet.http.HttpSession#putValue(java.lang.String,
     * java.lang.Object)
     */
    public void putValue(String pName, Object pValue) {
        putSessionValue(pName, pValue, false);
    }

    /*
     * @see javax.servlet.http.HttpSession#removeValue(java.lang.String)
     */
    public void removeValue(String pName) {
        removeSessionValue(pName);
    }
}
