package pl.edu.devtools.core.json;

import pl.edu.devtools.core.JsonTool;
import pl.edu.devtools.core.json.operations.Operation;

import java.io.IOException;

/** Tydzień 3, punkt 2: DIP — wspólna walidacja, szczegóły przetwarzania dostarcza podklasa. */
public abstract class AbstractJsonTool implements JsonTool {
    @Override
    public String execute(String input, Operation operation) throws IOException {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Wpisz JSON do sprawdzenia.");
        }
        return processDocument(input, operation);
    }

    protected abstract String processDocument(String input, Operation operation) throws IOException;
}
