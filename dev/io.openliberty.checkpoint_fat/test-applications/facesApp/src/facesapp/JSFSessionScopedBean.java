/*******************************************************************************
 * Copyright (c) 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package facesapp;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean(name="jsfSessionScopedBean", eager=true)
@SessionScoped
public class JSFSessionScopedBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private String data = ":" + getClass().getSimpleName() + ":";

    // Mojarra does not support @EJB, but MyFaces does
    @EJB
    TestEJB ejb;

    @PostConstruct
    public void start() {
        System.out.println("JSFSessionScopedBean postConstruct called");
        this.data += ":PostConstructCalled:";
        if (ejb != null && ejb.verifyPostConstruct())
            this.data += ":EJB-injected:";
        System.out.println("JSFSessionScopedBean data is: " + data);
    }

    @PreDestroy
    public void stop() {
        System.out.println("JSFSessionScopedBean preDestroy called.");
    }

    public void setData(String newData) {
        this.data += newData;
    }

    public String getData() {
        return this.data;
    }

    public String nextPage() {
        return "TestBean";
    }
}
