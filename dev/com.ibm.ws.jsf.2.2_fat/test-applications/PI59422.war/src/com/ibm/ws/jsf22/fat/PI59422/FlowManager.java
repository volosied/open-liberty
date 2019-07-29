/*
* IBM Confidential
*
* OCO Source Materials
*
* WLP Copyright IBM Corp. 2016
*
* The source code for this program is not published or otherwise divested 
* of its trade secrets, irrespective of what has been deposited with the 
* U.S. Copyright Office.
*/
package com.ibm.ws.jsf22.fat.PI59422;

import javax.enterprise.context.Dependent;
import javax.inject.Named;

@Named
@Dependent
public class FlowManager {

    public void init() {
        System.out.println("PI59422: SampleFlow initialized.");
    }

    public void destroy() {
        System.out.println("PI59422: SampleFlow finalized.");
    }
}
