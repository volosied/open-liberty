/*******************************************************************************
 * Copyright (c) 2017, 2023 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.wsoc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpointConfig;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.ws.wsoc.external.SessionExt;

public class EndpointManager {

    private static final TraceComponent tc = Tr.register(EndpointManager.class);

    // Map of URI path to the endpoint config object, currently not used for upgrades called from WsWsocServerContainer doUpgrade calls
    private final ConcurrentHashMap<String, ServerEndpointConfig> serverEndpointConfigMap = new ConcurrentHashMap<String, ServerEndpointConfig>();

    // Map the Endpoint config object to an instance of the Annotated Endpoint it is a config for.  For cache and clone generation.
    private final ConcurrentHashMap<Class<? extends Object>, AnnotatedEndpoint> annotatedEndpointMap = new ConcurrentHashMap<Class<? extends Object>, AnnotatedEndpoint>();

    // Map of active sessions for a given endpoint class
    private final ConcurrentHashMap<Class<?>, ArrayList<Session>> endpointSessionMap = new ConcurrentHashMap<Class<?>, ArrayList<Session>>();

    // Map of  sessions with corresponding active HttpSession
    private final ConcurrentHashMap<String, ArrayList<Session>> httpSessionMap = new ConcurrentHashMap<String, ArrayList<Session>>();

    public EndpointManager() {
        serverEndpointConfigMap.clear();
    }

    public void clear() {
        serverEndpointConfigMap.clear();
        annotatedEndpointMap.clear();
    }

    public synchronized void addSession(final Endpoint ep, final SessionExt sess) {

        Class<?> cl = ep.getClass();
        if (cl.equals(AnnotatedEndpoint.class)) {
            final AnnotatedEndpoint ae = (AnnotatedEndpoint) ep;
            cl = ae.getServerEndpointClass();
        }

        // find the session array for the given endpoint
        ArrayList<Session> sa = endpointSessionMap.get(cl);

        // create a new session list if none was found
        if (sa == null) {
            sa = new ArrayList<Session>();
        }

        // add the new session to the list for this endpoint
        sa.add(sess);

        // put the updated list back into the Map
        endpointSessionMap.put(cl, sa);

        if (tc.isDebugEnabled()) {
            Tr.debug(tc, "added session of: " + sess + "  to endpoint class of: " + cl + " using list of: " + sa + " in endpointmanager of: " + this);
        }

        final String id = sess.getSessionImpl().getHttpSessionID();
        if (id != null) {
            ArrayList<Session> http_sa = httpSessionMap.get(id);
            if (http_sa == null) {
                http_sa = new ArrayList<Session>();
            }
            http_sa.add(sess);
            httpSessionMap.put(id, http_sa);
        }
    }

    public synchronized void closeAllOpenSessions() {
        final CloseReason cr = new CloseReason(CloseReason.CloseCodes.GOING_AWAY, "Application shutting down.");

        final Set<Entry<Class<?>, ArrayList<Session>>> s = endpointSessionMap.entrySet();
        final Iterator<Entry<Class<?>, ArrayList<Session>>> i = s.iterator();
        while (i.hasNext()) {
            final Entry<Class<?>, ArrayList<Session>> e = i.next();
            final ArrayList<Session> sessions = e.getValue();

            // I don't think we can iterate over the list and call a session close (concurrent modification exception when close tries to remove on same thread..
            //  Iterator.remove() might work here as well...
            //  We have the synch, so this logic should work as well
            while (!sessions.isEmpty()) {
                final Session sess = sessions.remove(0);
                final SessionExt ext = (SessionExt) sess;

                ext.getSessionImpl().closeBecauseAppStopping(cr);

            }
        }

    }

    /*
     * closeAllSessions will call this with the session already removed...
     */
    public synchronized void removeSession(final Endpoint ep, final SessionExt sess) {
        Class<?> cl = ep.getClass();

        if (cl.equals(AnnotatedEndpoint.class)) {
            final AnnotatedEndpoint ae = (AnnotatedEndpoint) ep;
            cl = ae.getServerEndpointClass();
        }

        // Always try to remove http session if we can.
        final String id = sess.getSessionImpl().getHttpSessionID();
        if (id != null) {
            httpSessionMap.remove(id);
        }

        // find the session array for the given endpoint
        final ArrayList<Session> sa = endpointSessionMap.get(cl);

        // nothing to remove if we don't have a session
        if (sa == null) {
            return;
        }

        // remove the new session from the list for this endpoint
        sa.remove(sess);

        // put the updated list back into the Map
        endpointSessionMap.put(cl, sa);

        if (tc.isDebugEnabled()) {
            Tr.debug(tc, "removed session of: " + sess.getId() + "  from endpoint class of: " + cl.getName() + " in endpointmanager of: " + this);
        }
    }

    public synchronized Set<Session> getOpenSessions(final Endpoint ep) {
        Class<?> cl = ep.getClass();
        if (cl.equals(AnnotatedEndpoint.class)) {
            final AnnotatedEndpoint ae = (AnnotatedEndpoint) ep;
            cl = ae.getServerEndpointClass();
        }

        final ArrayList<Session> sa = endpointSessionMap.get(cl);
        if (sa == null) {
            return null;
        }

        if (tc.isDebugEnabled()) {
            Tr.debug(tc, "getOpenSessions is using set of: " + sa.hashCode());
        }

        final HashSet<Session> set = new HashSet<Session>();
        set.addAll(sa);

        return set;

    }

    public void httpSessionExpired(final String httpSessionID) {
        if (httpSessionID != null) {
            final ArrayList<Session> sessionList = httpSessionMap.remove(httpSessionID);
            if (sessionList != null) {
                // can't use HttpSession object on another thread during this processing (which we do if CDI 1.0 is enabled on the complete or error
                // callbacks) or else the Session API may deadlock.   Don't really need to do anything anyway with this session object since
                // it is invalidating.
                for (final Session session : sessionList) {
                    ((SessionExt) session).getSessionImpl().markHttpSessionInvalid();
                    if (session.isSecure() && (session.getUserPrincipal() != null)) {
                        final CloseReason cr = new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "Secure HTTP Session Closed");
                        ((SessionExt) session).getSessionImpl().close(cr, true);
                    }
                }
            }

            final Set<Entry<Class<?>, ArrayList<Session>>> s = endpointSessionMap.entrySet();
            final Iterator<Entry<Class<?>, ArrayList<Session>>> i = s.iterator();
            Class<?> endPointKey = null;

            // find the key that contains our expired session
            while (i.hasNext()) {
                final Entry<Class<?>, ArrayList<Session>> e = i.next();
                endPointKey = e.getKey();
                for (final Session session : sessionList) {
                    if (e.getValue().contains(session)) {
                        break;
                    }
                }
            }

            //remove that expired session from the endpointSessionMap
            if (endPointKey != null) {
                final ArrayList<Session> http_sa = endpointSessionMap.get(endPointKey);
                for (final Session session : sessionList) {
                    http_sa.remove(session);
                }
                endpointSessionMap.put(endPointKey, http_sa);
                if (tc.isDebugEnabled()) {
                    Tr.debug(tc, "removed expired session of: " + httpSessionID + "  from endpoint class of: " + endPointKey.getName() + " with list " + http_sa
                                 + " in endpointmanager of: " + this);
                }
            }
        }
    }

    public void addAnnotatedEndpoint(final AnnotatedEndpoint annotatedEP) {
        // If this happens to get called twice ( two different requests) during IBM API upgrade call, it will not matter..
        if (annotatedEP != null) {
            annotatedEndpointMap.put(annotatedEP.getServerEndpointClass(), annotatedEP);
        }
    }

    public AnnotatedEndpoint getAnnotatedEndpoint(final Class<? extends Object> aep) {
        if (aep != null) {
            return annotatedEndpointMap.get(aep);
        } else {
            return null;
        }
    }

    public void addServerEndpointConfig(final ServerEndpointConfig serverEndpointConfig) {
        final String path = serverEndpointConfig.getPath();
        if (path != null) {
            Tr.info(tc, "adding.endpoint", path);
            serverEndpointConfigMap.put(path, serverEndpointConfig);
        }
    }

    public boolean isURIExists(final String path) {
        if (serverEndpointConfigMap.keySet().contains(path)) { //found exact match
            return true;
        } else {
            return false;
        }
    }

    public ServerEndpointConfig getServerEndpointConfig(final String path) {
        String matchedURI = null;
        ServerEndpointConfig config = null;
        if (serverEndpointConfigMap.keySet().contains(path)) { //found exact match
            matchedURI = path;
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "found a exact URI match of: " + matchedURI);
            }
        } else {
            matchedURI = getMatchingURI(path); //find next best match
        }
        if (matchedURI != null) {
            config = serverEndpointConfigMap.get(matchedURI);
        }

        return config;
    }

    /*
     * JSR 356, section 3.1.1 URI Mapping
     */
    private synchronized String getMatchingURI(final String incomingURI) {
        final String[] incomingURIParts = incomingURI.split("/");

        final ArrayList<String[]> tockenizedEndpoints = new ArrayList<String[]>();
        for (final String endpointURI : serverEndpointConfigMap.keySet()) {
            // Since the endpoint paths are either relative URIs or URI templates level 1, the paths do not match if they 
            // do not have the same number of segments, using ’/’ as the separator.
            if (incomingURIParts.length == endpointURI.split("/").length) {
                tockenizedEndpoints.add(endpointURI.split("/"));
            }
        }

        /**
         * For each segment of the incoming uri, compare the corresponding segment of every endpoint uri. If matched (exact match or variable {} match)
         * add that endpoint to the list of matchedURI. Move to next segment of the incoming uri and do the same match. At the end of this loop
         * we have list of possible matched endpoints for the incoming URI
         * For e.g if the incoming uri = a/b/d and endpoints are /{var}/b/d, /a/x/y, {var}/b/{var}, /a/{var}/{var}, /{var}/{var}/{var} then
         * possible matchedURIs are /{var}/b/d, {var}/b/{var}, /a/{var}/{var}, /{var}/{var}/{var}
         */
        for (int i = 1; i < incomingURIParts.length; i++) { // start with i=1 skipping the first part because first part of split("/") for /../../.. is always an empty string 
            final ArrayList<String[]> matchedURIs = new ArrayList<String[]>();
            // for a each segment in incomingURI, check the corresponding segment of every endpoint.
            for (int j = 0; j < tockenizedEndpoints.size(); j++) {
                final String[] tockens = tockenizedEndpoints.get(j);
                if (incomingURIParts[i].equals(tockens[i])) {
                    matchedURIs.add(tockenizedEndpoints.get(j));
                }
                if (tockens[i].startsWith("{") && (tockens[i].endsWith("}"))) {
                    matchedURIs.add(tockenizedEndpoints.get(j));
                }
            }
            tockenizedEndpoints.clear();
            tockenizedEndpoints.addAll(matchedURIs);
        }

        //sort matched endpoints in order of preference from left to right for exact match at each segment giving a higher precedence 
        if (tockenizedEndpoints.size() > 1) {
            Collections.sort(tockenizedEndpoints, COMPARATOR);
        }

        String[] tockenizedEndpoint = null;
        if (!tockenizedEndpoints.isEmpty()) {
            tockenizedEndpoint = tockenizedEndpoints.get(0); //best match is always the top element in the sorted list
        }

        if (tockenizedEndpoint != null) {
            return unTockenizeEp(tockenizedEndpoint);
        } else {
            return null;
        }
    }

    private String unTockenizeEp(final String[] tockenizedEp) {
        final String delimiter = "/";
        final StringBuffer buffer = new StringBuffer();
        for (final String part : tockenizedEp) {
            if (!part.isEmpty()) {
                buffer.append(delimiter + part);
            }
        }
        return buffer.toString();
    }

    /**
     * sort matched endpoints in order of preference looking for exact matches at each segment. Exact match at each segment takes
     * precedence over variable match{var}
     * e.g before sorting --> /{var}/b/d, {var}/b/{var}, /a/{var}/{var}, /{var}/{var}/{var}
     * after sorting --> /a/{var}/{var}, /{var}/b/d, {var}/b/{var}, /{var}/{var}/{var}
     */
    private static final Comparator<String[]> COMPARATOR = new Comparator<String[]>() {
        @Override
        public int compare(final String[] endpoint1Parts, final String[] endpoint2Parts) {
            for (int i = 1; i < endpoint1Parts.length; i++) { // start with i=1 skipping the first part because first part of split("/") for /../../.. is always an empty string
                final boolean var1 = endpoint1Parts[i].startsWith("{") && endpoint1Parts[i].endsWith("}");
                final boolean var2 = endpoint2Parts[i].startsWith("{") && endpoint2Parts[i].endsWith("}");
                if (var1 != var2) {
                    return var1 ? 1 : -1;
                }

                final int compare = endpoint1Parts[i].compareTo(endpoint2Parts[i]);
                if (compare != 0) {
                    return compare;
                }
            }

            return 0;
        }
    };
}
