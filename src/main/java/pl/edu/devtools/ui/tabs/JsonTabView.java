package pl.edu.devtools.ui.tabs;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public final class JsonTabView implements WorkspaceTabView {
    @Override
    public Tab create() {
        VBox content = new VBox(createToolbar(), createEditorPane());
        VBox.setVgrow(content.getChildren().get(1), Priority.ALWAYS);

        Tab tab = new Tab("JSON");
        tab.setContent(content);
        return tab;
    }

    private ToolBar createToolbar() {
        Label status = new Label("Ready");
        status.getStyleClass().add("status-label");

        return new ToolBar(
                new Button("Validate"),
                new Button("Format"),
                new Button("Minify"),
                new Button("Clear"),
                status
        );
    }

    private SplitPane createEditorPane() {
        TextArea input = new TextArea();
        input.setPromptText("{ \"name\": \"DevTools\" }");

        TextArea output = new TextArea();
        output.setEditable(false);
        output.setPromptText("Validation result");

        SplitPane editorPane = new SplitPane(input, output);
        editorPane.setDividerPositions(0.5);
        return editorPane;
    }
}
