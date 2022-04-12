package parseview;

import java.io.File;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        var root = new TreeItem<String>();
        var fileWatcher = startFileWatcher(stage, root);
        var tree = new TreeView<>(root);
        tree.setCellFactory(e -> new CustomCell(fileWatcher));
        tree.setShowRoot(false);
        Scene scene = new Scene(tree, 640, 480);
        stage.setScene(scene);
        stage.show();
        stage.setOnHidden(e -> {
            fileWatcher.stopThread();
            Platform.exit();
        });
    }

    private FileWatcher startFileWatcher(Stage stage, TreeItem<String> rootItem) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("."));
        fileChooser.setTitle("Open Resource File");
        var file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            System.exit(0);
        }
        var fileWatcher = new FileWatcher(file, rootItem);
        fileWatcher.start();
        return fileWatcher;
    }

    public static void main(String[] args) {
        launch();
    }

}