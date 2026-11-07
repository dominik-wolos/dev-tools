package pl.edu.devtools.core.urls.observers;

import pl.edu.devtools.core.urls.UrlGroup;

/** Tydzień 3, punkt 3: ISP — stan dla odbiorców bez zarządzania subskrypcjami. */
public interface UrlSessionState {
    UrlGroup batch();

    boolean canEdit();

    boolean canUndo();
}
