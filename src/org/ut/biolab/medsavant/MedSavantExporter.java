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

import com.dnanexus.DXFile;
import com.dnanexus.DXUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main entry point.
 *
 *
 * @author Miroslav Cupak (mirocupak@gmail.com)
 */
public class MedSavantExporter {

    private static final Logger LOG = Logger.getLogger(MedSavantExporter.class.getName());

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Input {

        @JsonProperty
        private DXFile vcf;
        @JsonProperty
        private String host;
        @JsonProperty
        private Integer port;
        @JsonProperty
        private String db;
        @JsonProperty
        private Integer project;
        @JsonProperty
        private String username;
        @JsonProperty
        private String password;
        @JsonProperty
        private String email;
        @JsonProperty
        private Integer refId;
        @JsonProperty
        private Boolean includeHomoRef;
        @JsonProperty
        private Boolean autoPublish;
        @JsonProperty
        private Boolean preAnnotateWithJannovar;
    }

    private static class Output {

        @JsonProperty
        private DXFile vcf;

        public Output(DXFile vcf) {
            this.vcf = vcf;
        }
    }

    private static ConnectionDetails connection;
    private static UploadDetails upload;
    private static UserDetails user;
    private static DXFile vcf;

    private static void readConnection(Input input) {
        connection = new ConnectionDetails();
        connection.setHost(input.host);
        connection.setPort(input.port);
        connection.setDb(input.db);
        connection.setUsername(input.username);
        connection.setPassword(input.password);
    }

    private static void readProject(Input input) {
        upload = new UploadDetails();
        upload.setProject(input.project);
        upload.setRefId(input.refId);
        upload.setAutoPublish(input.autoPublish);
        upload.setIncludeHomoRef(input.includeHomoRef);
        upload.setPreAnnotateWithJannovar(input.preAnnotateWithJannovar);
    }

    private static void readUser(Input input) {
        user = new UserDetails();
        user.setEmail(input.email);
    }

    private static void readVcf(Input input) {
        vcf = input.vcf;
    }

    private static void readInput(Input input) {
        readConnection(input);
        readProject(input);
        readUser(input);
        readVcf(input);
    }

    /**
     * Main function in charge of the workflow control.
     *
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Starting.");
        boolean success = false;

        // read input
        Input input = DXUtil.getJobInput(Input.class);
        readInput(input);

        // obtain file from the platform
        System.out.println("Getting VCF file from the platform.");
        try {
            String fileName = FileManager.downloadDXFile(vcf);

            // connect
            System.out.println("Exporting variants to MedSavant.");
            ServerController c = new ServerController(connection);
            try {
                if (c.connect()) {
                    // upload file and import variants
                    success = c.processVariants(fileName, upload, user);
                }
            } finally {
                // disconnect
                c.disconnect();
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error processing variants.", ex);
            success = false;
        }

        if (!success) {
            // mark the build as failed
            System.out.println("Could not process variants.");
            System.exit(1);
        }

        // write output
        System.out.println("Generating output.");
        DXUtil.writeJobOutput(new Output(vcf));
        System.out.println("Done.");
    }

}
