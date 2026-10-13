package pl.edu.devtools.ui.tabs;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public final class ProfilesTabView implements WorkspaceTabView {
    @Override
    public Tab create() {
        VBox content = new VBox(createToolbar(), createProfilesPane());
        VBox.setVgrow(content.getChildren().get(1), Priority.ALWAYS);

        Tab tab = new Tab("Profiles");
        tab.setContent(content);
        return tab;
    }

    private ToolBar createToolbar() {
        return new ToolBar(
                new Button("Add"),
                new Button("Edit"),
                new Button("Run")
        );
    }

    private SplitPane createProfilesPane() {
        ListView<String> profiles = new ListView<>();
        profiles.getItems().addAll("Java", "Web", "University");

        ListView<String> actions = new ListView<>();
        actions.setPlaceholder(new Label("No profile selected"));

        SplitPane splitPane = new SplitPane(profiles, actions);
        splitPane.setDividerPositions(0.28);
        return splitPane;
    }
}
