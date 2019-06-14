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
import java.sql.Timestamp;
import java.util.Date;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

@Named
@ViewScoped
public class ViewScopedCDIBean implements Serializable {

    /**  */
    private static final long serialVersionUID = 1L;

    private Timestamp timestamp;

    public ViewScopedCDIBean() {
        Date date = new Date();
        timestamp = new Timestamp(date.getTime());
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
