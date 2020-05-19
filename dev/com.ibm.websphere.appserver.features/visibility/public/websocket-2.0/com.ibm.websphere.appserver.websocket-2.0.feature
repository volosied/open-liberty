-include= ~${workspace}/cnf/resources/bnd/feature.props
symbolicName=com.ibm.websphere.appserver.websocket-2.0
visibility=public
singleton=true
IBM-App-ForceRestart: install, \
 uninstall
IBM-API-Package: jakarta.websocket; version="2.0"; type="spec", \
 jakarta.websocket.server; version="2.0"; type="spec", \
 com.ibm.websphere.wsoc; type="ibm-api"
IBM-ShortName: websocket-2.0
Subsystem-Name: Java WebSocket 2.0
-features=com.ibm.websphere.appserver.servlet-5.0
-bundles=com.ibm.ws.wsoc.jakarta, \
 com.ibm.websphere.jakartaee.websocket.2.0; location:="dev/api/spec/,lib/"; mavenCoordinates="jakarta.websocket:jakarta.websocket-api:2.0", \
 com.ibm.ws.wsoc.1.1.jakarta
-jars=com.ibm.websphere.appserver.api.wsoc.jakarta; location:=dev/api/ibm/
-files=dev/api/ibm/javadoc/com.ibm.websphere.appserver.api.wsoc_1.0-javadoc.zip
kind=noship
edition=full
WLP-Activation-Type: parallel
