package pl.edu.devtools.core.json.operations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Lab 2, zadanie 8: Strategy, zastosowanie 1.
 * Wspólny interfejs wymiennych strategii walidacji, formatowania i minifikacji.
 */
public interface Operation {
    Operation VALIDATE = new Validate();
    Operation FORMAT = new Format();
    Operation MINIFY = new Minify();

    String apply(JsonNode value, ObjectMapper mapper) throws IOException;
}
/** Koniec, Lab 2, zadanie 8: Strategy, zastosowanie 1. */
