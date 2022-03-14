package parseview;

import com.portalis.lib.Book;
import com.portalis.lib.Chapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        var root = new TreeItem<String>();
        var fileWatcher = startFileWatcher(stage, root);
        ArrayList<Book> books = getTestData();
        addBooks(root, books);
        var tree = new TreeView<>(root);
        tree.setShowRoot(false);
        Scene scene = new Scene(tree, 640, 480);
        stage.setScene(scene);
        stage.show();
        stage.setOnHidden(e -> {
            fileWatcher.stop.set(true);
            Platform.exit();
        });
    }

    private FileWatcher startFileWatcher(Stage stage, TreeItem<String> rootItem) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        var file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            System.exit(0);
        }
        var fileWatcher = new FileWatcher(file, rootItem);
        fileWatcher.start();
        return fileWatcher;
    }

    private ArrayList<Book> getTestData() {
        var books = new ArrayList<Book>();
        var chapters = new ArrayList<Chapter>();
        for (int i = 0; i < 100; i++) {
            chapters.add(new Chapter("FatBoiSlim " + i, "http://example.com", "0"));
        }
        books.add(new Book("The adventures of FatBoiSlim", "http://example.com", "http://example.com", chapters));
        return books;
    }

    private void addBooks(TreeItem<String> root, ArrayList<Book> books) {
        for (var book : books) {
            var bookItem = new TreeItem<>("Title: " + book.getTitle());
            bookItem.getChildren().add(new TreeItem<>("Uri: " + book.getUri()));
            bookItem.getChildren().add(new TreeItem<>("ImageUri: " + book.getImageUri()));
            var chapterListing = new TreeItem<>("Chapters: " + book.getChapters().size());
            bookItem.getChildren().add(chapterListing);
            addChapters(chapterListing, book.getChapters());
            root.getChildren().add(bookItem);
        }
    }

    private void addChapters(TreeItem<String> chapterListing, List<Chapter> chapters) {
        for (var chapter : chapters) {
            var chapterItem = new TreeItem<>("Chapter Title: " + chapter.getTitle());
            chapterItem.getChildren().add(new TreeItem<>("Chapter Uri: " + chapter.getUri()));

            chapterListing.getChildren().add(chapterItem);
        }
    }

    public static void main(String[] args) {
        launch();
    }

}