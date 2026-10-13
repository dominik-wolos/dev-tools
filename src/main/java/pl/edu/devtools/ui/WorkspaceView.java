package pl.edu.devtools.ui;

import java.util.List;

import javafx.scene.control.TabPane;
import pl.edu.devtools.ui.tabs.WorkspaceTabView;

public final class WorkspaceView {
    private final List<WorkspaceTabView> tabs;

    public WorkspaceView(List<WorkspaceTabView> tabs) {
        this.tabs = List.copyOf(tabs);
    }

    public TabPane create() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.stream()
                .map(WorkspaceTabView::create)
                .forEach(tabPane.getTabs()::add);
        return tabPane;
    }
}
