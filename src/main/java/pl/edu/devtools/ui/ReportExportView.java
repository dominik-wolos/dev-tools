package pl.edu.devtools.ui;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import pl.edu.devtools.core.DevToolsFacade;
import pl.edu.devtools.infrastructure.ClipboardReportOutput;
import pl.edu.devtools.infrastructure.FileReportOutput;

import java.util.List;
import java.util.function.Supplier;

public final class ReportExportView {
    private final DevToolsFacade tools;
    private final Supplier<List<String>> lines;
    private final Window owner;
    private final ChoiceBox<String> format = new ChoiceBox<>(FXCollections.observableArrayList("TXT", "JSON"));
    private final Label status = new Label();

    public ReportExportView(DevToolsFacade tools, Supplier<List<String>> lines, Window owner) {
        this.tools = tools;
        this.lines = lines;
        this.owner = owner;
    }

    public ToolBar create() {
        format.setValue("TXT");
        Button copy = new Button("Kopiuj wynik");
        Button save = new Button("Zapisz wynik");
        copy.setOnAction(this::copy);
        save.setOnAction(this::save);
        return new ToolBar(new Label("Eksport:"), format, copy, save, status);
    }

    private void copy(ActionEvent event) {
        try {
            /** Tydzień 1, Wzorzec 9/9: Bridge, zastosowanie 5.
            * Przycisk Kopiuj wynik łączy format wybrany w formularzu z implementacją
            * schowka; ta sama implementacja przyjmuje raport TXT lub JSON.
             */
            tools.export(exportable(), asJson(), new ClipboardReportOutput());
            /** Koniec, Tydzień 1, Wzorzec 9/9: Bridge, zastosowanie 5.*/
            status.setText("Skopiowano.");
        } catch (Exception error) {
            status.setText(error.getMessage());
        }
    }

    private void save(ActionEvent event) {
        try {
            List<String> data = exportable();
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Zapisz wynik");
            chooser.setInitialFileName("devtools." + (asJson() ? "json" : "txt"));
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                    asJson() ? "JSON" : "Tekst", asJson() ? "*.json" : "*.txt"));
            var file = chooser.showSaveDialog(owner);
            if (file != null) {
                /** Tydzień 1, Wzorzec 9/9: Bridge, zastosowanie 6.
                * Przycisk Zapisz wynik łączy wybrany format raportu z plikiem
                * wskazanym w oknie zapisu; nie potrzebuje osobnych eksporterów dla TXT i JSON.
                 */
                tools.export(data, asJson(), new FileReportOutput(file.toPath()));
                /** Koniec, Tydzień 1, Wzorzec 9/9: Bridge, zastosowanie 6.*/
                status.setText("Zapisano: " + file.getName());
            }
        } catch (Exception error) {
            status.setText(error.getMessage());
        }
    }

    private boolean asJson() {
        return "JSON".equals(format.getValue());
    }

    private List<String> exportable() {
        List<String> data = lines.get();
        if (data.isEmpty()) {
            throw new IllegalArgumentException("Brak wyniku do eksportu.");
        }
        return data;
    }
}
