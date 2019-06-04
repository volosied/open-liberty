/*
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WLP Copyright IBM Corp. 2015
 *
 * The source code for this program is not published or otherwise divested 
 * of its trade secrets, irrespective of what has been deposited with the 
 * U.S. Copyright Office.
 */
package cdi.interceptors;

import javax.annotation.Priority;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import cdi.beans.ActionListenerBean;

/**
 * Interceptor locates the actionListenerBean and, if found (means it has been used for
 * the current session) adds data to the bean to show that the interceptor was called.
 */
@Interceptor
@TestActionListener
@Priority(Interceptor.Priority.APPLICATION)
public class TestActionListenerInterceptor {

    @AroundInvoke
    public Object checkParams(InvocationContext context) throws Exception {
        Object[] params = context.getParameters();

        if (params.length >= 1) {
            ActionEvent event = ((ActionEvent) params[0]);

            ActionListenerBean testBean = (ActionListenerBean) FacesContext.getCurrentInstance().
                            getExternalContext().getSessionMap().get("actionListenerBean");

            if (testBean != null) {
                testBean.setData("TestActionListenerInterceptor");
            }
        }
        context.setParameters(params);
        return context.proceed();
    }

}
