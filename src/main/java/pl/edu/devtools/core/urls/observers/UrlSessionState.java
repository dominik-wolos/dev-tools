package pl.edu.devtools.core.urls.observers;

import pl.edu.devtools.core.urls.UrlGroup;

/** Lab 3, zadanie 3: ISP, zastosowanie 6 — stan dla odbiorców bez zarządzania subskrypcjami. */
public interface UrlSessionState {
    UrlGroup batch();

    boolean canEdit();

    boolean canUndo();
}
