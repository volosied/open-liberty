package com.ibm.ws.jsf22.fat.PI85492;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

public class TestPhaseListener implements PhaseListener {

    private static final long serialVersionUID = 9000L;

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }

    @Override
    public void beforePhase(PhaseEvent event) {
        return;
    }

    @Override
    public void afterPhase(PhaseEvent event) {
        throw new RuntimeException("after RENDER_RESPONSE");
    }
}
