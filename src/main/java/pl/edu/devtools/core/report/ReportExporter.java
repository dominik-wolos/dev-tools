package pl.edu.devtools.core.report;

import java.io.IOException;
import java.util.List;

/** Lab 3, zadanie 2: DIP, zastosowanie 2 — klient eksportuje raport bez znajomości jego formatu. */
public interface ReportExporter {
    void export(List<String> lines) throws IOException;
}
