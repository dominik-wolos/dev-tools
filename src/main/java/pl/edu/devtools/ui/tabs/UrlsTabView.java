package pl.edu.devtools.ui.tabs;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import pl.edu.devtools.core.DevToolsFacade;
import pl.edu.devtools.core.UrlNode;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class UrlsTabView {
    private final DevToolsFacade tools;
    private final TextField template = new TextField("localhost/orders/{orderID}");
    private final TextArea parameters = new TextArea();
    private final TreeView<UrlNode> preview = new TreeView<>();
    private final Label status = new Label("Identyfikatory liczbowe; każdy znak inny niż cyfra jest separatorem.");
    private final Button open = new Button("Otwórz URL-e");
    private UrlNode.Group batch;
    private UrlNode.Group prototype;

    public UrlsTabView(DevToolsFacade tools) {
        this.tools = tools;
    }

    public Tab create() {
        template.setPromptText("http://localhost:8080/orders/{orderID}");
        template.setPrefColumnCount(34);
        parameters.setPromptText("Np. 1, 2; 003 | 4\n5 6\nSeparatorem może być dowolny znak poza cyfrą.");
        template.textProperty().addListener((observable, before, after) -> invalidate());
        parameters.textProperty().addListener((observable, before, after) -> invalidate());
        Button generate = new Button("Podgląd");
        generate.setOnAction(event -> generate());
        open.setOnAction(event -> open());
        Button remove = new Button("Usuń z podglądu");
        remove.setOnAction(event -> removeSelected());
        Button restore = new Button("Przywróć podgląd");
        restore.setOnAction(event -> {
            if (prototype != null) {
                // Tydzień 1, Wzorzec 3/9: Prototype, zastosowanie 2.
                // Odtworzenie niezależnego drzewa z prototypu zapisanego po generowaniu.
                batch = prototype.copy();
                // Koniec, Tydzień 1, Wzorzec 3/9: Prototype, zastosowanie 2.
                showPreview();
            }
        });
        SplitPane editors = new SplitPane(parameters, preview);
        editors.setDividerPositions(0.35);
        VBox.setVgrow(editors, Priority.ALWAYS);
        return new Tab("URLs", new VBox(new ToolBar(new Label("Szablon:"), template, generate, open),
                new ToolBar(remove, restore, status), editors));
    }

    private void invalidate() {
        batch = null;
        prototype = null;
        preview.setRoot(null);
        status.setText("Dane zmienione — wygeneruj podgląd.");
    }

    private boolean generate() {
        try {
            // Tydzień 1, Wzorzec 7/9: Facade, zastosowanie 3.
            // Przycisk Podgląd zleca fasadzie przygotowanie drzewa URL-i;
            // widok nie musi znać buildera, fabryki ani reguł walidacji adresów.
            batch = tools.urls(template.getText(), parameters.getText());
            // Koniec, Tydzień 1, Wzorzec 7/9: Facade, zastosowanie 3.
            // Tydzień 1, Wzorzec 3/9: Prototype, zastosowanie 3.
            // Zapamiętuje głęboką kopię wygenerowanego podglądu. Usuwanie adresów
            // z batch nie zmieni tej kopii używanej przez Przywróć podgląd.
            prototype = batch.copy();
            // Koniec, Tydzień 1, Wzorzec 3/9: Prototype, zastosowanie 3.
            showPreview();
            return true;
        } catch (IllegalArgumentException error) {
            batch = null;
            prototype = null;
            preview.setRoot(null);
            status.setText(error.getMessage());
            return false;
        }
    }

    private void showPreview() {
        preview.setRoot(tree(batch));
        status.setText("Adresów: " + batch.urls().size() + ". Każdy wiersz listy tworzy grupę.");
    }

    private TreeItem<UrlNode> tree(UrlNode node) {
        TreeItem<UrlNode> item = new TreeItem<>(node);
        if (node instanceof UrlNode.Group group) {
            group.children().forEach(child -> item.getChildren().add(tree(child)));
        }
        item.setExpanded(true);
        return item;
    }

    private void removeSelected() {
        TreeItem<UrlNode> selected = preview.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getParent() != null
                && selected.getParent().getValue() instanceof UrlNode.Group parent) {
            parent.remove(selected.getValue());
            showPreview();
        }
    }

    private void open() {
        if (batch == null && !generate()) {
            return;
        }
        if (batch.urls().isEmpty()) {
            status.setText("Podgląd jest pusty. Przywróć adresy lub zmień parametry.");
            return;
        }
        UrlNode snapshot = batch.copy();
        open.setDisable(true);
        status.setText("Otwieranie " + snapshot.urls().size() + " adresów…");
        CompletableFuture.supplyAsync(() -> tools.open(snapshot)).whenComplete((count, error) -> Platform.runLater(() -> {
            open.setDisable(false);
            status.setText(error == null ? "Przekazano do przeglądarki: " + count + " adresów."
                    : error.getCause().getMessage());
        }));
    }

    public List<String> reportLines() {
        return batch == null ? List.of() : batch.urls().stream().map(Object::toString).toList();
    }
}
