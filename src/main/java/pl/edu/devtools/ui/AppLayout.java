package pl.edu.devtools.ui;

import javafx.scene.layout.BorderPane;

public final class AppLayout {
    private final HeaderView headerView;
    private final WorkspaceView workspaceView;

    public AppLayout(HeaderView headerView, WorkspaceView workspaceView) {
        this.headerView = headerView;
        this.workspaceView = workspaceView;
    }

    public BorderPane createRoot() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.setTop(headerView.create());
        root.setCenter(workspaceView.create());
        return root;
    }
}
