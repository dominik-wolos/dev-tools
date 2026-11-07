package pl.edu.devtools.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import pl.edu.devtools.core.DevToolsFacade;
import pl.edu.devtools.core.report.JsonReport;
import pl.edu.devtools.core.report.Report;
import pl.edu.devtools.core.report.ReportOutput;
import pl.edu.devtools.core.report.TextReport;
import pl.edu.devtools.infrastructure.ClipboardReportOutput;
import pl.edu.devtools.infrastructure.FileReportOutput;
import pl.edu.devtools.infrastructure.LoggingFileReportOutput;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

public final class ReportExportView {
    private final DevToolsFacade tools;
    private final Supplier<List<String>> lines;
    private final Window owner;
    private final ChoiceBox<String> format = new ChoiceBox<>(FXCollections.observableArrayList("TXT", "JSON"));
    private final Label status = new Label();
    private final ObjectMapper reportMapper = new ObjectMapper();

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
            /** Lab 1, zadanie 9: Bridge, zastosowanie 5.
            * Przycisk Kopiuj wynik łączy format wybrany w formularzu z implementacją
            * schowka; ta sama implementacja przyjmuje raport TXT lub JSON.
             */
            tools.export(exportable(), report(new ClipboardReportOutput()));
            /** Koniec, Lab 1, zadanie 9: Bridge, zastosowanie 5. */
            status.setText("Skopiowano.");
        } catch (Exception error) {
            status.setText(error.getMessage());
        }
    }

    private void save(ActionEvent event) {
        try {
            List<String> data = exportable();
            File file = fileChooser().showSaveDialog(owner);
            if (file != null) {
                saveReport(data, file);
                status.setText("Zapisano: " + file.getName());
            }
        } catch (Exception error) {
            status.setText(error.getMessage());
        }
    }

    private FileChooser fileChooser() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Zapisz wynik");
        chooser.setInitialFileName("devtools." + (asJson() ? "json" : "txt"));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                asJson() ? "JSON" : "Tekst", asJson() ? "*.json" : "*.txt"));
        return chooser;
    }

    private void saveReport(List<String> data, File file) throws IOException {
        /** Lab 3, zadanie 1: Liskov, zastosowanie 3 — raport otrzymuje typ bazowy. */
        FileReportOutput output = new LoggingFileReportOutput(file.toPath());
        /** Koniec, Lab 3, zadanie 1: Liskov, zastosowanie 3. */
        /** Lab 1, zadanie 9: Bridge, zastosowanie 6.
        * Przycisk Zapisz wynik łączy wybrany format raportu z plikiem
        * wskazanym w oknie zapisu; nie potrzebuje osobnych eksporterów dla TXT i JSON.
         */
        tools.export(data, report(output));
        /** Koniec, Lab 1, zadanie 9: Bridge, zastosowanie 6. */
    }

    private boolean asJson() {
        return "JSON".equals(format.getValue());
    }

    private Report report(ReportOutput destination) {
        /**
         * Lab 1, zadanie 9: Bridge, zastosowanie 4.
         * Łączy wybrany format TXT/JSON z przekazanym miejscem zapisu: plikiem
         * lub schowkiem. Format i miejsce zapisu można wybierać niezależnie.
         */
        return asJson() ? new JsonReport(destination, reportMapper) : new TextReport(destination);
        /** Koniec, Lab 1, zadanie 9: Bridge, zastosowanie 4. */
    }

    private List<String> exportable() {
        List<String> data = lines.get();
        if (data.isEmpty()) {
            throw new IllegalArgumentException("Brak wyniku do eksportu.");
        }
        return data;
    }
}
