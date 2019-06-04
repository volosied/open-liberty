/*
 * Copyright (c) 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 */
/**
 * A simple CDI injected ViewScope bean with request, app, response, dep bean references.
 *
 */
package cdi.beans.injected;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import cdi.beans.viewscope.ViewScopeAppBean;
import cdi.beans.viewscope.ViewScopeDepBean;
import cdi.beans.viewscope.ViewScopeReqBean;
import cdi.beans.viewscope.ViewScopeSessBean;

@Named
@ViewScoped
public class ViewScopedManagedBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private ViewScopeAppBean vab = null;

    private ViewScopeSessBean vsb;

    @Inject
    private ViewScopeReqBean vrb;

    @Inject
    private ViewScopeDepBean vdb;

    @Inject
    public ViewScopedManagedBean(ViewScopeAppBean vb) {
        vab = vb;
    }

    @Inject
    public void setMethodBean(ViewScopeSessBean bean) {
        vsb = bean;
    }

    public ViewScopeAppBean getVab() {
        return vab;
    }

    public ViewScopeReqBean getVrb() {
        return vrb;
    }

    public ViewScopeSessBean getVsb() {
        return vsb;
    }

    public ViewScopeDepBean getVdb() {
        return vdb;
    }
}
