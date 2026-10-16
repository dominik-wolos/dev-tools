package pl.edu.devtools.infrastructure;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import pl.edu.devtools.core.Report;

import java.io.IOException;

// Tydzień 1, Wzorzec 9/9: Bridge, zastosowanie 3.
// Implementacja miejsca zapisu: schowek JavaFX, niezależnie od formatu raportu.
public final class ClipboardReportOutput implements Report.Output {
    @Override
    public void write(String text) throws IOException {
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        if (!Clipboard.getSystemClipboard().setContent(content)) {
            throw new IOException("Schowek jest niedostępny.");
        }
    }
}
// Koniec, Tydzień 1, Wzorzec 9/9: Bridge, zastosowanie 3.
