package parseview;

import com.portalis.lib.Schema;

public class Launcher {
    // This class is needed due to javafx dying when launching a Application as main.
    // So this class should be used as entry point
    public static void main(String[] args) {
        Schema.INSTANCE.outputSchema();
    }
}