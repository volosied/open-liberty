package com.test.jsf599;

import java.io.IOException;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

@FacesComponent(value = "placeCompositeComponent")
public class PlaceCompositeComponent extends UIComponentBase {

    @Override
    public String getFamily() {
        return "testComponents";
    }

    /**
     * The code here is testing programmatic creation of a JSF component using the component type.
     */
    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        UIComponent composite = context.getApplication().createComponent(context, "placeComponent", null);
        composite.setId("placeId");
        this.getChildren().add(composite);
    }

    @Override
    public void encodeEnd(FacesContext arg0) throws IOException {
        super.encodeEnd(arg0);
    }
}