package AppConfigPopJar;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

public class PhaseTracker implements PhaseListener {

    /**  */
    private static final long serialVersionUID = 521575630176960419L;

    @Override
    public void afterPhase(PhaseEvent event) {
        // do nothing        
    }

    @Override
    public void beforePhase(PhaseEvent event) {

        if (event.getPhaseId() == PhaseId.RENDER_RESPONSE) {
            FacesContext.getCurrentInstance().getExternalContext().log("JSF22:ACP beforePhase called.");
        }
    }

    /**
     * 
     * @return
     */
    @Override
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }
}
