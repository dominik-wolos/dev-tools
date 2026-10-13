package pl.edu.devtools.ui.tabs;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import pl.edu.devtools.ui.model.ProcessRow;

public final class ProcessesTabView implements WorkspaceTabView {
    @Override
    public Tab create() {
        VBox content = new VBox(createToolbar(), createProcessTable());
        VBox.setVgrow(content.getChildren().get(1), Priority.ALWAYS);

        Tab tab = new Tab("Processes");
        tab.setContent(content);
        return tab;
    }

    private ToolBar createToolbar() {
        return new ToolBar(
                new Button("Refresh"),
                new Button("Stop")
        );
    }

    private TableView<ProcessRow> createProcessTable() {
        TableView<ProcessRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPlaceholder(new Label("No process data loaded"));
        table.getColumns().add(createColumn("Name"));
        table.getColumns().add(createColumn("PID"));
        table.getColumns().add(createColumn("Status"));
        return table;
    }

    private TableColumn<ProcessRow, String> createColumn(String title) {
        return new TableColumn<>(title);
    }
}
