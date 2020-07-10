-include= ~${workspace}/cnf/resources/bnd/feature.props
symbolicName=io.openliberty.jsf-3.0
visibility=public
singleton=true
IBM-App-ForceRestart: install, \
 uninstall
IBM-API-Package: jakarta.faces; type="spec", \
 jakarta.faces.annotation; type="spec", \
 jakarta.faces.application; type="spec", \
 jakarta.faces.bean; type="spec", \
 jakarta.faces.component; type="spec", \
 jakarta.faces.component.behavior; type="spec", \
 jakarta.faces.component.html; type="spec", \
 jakarta.faces.component.search; type="spec", \
 jakarta.faces.component.visit; type="spec", \
 jakarta.faces.context; type="spec", \
 jakarta.faces.convert; type="spec", \
 jakarta.faces.el; type="spec", \
 jakarta.faces.event; type="spec", \
 jakarta.faces.flow; type="spec", \
 jakarta.faces.flow.builder; type="spec", \
 jakarta.faces.lifecycle; type="spec", \
 jakarta.faces.model; type="spec", \
 jakarta.faces.push; type="spec", \
 jakarta.faces.render; type="spec", \
 jakarta.faces.validator; type="spec", \
 jakarta.faces.view; type="spec", \
 jakarta.faces.view.facelets; type="spec", \
 jakarta.faces.webapp; type="spec", \
 org.apache.myfaces.renderkit.html; type="third-party", \
 org.apache.myfaces.shared.config; type="third-party", \
 org.apache.myfaces.shared.renderkit; type="third-party", \
 org.apache.myfaces.shared.renderkit.html; type="third-party", \
 org.apache.myfaces.shared.renderkit.html.util; type="third-party"
IBM-ShortName: jsf-3.0
Subsystem-Name: Jakarta Server Faces 3.0
-features=io.openliberty.jakarta.cdi-3.0, \
 io.openliberty.jakarta.servlet-5.0, \
 io.openliberty.jakarta.validation-3.0, \
 io.openliberty.jakarta.jsf-3.0, \
 com.ibm.websphere.appserver.jsp-3.0, \
 io.openliberty.jakarta.jaxb-3.0; \
 com.ibm.websphere.appserver.jsfProvider-3.0.0.MyFaces, \
 com.ibm.websphere.appserver.eeCompatible-9.0
-bundles=com.ibm.ws.org.apache.myfaces.2.3.jakarta, \
 com.ibm.ws.org.apache.commons.beanutils.1.9.4, \
 com.ibm.ws.org.apache.commons.collections, \
 com.ibm.ws.org.apache.commons.discovery.0.2, \
 com.ibm.ws.org.apache.commons.logging.1.0.3, \
 com.ibm.ws.jsf.shared.jakarta, \
 com.ibm.ws.cdi.interfaces.jakarta, \
 com.ibm.ws.org.apache.commons.digester.1.8, \
 io.openliberty.jakarta.websocket.2.0; apiJar=false; location:="dev/api/spec/,lib/"; mavenCoordinates="jakarta.websocket:jakarta.websocket-api:2.0.0-RC1", \
 com.ibm.websphere.appserver.thirdparty.jsf-2.3.jakarta; location:="dev/api/third-party/"
kind=noship
edition=full
WLP-Activation-Type: parallel
