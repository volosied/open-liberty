package com.test.jsf997;

import java.util.Map;

import javax.faces.component.FacesComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ListenerFor;
import javax.faces.event.PostAddToViewEvent;

@FacesComponent(value = "SingleListenerForComponent")
@ListenerFor(systemEventClass = PostAddToViewEvent.class)
public class SingleListenerForComponent extends HtmlInputText {

    /**
     * This code is testing a change to the JSF infrastructure that validates that listeners of
     * ComponentSystemEvents implements ComponentSystemEventListener. If this code executes
     * correctly then the validation via ComponentSystemEvent.isAppropriateListener() returned true.
     * The code here is testing a single listener.
     */
    @Override
    public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {
        Map<String, Object> requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
        if (event instanceof PostAddToViewEvent) {
            requestMap.put("PostAddToViewEvent", "PostAddToViewEvent");
        } else {
            super.processEvent(event);
        }
    }
}
