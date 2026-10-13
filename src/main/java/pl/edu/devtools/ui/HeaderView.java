package pl.edu.devtools.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public final class HeaderView {
    private final String title;
    private final String subtitle;

    public HeaderView(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }

    public HBox create() {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("app-title");

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.getStyleClass().add("app-subtitle");

        HBox header = new HBox(16, titleLabel, subtitleLabel);
        header.getStyleClass().add("app-header");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(18, 24, 16, 24));
        return header;
    }
}
