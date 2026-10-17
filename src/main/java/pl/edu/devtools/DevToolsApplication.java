package pl.edu.devtools;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import pl.edu.devtools.core.DevToolsFacade;
import pl.edu.devtools.core.JsonTool;
import pl.edu.devtools.core.Processes;
import pl.edu.devtools.infrastructure.DesktopBrowser;
import pl.edu.devtools.ui.ReportExportView;
import pl.edu.devtools.ui.tabs.JsonTabView;
import pl.edu.devtools.ui.tabs.ProcessesTabView;
import pl.edu.devtools.ui.tabs.UrlsTabView;

import java.util.List;
import java.util.function.Supplier;

public class DevToolsApplication extends Application {
    private final DevToolsFacade tools = new DevToolsFacade(
            /** Tydzień 1, Wzorzec 8/9: Proxy, zastosowanie 2.
            * Fasada dostaje Guarded zamiast bezpośredniego dostępu do SystemProcesses;
            * każde żądanie Zatrzymaj trafia więc najpierw do kontroli chronionych PID-ów.
             */
            new Processes.Guarded(new Processes.SystemProcesses(), ProcessHandle.current().pid()),
            /** Koniec, Tydzień 1, Wzorzec 8/9: Proxy, zastosowanie 2.*/
            new JsonTool.Jackson(), new DesktopBrowser());

    @Override
    public void start(Stage stage) {
        var processes = new ProcessesTabView(tools);
        var json = new JsonTabView(tools);
        var urls = new UrlsTabView(tools);
        TabPane tabs = new TabPane(
                withExport(processes.create(), processes::reportLines, stage),
                withExport(json.create(), json::reportLines, stage),
                withExport(urls.create(), urls::reportLines, stage));
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Label title = new Label("DevTools");
        title.getStyleClass().add("app-title");
        Label subtitle = new Label("Procesy · JSON · URL-e");
        subtitle.getStyleClass().add("app-subtitle");
        HBox header = new HBox(20, title, subtitle);
        header.getStyleClass().add("app-header");
        header.setPadding(new Insets(18, 24, 16, 24));

        BorderPane root = new BorderPane(tabs);
        root.setTop(header);
        root.getStyleClass().add("app-root");
        Scene scene = new Scene(root, 1100, 720);
        var stylesheet = getClass().getResource("/pl/edu/devtools/styles/app.css");
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        }
        stage.setTitle("DevTools");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    private Tab withExport(Tab tab, Supplier<List<String>> lines, Stage owner) {
        BorderPane content = new BorderPane(tab.getContent());
        content.setBottom(new ReportExportView(tools, lines, owner).create());
        tab.setContent(content);
        return tab;
    }
}
