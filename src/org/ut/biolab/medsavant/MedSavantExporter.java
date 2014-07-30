package org.ut.biolab.medsavant;

import com.dnanexus.DXUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.IOException;

/**
 * Main entry point.
 *
 *
 * @author Miroslav Cupak (mirocupak@gmail.com)
 */
public class MedSavantExporter {

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Input {

        @JsonProperty
        private String host;
        @JsonProperty
        private Integer port;
        @JsonProperty
        private String db;
        @JsonProperty
        private String project;
        @JsonProperty
        private String username;
        @JsonProperty
        private String password;
        @JsonProperty
        private String referenceGenome;
    }

    private static class Output {

        @JsonProperty
        private String status;

        public Output(String status) {
            this.status = status;
        }
    }

    private static Connection connection;
    private static Project project;

    private static void readConnection(Input input) {
        connection = new Connection();
        connection.setHost(input.host);
        connection.setPort(input.port);
        connection.setDb(input.db);
        connection.setUsername(input.username);
        connection.setPassword(input.password);
    }

    private static void readProject(Input input) {
        project = new Project();
        project.setProject(input.project);
        project.setReferenceGenome(input.referenceGenome);
    }

    private static void readInput(Input input) {
        readConnection(input);
        readProject(input);
    }

    public static void main(String[] args) throws IOException {
        Input input = DXUtil.getJobInput(Input.class);

        readInput(input);

        String status = "OK " + project.getProject();
        DXUtil.writeJobOutput(new Output(status));
    }

}
