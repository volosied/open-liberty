package AppConfigPopJar;

import javax.faces.application.ApplicationConfigurationPopulator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * This is a callback to do configuration via an empty DOM root which respresents faces.config.xml. Existing configuration options can be added, but cannot overwrite existing ones.
 * 
 * This callback tests the presence of META-INF/services/javax.faces.application.ApplicationConfigurationPopulator in a jar in a war file.
 * 
 */
public class JarACPInitializer extends ApplicationConfigurationPopulator {

    @Override
    public void populateApplicationConfiguration(Document document) {

        String ns = document.getDocumentElement().getNamespaceURI();

        Element a = document.createElementNS(ns, "application");
        Element sel = document.createElementNS(ns, "system-event-listener");
        sel.appendChild(createNode(document, "system-event-listener-class", "AppConfigPopJar.SEListener"));
        sel.appendChild(createNode(document, "system-event-class", "javax.faces.event.PostConstructApplicationEvent"));
        a.appendChild(sel);
        document.getChildNodes().item(0).appendChild(a);

        Element l = document.createElementNS(ns, "lifecycle");
        l.appendChild(createNode(document, "phase-listener", "AppConfigPopJar.PhaseTracker"));
        document.getChildNodes().item(0).appendChild(l);

    }

    private Element createNode(Document doc, String element, String value) {
        String ns = doc.getDocumentElement().getNamespaceURI();
        Element e = doc.createElementNS(ns, element);
        e.appendChild(doc.createTextNode(value));
        return e;

    }

}
