package pl.edu.devtools.core.report;

import java.io.IOException;
import java.util.List;

/** Tydzień 3, punkt 2: DIP — klient eksportuje raport bez znajomości jego formatu. */
public interface ReportExporter {
    void export(List<String> lines) throws IOException;
}
