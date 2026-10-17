package pl.edu.devtools.core.report;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Tydzień 1, Wzorzec 9/9: Bridge, zastosowanie 1.
 * Formaty TextReport/JsonReport są niezależne od miejsca zapisu ReportOutput: pliku albo schowka.
 * Konkretne implementacje ReportOutput znajdują się w osobnych plikach pakietu infrastructure.
 */
public abstract class Report {
    private final ReportOutput output;

    protected Report(ReportOutput output) {
        this.output = Objects.requireNonNull(output);
    }

    /**
     * Tydzień 2, Wzorzec 9: Template Method, zastosowanie 1.
     * Stały przebieg: walidacja, renderowanie przez podklasę, zapis przez ReportOutput.
     */
    public final void export(List<String> lines) throws IOException {
        validate(lines);
        output.write(render(lines));
    }

    private void validate(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("Brak wyniku do eksportu.");
        }
    }

    protected abstract String render(List<String> lines) throws IOException;
    /** Koniec, Tydzień 2, Wzorzec 9: Template Method, zastosowanie 1. */
}
/** Koniec, Tydzień 1, Wzorzec 9/9: Bridge, zastosowanie 1. */
