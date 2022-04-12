package parseview;

import javafx.scene.control.TreeCell;

public class CustomCell extends TreeCell<String> {
    FileWatcher fileWatcher;

    public CustomCell(FileWatcher fileWatcher) {
        this.fileWatcher = fileWatcher;
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        // If the cell is empty we don't show anything.
        if (isEmpty()) {
            setGraphic(null);
            setText(null);
        } else {
            setGraphic(null);
            setText(item);
            if (this.getTreeItem().isLeaf() && item.startsWith("BookUri: ")) {
                this.setOnMouseClicked(e -> {
                    if (e.getClickCount() == 2) {
                        fileWatcher.switchToChapterView(item.replace("BookUri: ", ""));
                    }
                });
            }
        }
    }
}