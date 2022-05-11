/*******************************************************************************
 * Copyright (c) 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package io.openliberty.session.http;

import java.util.logging.Level;

import com.ibm.ws.session.AbstractSessionData;
import com.ibm.ws.session.utils.LoggingUtil;
import com.ibm.wsspi.session.ISession;

/**
 * This class provides the adapted version of the ISession.
 * It simply wrappers the session and proxies any of its method calls to
 * the underlying ISession object.
 *
 * Since Servlet 6.0
 */
public class HttpSessionImpl60 extends AbstractSessionData {

    private static final String methodClassName = "HttpSessionImpl60";
    private static boolean _loggedVersion = false;

    protected HttpSessionImpl60(ISession session) {
        if (com.ibm.ejs.ras.TraceComponent.isAnyTracingEnabled() && LoggingUtil.SESSION_LOGGER_CORE.isLoggable(Level.FINE)) {
            if (!_loggedVersion) {
                LoggingUtil.SESSION_LOGGER_CORE.logp(Level.FINE, methodClassName, "", "HttpSession implementation for servlet 6.0");
                _loggedVersion = true;
            }
        }
        _iSession = session;
    }

    /**
     * Method toString
     * <p>
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("# HttpSessionImpl60 # \n { ").append("\n _iSession=").append(_iSession).append("\n } \n");
        return sb.toString();
    }
}
