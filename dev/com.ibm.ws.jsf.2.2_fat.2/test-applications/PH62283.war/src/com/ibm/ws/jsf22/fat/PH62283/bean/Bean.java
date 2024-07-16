/*******************************************************************************
 * Copyright (c) 2024 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package com.ibm.ws.jsf22.fat.PH62283.bean;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.faces.context.FacesContext;

@RequestScoped
@Named
public class Bean {

    public void flashRedirect(){
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setRedirect(true);
        System.out.println("Called getFlash().setRedirect(true)");
    }
    
}
