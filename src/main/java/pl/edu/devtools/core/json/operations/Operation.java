package pl.edu.devtools.core.json.operations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Tydzień 2, Wzorzec 8: Strategy, zastosowanie 1.
 * Wspólny interfejs wymiennych strategii walidacji, formatowania i minifikacji.
 */
public interface Operation {
    Operation VALIDATE = new Validate();
    Operation FORMAT = new Format();
    Operation MINIFY = new Minify();

    String apply(JsonNode value, ObjectMapper mapper) throws IOException;
}
/** Koniec, Tydzień 2, Wzorzec 8: Strategy, zastosowanie 1. */
