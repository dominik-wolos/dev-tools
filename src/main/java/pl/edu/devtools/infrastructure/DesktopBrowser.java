package pl.edu.devtools.infrastructure;

import pl.edu.devtools.core.Browser;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

/**
 * Lab 1, zadanie 4: Adapter, zastosowanie 2.
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
/** Koniec, Lab 1, zadanie 4: Adapter, zastosowanie 2. */
