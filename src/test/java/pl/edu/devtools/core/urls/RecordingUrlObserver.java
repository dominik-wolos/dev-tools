package pl.edu.devtools.core.urls;

import pl.edu.devtools.core.urls.observers.UrlSessionObserver;
import pl.edu.devtools.core.urls.observers.UrlSessionObservable;

import java.util.ArrayList;
import java.util.List;

final class RecordingUrlObserver implements UrlSessionObserver {
    final List<Integer> sizes = new ArrayList<>();
    final List<Boolean> editable = new ArrayList<>();
    final List<Boolean> undo = new ArrayList<>();

    @Override
    public void sessionChanged(UrlSessionObservable session) {
        sizes.add(session.batch() == null ? -1 : session.batch().urls().size());
        editable.add(session.canEdit());
        undo.add(session.canUndo());
    }
}
