package pl.edu.devtools.ui.urls.observers;

import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import pl.edu.devtools.core.UrlNode;
import pl.edu.devtools.core.urls.UrlListing;
import pl.edu.devtools.core.urls.UrlGroup;
import pl.edu.devtools.core.urls.observers.UrlSessionState;
import pl.edu.devtools.core.urls.observers.UrlSessionObserver;

/** Lab 2, zadanie 6: Observer, zastosowanie 4 — odbiorca odświeża drzewo i licznik. */
public record UrlPreviewObserver(TreeView<UrlNode> preview, Label status) implements UrlSessionObserver {
    @Override
    public void sessionChanged(UrlSessionState session) {
        UrlGroup batch = session.batch();
        preview.setRoot(batch == null ? null : tree(batch));
        if (batch != null) {
            showCount(batch);
        }
    }

    private void showCount(UrlListing addresses) {
        status.setText("Adresów: " + addresses.urls().size() + ". Każdy wiersz listy tworzy grupę.");
    }

    private TreeItem<UrlNode> tree(UrlNode node) {
        TreeItem<UrlNode> item = new TreeItem<>(node);
        if (node instanceof UrlGroup group) {
            group.children().forEach(child -> item.getChildren().add(tree(child)));
        }
        item.setExpanded(true);
        return item;
    }
}
