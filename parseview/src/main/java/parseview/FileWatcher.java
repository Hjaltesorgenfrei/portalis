package parseview;

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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class FileWatcher extends Thread {
    private final File file;
    public final AtomicBoolean stop = new AtomicBoolean(false);
    private final TreeItem<String> rootItem;

    public FileWatcher(File file, TreeItem<String> rootItem) {
        this.file = file;
        this.rootItem = rootItem;
    }

    public boolean isStopped() {
        return stop.get();
    }

    public void stopThread() {
        stop.set(true);
    }

    public void doOnChange() {
        try {
            var content = new String(Files.readAllBytes(file.toPath()));
            System.out.println(content);
            var parser = new Parser(content);
            var bookSelector = parser.getBookSelector();
            Platform.runLater(() -> {
                rootItem.getChildren().clear();
                rootItem.getChildren().add(new TreeItem<>(bookSelector.getBook()));
                rootItem.getChildren().add(new TreeItem<>(bookSelector.getTitle()));
                rootItem.getChildren().add(new TreeItem<>(bookSelector.getUri()));
                rootItem.getChildren().add(new TreeItem<>(bookSelector.getImageUri()));
            });
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
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