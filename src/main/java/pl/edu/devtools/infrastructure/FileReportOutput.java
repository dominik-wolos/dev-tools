package pl.edu.devtools.infrastructure;

import pl.edu.devtools.core.Report;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Tydzień 1, Wzorzec 9/9: Bridge, zastosowanie 2.
 * Implementacja miejsca zapisu; nie zna formatu TXT/JSON ani kontrolek aplikacji.
 */
public final class FileReportOutput implements Report.Output {
    private final Path path;

    public FileReportOutput(Path path) {
        this.path = path;
    }

    @Override
    public void write(String text) throws IOException {
        Files.writeString(path, text, StandardCharsets.UTF_8);
    }
}
/** Koniec, Tydzień 1, Wzorzec 9/9: Bridge, zastosowanie 2.*/
