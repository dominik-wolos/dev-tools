package pl.edu.devtools.core.json;

import pl.edu.devtools.core.json.operations.Operation;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Lab 3, zadanie 1: Liskov, zastosowanie 1.
 * Dodaje diagnostykę do JacksonJsonTool, zachowując wynik operacji i oryginalne wyjątki.
 */
public final class LoggingJacksonJsonTool extends JacksonJsonTool {
    private static final Logger LOGGER = Logger.getLogger(LoggingJacksonJsonTool.class.getName());

    @Override
    public String execute(String input, Operation operation) throws IOException {
        String result = super.execute(input, operation);
        LOGGER.fine(() -> "Przetworzono JSON, znaków wejściowych: " + input.length());
        return result;
    }
}
/** Koniec, Lab 3, zadanie 1: Liskov, zastosowanie 1. */
