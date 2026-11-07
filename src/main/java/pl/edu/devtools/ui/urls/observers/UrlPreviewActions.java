package pl.edu.devtools.ui.urls.observers;

import javafx.scene.control.Button;
import pl.edu.devtools.core.urls.observers.UrlSessionState;

/** Lab 3, zadanie 8: Maksymalnie 3 argumenty, zastosowanie 4 — powiązane akcje edycji podglądu. */
public record UrlPreviewActions(Button remove, Button restore, Button undo) {
    public void update(UrlSessionState session) {
        boolean editablePreview = session.canEdit() && session.batch() != null;
        remove.setDisable(!editablePreview);
        restore.setDisable(!editablePreview);
        undo.setDisable(!session.canUndo());
    }
}
