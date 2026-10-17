package pl.edu.devtools.infrastructure;

import pl.edu.devtools.core.Browser;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

/**
 * Tydzień 1, Wzorzec 4/9: Adapter, zastosowanie 2.
 * Udostępnia systemową przeglądarkę przez interfejs Browser używany przez logikę aplikacji.
 */
public final class DesktopBrowser implements Browser {
    @Override
    public void open(URI uri) throws IOException {
        if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            throw new IOException("System nie udostępnia przeglądarki.");
        }
        Desktop.getDesktop().browse(uri);
    }
}
/** Koniec, Tydzień 1, Wzorzec 4/9: Adapter, zastosowanie 2.*/
