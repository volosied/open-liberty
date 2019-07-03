package com.ibm.ws.jsf.test;


import java.io.IOException;
import java.util.Hashtable;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
 
@FacesComponent(value = "translatorComponent", createTag=true, tagName="translatorComponent", namespace = "test-ns")
 
public class TranslatorComponent extends UIComponentBase {
	
    Hashtable<String, String> dictionary = new Hashtable<String, String>();
 
    @Override
   	public String getFamily() {
   		return "testComponents";
   	}
 
    public TranslatorComponent() {
        dictionary.put("dog", "cane");
        dictionary.put("cat", "gatto");
        dictionary.put("mouse", "topo");
    }
 
    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        String value = (String) getAttributes().get("value");
 
        if (value != null) {        
 
            ResponseWriter writer = context.getResponseWriter();
            Object translation = dictionary.get(value);
            if (translation == null) {
                  writer.write("Sorry word not found!");
            }
            else {
                  writer.write((String)translation);
            }
 
        }
    }
}
