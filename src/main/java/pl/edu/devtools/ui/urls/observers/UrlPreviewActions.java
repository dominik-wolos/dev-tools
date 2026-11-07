package pl.edu.devtools.ui.urls.observers;

import javafx.scene.control.Button;
import pl.edu.devtools.core.urls.observers.UrlSessionState;

/** Tydzień 3, punkt 8 — powiązane akcje edycji podglądu. */
public record UrlPreviewActions(Button remove, Button restore, Button undo) {
    public void update(UrlSessionState session) {
        boolean editablePreview = session.canEdit() && session.batch() != null;
        remove.setDisable(!editablePreview);
        restore.setDisable(!editablePreview);
        undo.setDisable(!session.canUndo());
    }
}
