import com.dnanexus.DXUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.IOException;

public class MedSavantExporter {

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class HelloWorldInput {
        @JsonProperty
        private String name;
    }

    private static class HelloWorldOutput {
        @JsonProperty
        private String greeting;

        public HelloWorldOutput(String greeting) {
            this.greeting = greeting;
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("This is the DNAnexus Java Demo App");

        HelloWorldInput input = DXUtil.getJobInput(HelloWorldInput.class);

        String name = input.name;
        String greeting = "Hello, " + (name == null ? "World" : name) + "!";

        DXUtil.writeJobOutput(new HelloWorldOutput(greeting));
    }

}
