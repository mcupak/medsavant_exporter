/*
 * Copyright (C) 2014 Miroslav Cupak (mirocupak@gmail.com).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.ut.biolab.medsavant;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ut.biolab.medsavant.shared.model.SessionExpiredException;
import static org.ut.biolab.medsavant.shared.serverapi.MedSavantServerRegistry.NETWORK_MANAGER;
import static org.ut.biolab.medsavant.shared.serverapi.MedSavantServerRegistry.SESSION_MANAGER;
import static org.ut.biolab.medsavant.shared.serverapi.MedSavantServerRegistry.VARIANT_MANAGER;
import org.ut.biolab.medsavant.shared.serverapi.NetworkManagerAdapter;
import org.ut.biolab.medsavant.shared.serverapi.SessionManagerAdapter;
import org.ut.biolab.medsavant.shared.serverapi.VariantManagerAdapter;

/**
 *
 * @author Miroslav Cupak (mirocupak@gmail.com)
 * @version 1.0
 */
public class ServerController {

    private static final Logger LOG = Logger.getLogger(ServerController.class.getName());
    private String sessionId = null;
    private final ConnectionDetails connectionDetails;
    private SessionManagerAdapter sessionManager;
    private NetworkManagerAdapter networkManager;
    private VariantManagerAdapter variantManager;

    public ServerController(ConnectionDetails connectionDetails) {
        this.connectionDetails = connectionDetails;

        initAdapters();
    }

    private void initAdapters() {
        Registry registry = null;

        try {
            registry = LocateRegistry.getRegistry(connectionDetails.getHost(), connectionDetails.getPort());
            try {
                sessionManager = (SessionManagerAdapter) registry.lookup(SESSION_MANAGER);
                networkManager = (NetworkManagerAdapter) registry.lookup(NETWORK_MANAGER);
                variantManager = (VariantManagerAdapter) registry.lookup(VARIANT_MANAGER);
            } catch (NotBoundException ex) {
                LOG.log(Level.SEVERE, "SessionManager lookup failed.", ex);
            } catch (AccessException ex) {
                LOG.log(Level.SEVERE, "SessionManager lookup failed.", ex);
            }
        } catch (RemoteException ex) {
            LOG.log(Level.SEVERE, "Server lookup failed.", ex);
        }
    }

    /**
     * Opens connection to the server.
     *
     * @return true if the connection was opened successfully, false otherwise
     */
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

    /**
     * Closes connection to the server.
     */
    public void disconnect() {
        if (sessionManager != null && sessionId != null) {
            try {
                sessionManager.unregisterSession(sessionId);
            } catch (RemoteException ex) {
                LOG.log(Level.SEVERE, "Error unregistering session", ex);
            } catch (SQLException ex) {
                LOG.log(Level.SEVERE, "Error unregistering session", ex);
            } catch (SessionExpiredException ex) {
                LOG.log(Level.SEVERE, "Error unregistering session", ex);
            }
        }
    }

    /**
     * Copies file to the remote server.
     *
     * @param fileName file path
     * @return stream ID or -1 in case of failure
     */
    private int uploadFile(String fileName) throws IOException, InterruptedException {
        File file = new File(fileName);
        int streamId = -1;
        InputStream stream = null;

        try {
            streamId = networkManager.openWriterOnServer(sessionId, file.getName(), file.length());
            stream = new FileInputStream(file);

            int numBytes;
            byte[] buf = null;
            while ((numBytes = Math.min(stream.available(), NetworkManagerAdapter.BLOCK_SIZE)) > 0) {
                if (buf == null || numBytes != buf.length) {
                    buf = new byte[numBytes];
                }
                stream.read(buf);
                networkManager.writeToServer(sessionId, streamId, buf);
            }
        } finally {
            if (networkManager != null && streamId >= 0) {
                try {
                    networkManager.closeWriterOnServer(sessionId, streamId);
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, "Failed to close the writer.", ex);
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, "Error closing the stream.", ex);
                }
            }
        }

        return streamId;
    }

    /**
     * Imports variants from a file uploaded previously.
     */
    private void importVariants(int streamId, int projId, int refId, boolean includeHomoRef, String email, boolean autoPublish, boolean preAnnotateWithAnnovar) throws Exception {
        boolean doPhasing = false; // not supported yet
        int[] transferIds = new int[]{streamId};
        String[][] tags = new String[0][0]; // for now

        variantManager.uploadTransferredVariants(sessionId, transferIds, projId, refId, tags, includeHomoRef, email, autoPublish, preAnnotateWithAnnovar, doPhasing);
    }

    /**
     * Uploads VCF file and imports the variants.
     *
     * @param fileName name of the file to upload
     * @param upload upload configuration
     * @param user user details
     * @return true if the operation finished successfully, false otherwise
     */
    public boolean processVariants(String fileName, UploadDetails upload, UserDetails user) {
        int streamId = -1;

        // copy file 
        try {
            streamId = uploadFile(fileName);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error uploading file.", ex);
            return false;
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, "Error uploading file.", ex);
            return false;
        }

        if (streamId < 0) {
            return false;
        }

        // import variants
        try {
            importVariants(streamId, upload.getProject(), upload.getRefId(), upload.isIncludeHomoRef(), user.getEmail(), upload.isAutoPublish(), upload.isPreAnnotateWithJannovar());
        } catch (SessionExpiredException sex) {
            // silently ignore
        } catch (NullPointerException nex) {
            // server problem, tyically does not fail the upload - log, but don't fail
            LOG.log(Level.SEVERE, "NPE when importing variants.", nex);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error importing variants.", ex);
            return false;
        }

        return true;
    }

    public String getSessionId() {
        return sessionId;
    }
}
