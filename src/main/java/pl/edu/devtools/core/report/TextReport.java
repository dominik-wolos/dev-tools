package pl.edu.devtools.core.report;

import java.util.List;

/**
 * Tydzień 2, Wzorzec 9: Template Method, zastosowanie 3.
 * Podklasa dostarcza krok renderowania TXT do wspólnego szablonu eksportu.
 */
public final class TextReport extends Report {
    public TextReport(ReportOutput output) {
        super(output);
    }

    @Override
    protected String render(List<String> lines) {
        return String.join(System.lineSeparator(), lines);
    }
}
/** Koniec, Tydzień 2, Wzorzec 9: Template Method, zastosowanie 3. */
