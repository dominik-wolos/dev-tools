package pl.edu.devtools.ui.tabs;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import pl.edu.devtools.core.DevToolsFacade;
import pl.edu.devtools.core.ProcessRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public final class ProcessesTabView {
    private final DevToolsFacade tools;
    private final TableView<ProcessRow> table = new TableView<>();
    private final ObservableList<ProcessRow> source = FXCollections.observableArrayList();
    private final FilteredList<ProcessRow> rows = new FilteredList<>(source);
    private final Label status = new Label();
    private final Button refresh = new Button("Odśwież");
    private final Button stop = new Button("Zatrzymaj");

    public ProcessesTabView(DevToolsFacade tools) {
        this.tools = tools;
    }

    public Tab create() {
        TextField filter = new TextField();
        filter.setPromptText("Filtruj po nazwie, PID lub użytkowniku");
        filter.setPrefColumnCount(28);
        filter.textProperty().addListener((observable, before, after) -> {
            String query = after.trim().toLowerCase(Locale.ROOT);
            rows.setPredicate(row -> row.name().toLowerCase(Locale.ROOT).contains(query)
                    || Long.toString(row.pid()).contains(query)
                    || row.user().toLowerCase(Locale.ROOT).contains(query));
        });
        var sorted = new SortedList<>(rows);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sorted);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPlaceholder(new Label("Brak procesów pasujących do filtra."));
        table.getColumns().add(column("Nazwa", ProcessRow::name));
        table.getColumns().add(column("PID", ProcessRow::pid));
        table.getColumns().add(column("Użytkownik", ProcessRow::user));
        table.getColumns().add(column("CPU łącznie [s]", ProcessRow::cpuSeconds));

        TextArea details = new TextArea();
        details.setEditable(false);
        details.setPrefRowCount(3);
        details.setPromptText("Wybierz proces, aby zobaczyć ścieżkę programu i czas uruchomienia.");
        table.getSelectionModel().selectedItemProperty().addListener((observable, before, row) -> {
            details.setText(row == null ? "" : "PID: " + row.pid() + "\nProgram: "
                    + (row.command().isBlank() ? "niedostępny" : row.command()) + "\nUruchomiono (UTC): " + row.started());
            stop.setDisable(row == null || row.pid() <= 1 || row.pid() == ProcessHandle.current().pid());
        });
        stop.setDisable(true);
        stop.setOnAction(event -> stopSelected());
        refresh.setOnAction(event -> refresh());
        VBox.setVgrow(table, Priority.ALWAYS);
        VBox content = new VBox(new ToolBar(refresh, stop, filter, status), table, details);
        refresh();
        return new Tab("Processes", content);
    }

    private <T> TableColumn<ProcessRow, T> column(String title, Function<ProcessRow, T> value) {
        TableColumn<ProcessRow, T> column = new TableColumn<>(title);
        column.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(value.apply(cell.getValue())));
        return column;
    }

    private void refresh() {
        refresh.setDisable(true);
        status.setText("Wczytywanie…");
        // Tydzień 1, Wzorzec 7/9: Facade, zastosowanie 4.
        // Przycisk Odśwież pobiera procesy przez fasadę w tle, a potem aktualizuje
        // tabelę. Widok nie odwołuje się bezpośrednio do systemowego ProcessHandle.
        CompletableFuture.supplyAsync(tools::processes).whenComplete((result, error) -> Platform.runLater(() -> {
            refresh.setDisable(false);
            if (error == null) {
                source.setAll(result);
                status.setText("Procesów: " + result.size());
            } else {
                status.setText("Nie udało się wczytać procesów: " + error.getCause().getMessage());
            }
        }));
        // Koniec, Tydzień 1, Wzorzec 7/9: Facade, zastosowanie 4.
    }

    private void stopSelected() {
        ProcessRow row = table.getSelectionModel().getSelectedItem();
        if (row == null) {
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Zatrzymać " + row.name() + " (PID " + row.pid() + ")? Niezapisane dane mogą zostać utracone.",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.initOwner(table.getScene().getWindow());
        confirm.setHeaderText("Zatrzymanie procesu");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                boolean requested = tools.stop(row.pid());
                refresh();
                status.setText(requested ? "Wysłano prośbę o zatrzymanie. Odśwież listę za chwilę."
                        : "Proces już zakończony lub system odmówił zatrzymania.");
            } catch (RuntimeException error) {
                status.setText(error.getMessage());
            }
        }
    }

    public List<String> reportLines() {
        if (table.getItems().isEmpty()) {
            return List.of();
        }
        List<String> lines = new ArrayList<>();
        lines.add("PID\tNazwa\tUżytkownik\tCPU łącznie [s]\tProgram\tUruchomiono (UTC)");
        table.getItems().forEach(row -> lines.add(row.pid() + "\t" + row.name() + "\t" + row.user()
                + "\t" + (row.cpuSeconds() == null ? "—" : row.cpuSeconds()) + "\t" + row.command() + "\t" + row.started()));
        return lines;
    }
}
