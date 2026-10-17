package pl.edu.devtools.ui.tabs;

import com.fasterxml.jackson.core.JsonProcessingException;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import pl.edu.devtools.core.DevToolsFacade;
import pl.edu.devtools.core.json.operations.Operation;

import java.util.List;

public final class JsonTabView {
    private final DevToolsFacade tools;
    private final TextArea input = new TextArea();
    private final TextArea output = new TextArea();
    private final Label status = new Label("Wpisz JSON i wybierz operację.");
    private boolean valid;

    public JsonTabView(DevToolsFacade tools) {
        this.tools = tools;
    }

    public Tab create() {
        input.setPromptText("{ \"orderID\": 123, \"status\": \"NEW\" }");
        output.setPromptText("Wynik walidacji, formatowania lub minifikacji JSON-a");
        output.setEditable(false);
        input.textProperty().addListener((observable, before, after) -> {
            valid = false;
            output.clear();
            status.setText("Dane zmienione — sprawdź ponownie.");
        });
        Button validate = new Button("Waliduj");
        Button format = new Button("Formatuj");
        Button minify = new Button("Minifikuj");
        Button clear = new Button("Wyczyść");
        validate.setOnAction(event -> run(Operation.VALIDATE));
        format.setOnAction(event -> run(Operation.FORMAT));
        minify.setOnAction(event -> run(Operation.MINIFY));
        clear.setOnAction(event -> {
            input.clear();
            output.clear();
            valid = false;
            status.setText("Wpisz JSON i wybierz operację.");
        });
        SplitPane editors = new SplitPane(input, output);
        editors.setDividerPositions(0.5);
        VBox.setVgrow(editors, Priority.ALWAYS);
        return new Tab("JSON", new VBox(new ToolBar(validate, format, minify, clear, status), editors));
    }

    private void run(Operation operation) {
        valid = false;
        try {
            /** Tydzień 1, Wzorzec 7/9: Facade, zastosowanie 2.
            * Przyciski operacji JSON-a korzystają z jednej metody fasady;
            * widok nie konfiguruje Jacksona ani dekoratora pomiaru czasu.
            */
            output.setText(tools.json(input.getText(), operation));
            /** Koniec, Tydzień 1, Wzorzec 7/9: Facade, zastosowanie 2.*/
            valid = true;
            status.setText(String.format("Poprawny JSON · %.2f ms", tools.jsonMillis()));
        } catch (JsonProcessingException error) {
            var location = error.getLocation();
            output.setText(error.getOriginalMessage() + (location == null ? ""
                    : "\nWiersz: " + location.getLineNr() + ", kolumna: " + location.getColumnNr()));
            status.setText("Niepoprawny JSON");
        } catch (Exception error) {
            output.setText(error.getMessage());
            status.setText("Nie udało się przetworzyć JSON-a.");
        }
    }

    public List<String> reportLines() {
        return valid ? List.of(output.getText()) : List.of();
    }
}
