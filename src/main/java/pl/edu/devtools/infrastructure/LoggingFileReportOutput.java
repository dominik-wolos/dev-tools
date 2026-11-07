package pl.edu.devtools.infrastructure;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * Tydzień 3, Stosowanie zasad SOLID, punkt 1: Liskov, zastosowanie 3.
 * Dodaje diagnostykę do FileReportOutput bez zmiany treści, kodowania i obsługi błędów zapisu.
 */
public final class LoggingFileReportOutput extends FileReportOutput {
    private static final Logger LOGGER = Logger.getLogger(LoggingFileReportOutput.class.getName());

    public LoggingFileReportOutput(Path path) {
        super(path);
    }

    @Override
    public void write(String text) throws IOException {
        super.write(text);
        LOGGER.fine(() -> "Zapisano raport, znaków: " + text.length());
    }
}
/** Koniec, Tydzień 3, punkt 1: Liskov, zastosowanie 3. */
