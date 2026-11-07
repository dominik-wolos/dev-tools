package pl.edu.devtools.core.report;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Lab 2, zadanie 9: Template Method, zastosowanie 4.
 * Podklasa dostarcza krok renderowania JSON przy użyciu wstrzykniętego mappera.
 */
public final class JsonReport extends Report {
    private final ObjectMapper mapper;

    public JsonReport(ReportOutput output, ObjectMapper mapper) {
        super(output);
        this.mapper = Objects.requireNonNull(mapper);
    }

    @Override
    protected String render(List<String> lines) throws IOException {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(lines);
    }
}
/** Koniec, Lab 2, zadanie 9: Template Method, zastosowanie 4. */
