/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ut.biolab.medsavant;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ut.biolab.medsavant.shared.model.SessionExpiredException;
import org.ut.biolab.medsavant.shared.serverapi.MedSavantServerRegistry;
import org.ut.biolab.medsavant.shared.serverapi.SessionManagerAdapter;

/**
 *
 * @author Miroslav Cupak (mirocupak@gmail.com)
 * @version 1.0
 */
public class ConnectionController {

    private static final Logger LOG = Logger.getLogger(ConnectionController.class.getName());
    private final ConnectionDetails connectionDetails;
    private SessionManagerAdapter sessionManager;
    private String sessionId = null;

    public ConnectionController(ConnectionDetails connectionDetails) {
        this.connectionDetails = connectionDetails;

        initAdapters();
    }

    private void initAdapters() {
        Registry registry = null;

        try {
            registry = LocateRegistry.getRegistry(connectionDetails.getHost(), connectionDetails.getPort());
            try {
                sessionManager = (SessionManagerAdapter) registry.lookup(MedSavantServerRegistry.SESSION_MANAGER);
            } catch (NotBoundException ex) {
                LOG.log(Level.SEVERE, "SessionManager lookup failed.", ex);
            } catch (AccessException ex) {
                LOG.log(Level.SEVERE, "SessionManager lookup failed.", ex);
            }
        } catch (RemoteException ex) {
            LOG.log(Level.SEVERE, "Server lookup failed.", ex);
        }
    }

    public boolean connect() {
        if (sessionManager != null) {
            try {
                sessionId = sessionManager.registerNewSession(connectionDetails.getUsername(), connectionDetails.getPassword(), connectionDetails.getDb());
                sessionManager.testConnection(sessionId);
                return true;
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, null, ex);
            } catch (SessionExpiredException ex) {
                LOG.log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }

        return false;
    }

    public void disconnect() {
        if (sessionManager != null && sessionId != null) {
            try {
                sessionManager.unregisterSession(sessionId);
            } catch (RemoteException ex) {
                LOG.log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, null, ex);
            } catch (SessionExpiredException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
    }

    public String getSessionId() {
        return sessionId;
    }
}
