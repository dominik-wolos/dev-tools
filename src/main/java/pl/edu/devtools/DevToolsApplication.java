package pl.edu.devtools;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import pl.edu.devtools.ui.AppViewFactory;
import pl.edu.devtools.ui.DefaultAppViewFactory;

public class DevToolsApplication extends Application {
    private static final String APP_TITLE = "DevTools";
    private final AppViewFactory appViewFactory;

    public DevToolsApplication() {
        this(new DefaultAppViewFactory());
    }

    DevToolsApplication(AppViewFactory appViewFactory) {
        this.appViewFactory = appViewFactory;
    }

    @Override
    public void start(Stage stage) {
        BorderPane root = appViewFactory.createRoot();

        Scene scene = new Scene(root, 1100, 720);
        var stylesheet = getClass().getResource("/pl/edu/devtools/styles/app.css");
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        }

        stage.setTitle(APP_TITLE);
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }
}
