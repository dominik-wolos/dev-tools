package pl.edu.devtools.ui.tabs;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public final class UrlsTabView implements WorkspaceTabView {
    @Override
    public Tab create() {
        VBox content = new VBox(createToolbar(), createUrlPreviewPane());
        VBox.setVgrow(content.getChildren().get(1), Priority.ALWAYS);

        Tab tab = new Tab("URLs");
        tab.setContent(content);
        return tab;
    }

    private ToolBar createToolbar() {
        TextField templateField = new TextField();
        templateField.setPromptText("https://example.com/items/{value}");
        HBox.setHgrow(templateField, Priority.ALWAYS);

        return new ToolBar(
                new Label("Template"),
                templateField,
                new Button("Preview"),
                new Button("Open")
        );
    }

    private SplitPane createUrlPreviewPane() {
        TextArea valuesArea = new TextArea();
        valuesArea.setPromptText("one value per line");

        ListView<String> previewList = new ListView<>();
        previewList.setPlaceholder(new Label("No URL preview"));

        SplitPane splitPane = new SplitPane(valuesArea, previewList);
        splitPane.setDividerPositions(0.45);
        return splitPane;
    }
}
