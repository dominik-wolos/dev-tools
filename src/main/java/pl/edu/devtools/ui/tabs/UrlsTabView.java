package pl.edu.devtools.ui.tabs;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import pl.edu.devtools.core.DevToolsFacade;
import pl.edu.devtools.core.UrlNode;
import pl.edu.devtools.core.urls.UrlGroup;
import pl.edu.devtools.core.urls.UrlSession;
import pl.edu.devtools.core.urls.commands.Command;
import pl.edu.devtools.core.urls.commands.RemoveUrlCommand;
import pl.edu.devtools.core.urls.commands.RestorePreviewCommand;
import pl.edu.devtools.core.urls.commands.UndoPreviewCommand;
import pl.edu.devtools.ui.urls.observers.UrlActionsObserver;
import pl.edu.devtools.ui.urls.observers.UrlPreviewObserver;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.StreamSupport;

public final class UrlsTabView {
    private final DevToolsFacade tools;
    private final TextField template = new TextField("localhost/orders/{orderID}");
    private final TextArea parameters = new TextArea();
    private final TreeView<UrlNode> preview = new TreeView<>();
    private final Label status = new Label("Identyfikatory liczbowe; każdy znak inny niż cyfra jest separatorem.");
    private final Button open = new Button("Otwórz URL-e");
    private final Button undo = new Button("Cofnij");
    private final Button generate = new Button("Podgląd");
    private final Button remove = new Button("Usuń z podglądu");
    private final Button restore = new Button("Przywróć podgląd");
    private final UrlSession session = new UrlSession();

    public UrlsTabView(DevToolsFacade tools) {
        this.tools = tools;
        /** Tydzień 2, Wzorzec 6: Observer, zastosowanie 6 — wywołanie rejestracji dwóch odbiorców. */
        session.addObserver(new UrlPreviewObserver(preview, status));
        var actions = new UrlActionsObserver(List.of(template, parameters, generate, open, preview),
                remove, restore, undo);
        session.addObserver(actions);
        actions.sessionChanged(session);
    }

    public Tab create() {
        template.setPromptText("http://localhost:8080/orders/{orderID}");
        template.setPrefColumnCount(34);
        parameters.setPromptText("Np. 1, 2; 003 | 4\n5 6\nSeparatorem może być dowolny znak poza cyfrą.");
        template.textProperty().addListener((observable, before, after) -> invalidate());
        parameters.textProperty().addListener((observable, before, after) -> invalidate());
        generate.setOnAction(event -> generate());
        open.setOnAction(event -> open());
        remove.setOnAction(event -> removeSelected());
        restore.setOnAction(event -> execute(new RestorePreviewCommand(session)));
        /**
         * Tydzień 2, Wzorzec 5: Memento, zastosowanie 5.
         * Przycisk inicjuje wywołanie cofnięcia ostatniej zmiany kopii roboczej.
         */
        undo.setOnAction(event -> execute(new UndoPreviewCommand(session)));
        /** Koniec, Tydzień 2, Wzorzec 5: Memento, zastosowanie 5. */
        SplitPane editors = new SplitPane(parameters, preview);
        editors.setDividerPositions(0.35);
        VBox.setVgrow(editors, Priority.ALWAYS);
        return new Tab("URLs", new VBox(new ToolBar(new Label("Szablon:"), template, generate, open),
                new ToolBar(remove, restore, undo, status), editors));
    }

    private void invalidate() {
        session.clear();
        status.setText("Dane zmienione — wygeneruj podgląd.");
    }

    private boolean generate() {
        try {
            /**
             * Tydzień 1, Wzorzec 7/9: Facade, zastosowanie 3.
             * Przycisk Podgląd zleca fasadzie przygotowanie drzewa URL-i;
             * widok nie musi znać buildera, fabryki ani reguł walidacji adresów.
             */
            session.load(tools.urls(template.getText(), parameters.getText()));
            /** Koniec, Tydzień 1, Wzorzec 7/9: Facade, zastosowanie 3. */
            return true;
        } catch (IllegalArgumentException error) {
            session.clear();
            status.setText(error.getMessage());
            return false;
        }
    }

    private void removeSelected() {
        TreeItem<UrlNode> selected = preview.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getParent() != null
                && selected.getParent().getValue() instanceof UrlGroup parent) {
            execute(new RemoveUrlCommand(session, parent, selected.getValue()));
        }
    }

    /**
     * Tydzień 2, Wzorzec 1: Command, zastosowanie 5.
     * Widok inicjuje wywołanie polecenia przez wspólny interfejs; obserwatorzy odświeżają podgląd.
     */
    private void execute(Command command) {
        command.execute();
    }
    /** Koniec, Tydzień 2, Wzorzec 1: Command, zastosowanie 5. */

    private void open() {
        if (session.batch() == null && !generate()) {
            return;
        }
        UrlNode snapshot;
        try {
            /** Tydzień 2, Wzorzec 7: State, zastosowanie 3 — wywołanie rozpoczęcia otwierania. */
            snapshot = session.beginOpening();
        } catch (IllegalStateException error) {
            status.setText(error.getMessage());
            return;
        }
        status.setText("Otwieranie " + snapshot.urls().size() + " adresów…");
        CompletableFuture.supplyAsync(() -> tools.open(snapshot)).whenComplete((count, error) -> Platform.runLater(() -> {
            session.finishOpening();
            status.setText(error == null ? "Przekazano do przeglądarki: " + count + " adresów."
                    : error.getCause().getMessage());
        }));
    }

    public List<String> reportLines() {
        UrlGroup batch = session.batch();
        /**
         * Tydzień 2, Wzorzec 3: Iterator, zastosowanie 3.
         * Wyeksportowanie podglądu wykorzystuje wywołanie iteratora przez spliterator().
         */
        return batch == null ? List.of()
                : StreamSupport.stream(batch.spliterator(), false).map(Object::toString).toList();
        /** Koniec, Tydzień 2, Wzorzec 3: Iterator, zastosowanie 3. */
    }
}
