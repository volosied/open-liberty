/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.myfaces.component.validate;

import jakarta.faces.component.UIInput;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.validator.BeanValidator;
import jakarta.faces.validator.Validator;
import org.apache.myfaces.buildtools.maven2.plugin.builder.annotation.JSFComponent;
import org.apache.myfaces.buildtools.maven2.plugin.builder.annotation.JSFProperty;
import org.apache.myfaces.util.WebConfigParamUtils;

import java.lang.IllegalStateException;
import java.util.Collections;
import java.util.List;

/**
 *
 */
@JSFComponent
public class ValidateWholeBeanComponent extends UIInput
{
    static public final String COMPONENT_FAMILY =
        "jakarta.faces.Input";
    static public final String COMPONENT_TYPE =
        "org.apache.myfaces.component.validate.ValidateWholeBean";

    public ValidateWholeBeanComponent()
    {
        setRendererType(null);
    }

    @Override
    public Object getSubmittedValue()
    {
        return "WholeBeanValidator";
    }

    @Override
    public void addValidator(Validator validator)
    {
        // No-op. It does not make sense to allow additional validators to be installed.
    }

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException
    {
        UIComponent parent = component.getParent();
        if( !(parent instanceof jakarta.faces.component.UIForm) ){
            // Throw exception similarly to Mojarra 
            throw new IllegalStateException("f:validateWholeBean must be placed within a form");
        }

        validatePlacementOfWholeBeanValidate(parent, component.getClientId(context));
        
    }

    /*
     *  As required by https://github.com/jakartaee/faces/issues/1
     *  Also ensures all inputs are available for f:wholeBeanValidate processing
     *  (otherwise they'd be empty during the validation)
     */
    public void validatePlacementOfWholeBeanValidate(UIComponent component, String clientId) throws IllegalStateException {
        List<UIComponent> children = Collections.reverse(component.getChildren());

        for(UIComponent c : children){
            if(c instanceof jakarta.faces.component.EditableValueHolder && !(c instanceof ValidateWholeBeanComponent)){
                throw new IllegalStateException("f:validateWholeBean must be placed after all validated inputs (end of the form)");
            } else {
                if(c.getClientId().equals(clientId)){
                    return;
                } else {
                    validatePlacementOfWholeBeanValidate(component, clientId);
                }
            }
        }
        

    }

    @Override
    public void validate(FacesContext context)
    {
        
        Boolean enabled = WebConfigParamUtils.getBooleanInitParameter(context.getExternalContext(), 
                BeanValidator.ENABLE_VALIDATE_WHOLE_BEAN_PARAM_NAME, Boolean.FALSE);
        
        if (Boolean.TRUE.equals(enabled) && !isDisabled())
        {
            //Install WholeBeanValidator
            Validator[] validators = this.getValidators();
            if (validators != null && validators.length > 0)
            {
                //No op
            }
            else
            {
                super.addValidator(new WholeBeanValidator());
            }
            super.validate(context);
        }
    }

    @Override
    public void setConverter(Converter converter)
    {
        // No-op. It does not make sense to allow a converter to be installed.
    }

    @Override
    public void updateModel(FacesContext context)
    {
        // Do nothing! See the following specification issue: https://github.com/eclipse-ee4j/mojarra/issues/4313
    }

    @JSFProperty
    public String getValidationGroups()
    {
        return (String) getStateHelper().eval(PropertyKeys.validationGroups);
    }
    
    public void setValidationGroups(String validationGroups)
    {
        getStateHelper().put(PropertyKeys.validationGroups, validationGroups);
    }
    
    @JSFProperty(defaultValue="false")
    public boolean isDisabled()
    {
        return (Boolean) getStateHelper().eval(PropertyKeys.disabled, false);
    }
    
    public void setDisabled(boolean disabled)
    {
        getStateHelper().put(PropertyKeys.disabled, disabled);
    }
    
    enum PropertyKeys
    {
        validationGroups,
        disabled
    }
}
