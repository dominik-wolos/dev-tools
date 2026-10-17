package pl.edu.devtools.core.json.operations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tydzień 2, Wzorzec 8: Strategy, zastosowanie 3.
 * Konkretna strategia potwierdza poprawność danych sprawdzonych przez parser.
 */
public final class Validate implements Operation {
    @Override
    public String apply(JsonNode value, ObjectMapper mapper) {
        return "JSON jest poprawny.";
    }
}
/** Koniec, Tydzień 2, Wzorzec 8: Strategy, zastosowanie 3. */
