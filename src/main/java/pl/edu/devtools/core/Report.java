package pl.edu.devtools.core;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

/**
 * Tydzień 1, Wzorzec 9/9: Bridge, zastosowanie 1.
 * Formaty Text/Json są niezależne od miejsca zapisu Output: pliku albo schowka.
 * Konkretne implementacje Output znajdują się w osobnych plikach pakietu infrastructure.
 */
public abstract class Report {
    @FunctionalInterface
    public interface Output {
        void write(String text) throws IOException;
    }

    private final Output output;

    protected Report(Output output) {
        this.output = output;
    }

    public final void export(List<String> lines) throws IOException {
        output.write(render(lines));
    }

    public abstract String render(List<String> lines) throws IOException;

    public static final class Text extends Report {
        public Text(Output output) {
            super(output);
        }

        @Override
        public String render(List<String> lines) {
            return String.join(System.lineSeparator(), lines);
        }
    }

    public static final class Json extends Report {
        private final ObjectMapper mapper;

        public Json(Output output, ObjectMapper mapper) {
            super(output);
            this.mapper = mapper;
        }

        @Override
        public String render(List<String> lines) throws IOException {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(lines);
        }
    }
}
// Koniec, Tydzień 1, Wzorzec 9/9: Bridge, zastosowanie 1.
