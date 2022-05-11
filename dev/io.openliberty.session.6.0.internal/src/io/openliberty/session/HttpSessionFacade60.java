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
package io.openliberty.session;

import com.ibm.ws.session.AbstractHttpSessionFacade;

//public class HttpSessionFacade60 implements IBMSession, IBMSessionExt {
public class HttpSessionFacade60 extends AbstractHttpSessionFacade {

    //  protected transient SessionData60 _session = null;
    private static final long serialVersionUID = 3108339284895967670L;

    public HttpSessionFacade60(SessionData60 data) {
        this._session = data;
    }

}
