package pl.edu.devtools.ui.urls.observers;

import javafx.scene.control.Control;
import pl.edu.devtools.core.urls.observers.UrlSessionState;
import pl.edu.devtools.core.urls.observers.UrlSessionObserver;

import java.util.List;

/** Tydzień 2, Wzorzec 6: Observer, zastosowanie 5 — odbiorca steruje dostępnością kontrolek. */
public record UrlActionsObserver(List<Control> editors, UrlPreviewActions actions)
        implements UrlSessionObserver {
    public UrlActionsObserver {
        editors = List.copyOf(editors);
    }

    /** Tydzień 2, Wzorzec 7: State, zastosowanie 4 — wywołanie oceny dostępności akcji. */
    @Override
    public void sessionChanged(UrlSessionState session) {
        boolean editable = session.canEdit();
        editors.forEach(control -> control.setDisable(!editable));
        actions.update(session);
    }
}
