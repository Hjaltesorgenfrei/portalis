package parseview;

import com.portalis.lib.Book;
import com.portalis.lib.NetUtil;
import com.portalis.lib.Parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;

public class FileWatcher extends Thread {
    private final File file;
    private final AtomicBoolean stop = new AtomicBoolean(false);
    public String htmlContent;
    private String url;
    private final TreeItem<String> rootItem;

    public FileWatcher(File file, TreeItem<String> rootItem) {
        this.file = file;
        this.rootItem = rootItem;
        try {
            url = Objects.requireNonNull(getParser()).getTopRated();
            getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        doOnChange();
    }

    private void getContent() throws IOException {
        htmlContent = NetUtil.Companion.get(url);
    }

    public boolean isStopped() {
        return stop.get();
    }

    public void stopThread() {
        stop.set(true);
    }

    public void doOnChange() {
        try {
            Parser parser = getParser();
            if (parser == null) return;
            if (!parser.getTopRated().equals(url)) {
                url = Objects.requireNonNull(getParser()).getTopRated();
                getContent();
            }
            var books = parser.parseOverview(htmlContent);
            Platform.runLater(() -> {
                rootItem.getChildren().clear();
                addBooks(rootItem, books);
            });
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Parser getParser() throws IOException {
        var content = new String(Files.readAllBytes(file.toPath()));
        if (content.strip().length() < 2) return null;
        return new Parser(content);
    }

    private void addBooks(TreeItem<String> root, List<Book> books) {
        for (var book : books) {
            var bookItem = new TreeItem<>("Title: " + book.getTitle());
            bookItem.getChildren().add(new TreeItem<>("Uri: " + book.getUri()));
            bookItem.getChildren().add(new TreeItem<>("ImageUri: " + book.getImageUri()));
            root.getChildren().add(bookItem);
        }
    }

    @Override
    public void run() {
        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
            Path path = file.toPath().getParent();
            path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
            while (!isStopped()) {
                WatchKey key;
                try {
                    key = watcher.poll(25, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    return;
                }
                if (key == null) {
                    Thread.yield();
                    continue;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filename = ev.context();

                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        Thread.yield();
                        continue;
                    } else if (kind == java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY
                            && filename.toString().equals(file.getName())) {
                        doOnChange();
                    }
                    boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }
                }
                Thread.yield();
            }
        } catch (Throwable e) {
            // Log or rethrow the error
        }
    }
}