-include= ~${workspace}/cnf/resources/bnd/feature.props
symbolicName=com.ibm.websphere.appserver.el-4.0
visibility=public
singleton=true
IBM-App-ForceRestart: install, \
 uninstall
IBM-API-Package: jakarta.el; type="spec", \
 com.sun.el;  type="internal", \
 com.sun.el.lang; type="internal", \
 com.sun.el.util; type="internal", \
 com.sun.el.stream; type="internal"
IBM-ShortName: el-4.0
Subsystem-Version: 4.0.0
Subsystem-Name: Jakarta Expression Language 4.0
-features=io.openliberty.jakarta.el-4.0, \
 com.ibm.websphere.appserver.eeCompatible-9.0
-bundles=io.openliberty.com.sun.el4
kind=beta
edition=core
WLP-Activation-Type: parallel
