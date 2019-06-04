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
package cdi.beans;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;

import cdi.beans.injected.ConstructorBean;
import cdi.beans.injected.FieldBean;
import cdi.beans.injected.MethodBean;
import cdi.beans.injected.TestBeanType;
import cdi.interceptors.TestPlainBean;

/**
 *
 */
@ManagedBean(name = "testBean")
@SessionScoped
public class TestBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String data = ":TestBean:";

    // Field Injected bean
    @Inject
    @TestBeanType
    private FieldBean _fieldBean;
    //private final FieldBean _fieldBean = null;

    private ConstructorBean _constructorBean = null;
    private MethodBean _methodBean = null;

    // Constructor Injected bean
    @Inject
    public TestBean(ConstructorBean bean) {
        _constructorBean = bean;
    }

    @PostConstruct
    public void start() {
        data += ":PostConstructCalled:";
    }

    @PreDestroy
    public void stop() {
        System.out.println("TestBean preDestroy called.");
    }

    // Method Injected bean
    @Inject
    public void setMethodBean(MethodBean bean) {
        _methodBean = bean;
    }

    @TestPlainBean
    public void setData(String newData) {
        this.data += newData;
    }

    @TestPlainBean
    public String getData() {

        if (_fieldBean == null)
            data += ":FieldInjectionFailed:";
        else
            data += _fieldBean.getData();

        if (_constructorBean == null)
            data += ":ConstructorInjectionFailed:";
        else
            data += _constructorBean.getData();

        if (_methodBean == null)
            data += ":MethodInjectionFailed:";
        else
            data += _methodBean.getData();

        return data;
    }

    public String nextPage() {
        return "TestBean";
    }

}
