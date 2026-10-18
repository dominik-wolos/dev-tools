package pl.edu.devtools.core;

import pl.edu.devtools.core.json.operations.Operation;

import java.io.IOException;

public interface JsonTool {
    String execute(String input, Operation operation) throws IOException;
}
