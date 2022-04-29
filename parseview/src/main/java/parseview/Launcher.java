package parseview;

import com.portalis.lib.schema.Schema;
import java.io.FileWriter;
import java.io.IOException;

public class Launcher {
    // This class is needed due to javafx dying when launching a Application as main.
    // So this class should be used as entry point
    public static void main(String[] args) throws IOException {
        var file = new FileWriter("schema.json");
        file.write(Schema.INSTANCE.prettyPrintedSchema());
        file.flush();
        App.main(args);
    }
}