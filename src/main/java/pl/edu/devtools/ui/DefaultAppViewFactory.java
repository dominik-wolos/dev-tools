package pl.edu.devtools.ui;

import java.util.List;

import javafx.scene.layout.BorderPane;
import pl.edu.devtools.ui.tabs.JsonTabView;
import pl.edu.devtools.ui.tabs.ProcessesTabView;
import pl.edu.devtools.ui.tabs.ProfilesTabView;
import pl.edu.devtools.ui.tabs.UrlsTabView;
import pl.edu.devtools.ui.tabs.WorkspaceTabView;

public final class DefaultAppViewFactory implements AppViewFactory {
    @Override
    public BorderPane createRoot() {
        HeaderView headerView = new HeaderView("DevTools", "Developer workstation manager");
        WorkspaceView workspaceView = new WorkspaceView(createTabs());
        return new AppLayout(headerView, workspaceView).createRoot();
    }

    private List<WorkspaceTabView> createTabs() {
        return List.of(
                new ProcessesTabView(),
                new JsonTabView(),
                new UrlsTabView(),
                new ProfilesTabView()
        );
    }
}
