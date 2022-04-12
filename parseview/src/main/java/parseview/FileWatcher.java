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
    private String topRatedUrl;
    private String bookUrl = null;
    private final TreeItem<String> rootItem;

    public FileWatcher(File file, TreeItem<String> rootItem) {
        this.file = file;
        this.rootItem = rootItem;
        try {
            topRatedUrl = Objects.requireNonNull(getParser()).getTopRated();
            getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        doOnChange();
    }

    private void getContent() throws IOException {
        if (bookUrl != null) {
            htmlContent = NetUtil.Companion.get(bookUrl);
            return;
        }
        htmlContent = NetUtil.Companion.get(topRatedUrl);
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
            if (!parser.getTopRated().equals(topRatedUrl)) {
                topRatedUrl = Objects.requireNonNull(getParser()).getTopRated();
                getContent();
            }
            if (bookUrl != null) {
                extractChapters(parser);
            }
            else {
                extractBooks(parser);
            }
            System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void extractChapters(Parser parser) {
        var book = parser.parseBook(htmlContent);
        Platform.runLater(() -> {
            rootItem.getChildren().clear();
            addBook(rootItem, book);
        });
    }

    private void addBook(TreeItem<String> root, Book book) {
        root.getChildren().add(new TreeItem<>(book.getTitle()));
        root.getChildren().add(new TreeItem<>(book.getImageUri()));
        root.getChildren().add(new TreeItem<>(book.getUri()));
        for (var chapter : book.getChapters()) {
            var bookItem = new TreeItem<>("Title: " + chapter.getTitle());
            bookItem.getChildren().add(new TreeItem<>("ChapterUri: " + chapter.getUri()));
            bookItem.getChildren().add(new TreeItem<>("ChapterDate: " + chapter.getDate()));
            root.getChildren().add(bookItem);
        }
    }

    private void extractBooks(Parser parser) {
        var books = parser.parseOverview(htmlContent);
        Platform.runLater(() -> {
            rootItem.getChildren().clear();
            addBooks(rootItem, books);
        });
    }

    private Parser getParser() throws IOException {
        var content = new String(Files.readAllBytes(file.toPath()));
        if (content.strip().length() < 2) return null;
        return new Parser(content);
    }

    private void addBooks(TreeItem<String> root, List<Book> books) {
        for (var book : books) {
            var bookItem = new TreeItem<>("Title: " + book.getTitle());
            bookItem.getChildren().add(new TreeItem<>("BookUri: " + book.getUri()));
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
            e.printStackTrace();
        }
    }

    public void switchToChapterView(String bookUrl) {
        this.bookUrl = bookUrl;
        try {
            getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        doOnChange();
    }
}

