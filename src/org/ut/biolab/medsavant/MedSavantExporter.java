package org.ut.biolab.medsavant;

import com.dnanexus.DXUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
        private Boolean success;

        public Output(Boolean success) {
            this.success = success;
        }
    }

    private static ConnectionDetails connection;
    private static UploadDetails upload;
    private static UserDetails user;

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

    private static void readInput(Input input) {
        readConnection(input);
        readProject(input);
        readUser(input);
    }

    private static void writeFile(String fileName, String s) {
        FileWriter output = null;
        try {
            try {
                output = new FileWriter(fileName, false);
                BufferedWriter writer = new BufferedWriter(output);
                writer.write(s);
                writer.flush();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    // Ignore issues during closing
                }
            }
        }
    }

    /**
     * Main function in charge of the workflow control.
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        final String fileName = "test.vcf";
        boolean success = false;

        // read input
        Input input = DXUtil.getJobInput(Input.class);
        readInput(input);

        // connect
        ServerController c = new ServerController(connection);
        if (c.connect()) {
            // generate sample VCF
            writeFile(fileName, "##fileformat=VCFv4.1\n"
                    + "##INFO=<ID=LDAF,Number=1,Type=Float,Description=\"MLE Allele Frequency Accounting for LD\">\n"
                    + "##INFO=<ID=AVGPOST,Number=1,Type=Float,Description=\"Average posterior probability from MaCH/Thunder\">\n"
                    + "##INFO=<ID=RSQ,Number=1,Type=Float,Description=\"Genotype imputation quality from MaCH/Thunder\">\n"
                    + "##INFO=<ID=ERATE,Number=1,Type=Float,Description=\"Per-marker Mutation rate from MaCH/Thunder\">\n"
                    + "##INFO=<ID=THETA,Number=1,Type=Float,Description=\"Per-marker Transition rate from MaCH/Thunder\">\n"
                    + "##INFO=<ID=CIEND,Number=2,Type=Integer,Description=\"Confidence interval around END for imprecise variants\">\n"
                    + "##INFO=<ID=CIPOS,Number=2,Type=Integer,Description=\"Confidence interval around POS for imprecise variants\">\n"
                    + "##INFO=<ID=END,Number=1,Type=Integer,Description=\"End position of the variant described in this record\">\n"
                    + "##INFO=<ID=HOMLEN,Number=.,Type=Integer,Description=\"Length of base pair identical micro-homology at event breakpoints\">\n"
                    + "##INFO=<ID=HOMSEQ,Number=.,Type=String,Description=\"Sequence of base pair identical micro-homology at event breakpoints\">\n"
                    + "##INFO=<ID=SVLEN,Number=1,Type=Integer,Description=\"Difference in length between REF and ALT alleles\">\n"
                    + "##INFO=<ID=SVTYPE,Number=1,Type=String,Description=\"Type of structural variant\">\n"
                    + "##INFO=<ID=AC,Number=.,Type=Integer,Description=\"Alternate Allele Count\">\n"
                    + "##INFO=<ID=AN,Number=1,Type=Integer,Description=\"Total Allele Count\">\n"
                    + "##ALT=<ID=DEL,Description=\"Deletion\">\n"
                    + "##FORMAT=<ID=GT,Number=1,Type=String,Description=\"Genotype\">\n"
                    + "##FORMAT=<ID=DS,Number=1,Type=Float,Description=\"Genotype dosage from MaCH/Thunder\">\n"
                    + "##FORMAT=<ID=GL,Number=.,Type=Float,Description=\"Genotype Likelihoods\">\n"
                    + "##INFO=<ID=AA,Number=1,Type=String,Description=\"Ancestral Allele, ftp://ftp.1000genomes.ebi.ac.uk/vol1/ftp/pilot_data/technical/reference/ancestral_alignments/README\">\n"
                    + "##INFO=<ID=AF,Number=1,Type=Float,Description=\"Global Allele Frequency based on AC/AN\">\n"
                    + "##INFO=<ID=AMR_AF,Number=1,Type=Float,Description=\"Allele Frequency for samples from AMR based on AC/AN\">\n"
                    + "##INFO=<ID=ASN_AF,Number=1,Type=Float,Description=\"Allele Frequency for samples from ASN based on AC/AN\">\n"
                    + "##INFO=<ID=AFR_AF,Number=1,Type=Float,Description=\"Allele Frequency for samples from AFR based on AC/AN\">\n"
                    + "##INFO=<ID=EUR_AF,Number=1,Type=Float,Description=\"Allele Frequency for samples from EUR based on AC/AN\">\n"
                    + "##INFO=<ID=VT,Number=1,Type=String,Description=\"indicates what type of variant the line represents\">\n"
                    + "##INFO=<ID=SNPSOURCE,Number=.,Type=String,Description=\"indicates if a snp was called when analysing the low coverage or exome alignment data\">\n"
                    + "##reference=GRCh37\n"
                    + "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\n"
                    + "1\t10583\trs58108140\tG\tA\t100\tPASS\tAVGPOST=0.7707;RSQ=0.4319;LDAF=0.2327;ERATE=0.0161;AN=2184;VT=SNP;AA=.;THETA=0.0046;AC=314;SNPSOURCE=LOWCOV;AF=0.14;ASN_AF=0.13;AMR_AF=0.17;AFR_AF=0.04;EUR_AF=0.21\n");

            // upload file and import variants
            success = c.processVariants(fileName, upload, user);

            // disconnect
            c.disconnect();

            //clean up
            File f = new File(fileName);
            f.delete();
        }

        // write output
        DXUtil.writeJobOutput(new Output(success));
    }

}
