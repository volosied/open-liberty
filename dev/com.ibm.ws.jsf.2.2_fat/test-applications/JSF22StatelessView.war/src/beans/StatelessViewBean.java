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
/**
 * A simple managed bean that will be used to test
 * a view scoped managed bean.
 * 
 * @author Bill Lucy
 *
 */
package beans;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

@ManagedBean
public class StatelessViewBean implements Serializable {

    /**  */
    private static final long serialVersionUID = 1L;

    private String stateless;

    public void setStateless(String state) {
        stateless = state;
    }

    public String getStateless() {
        return stateless;
    }

    public void statelessQuestion() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        UIViewRoot uiViewRoot = facesContext.getViewRoot();
        Boolean isTransient = uiViewRoot.isTransient();
        Boolean isStateless = facesContext.getRenderKit().getResponseStateManager().isStateless(facesContext, null);
        String output = "isTransient returns " + isTransient.toString();
        output += " and isStateless returns  " + isStateless.toString();
        stateless = output;
    }
}
