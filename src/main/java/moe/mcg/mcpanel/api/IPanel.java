package moe.mcg.mcpanel.api;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public interface IPanel<T> {
    void refresh(T data);

    default HBox createInfoRow(String title, String value) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 12, 8, 12));
        row.getStyleClass().add("info-row");

        Label titleLabel = new Label(title + ":");
        titleLabel.getStyleClass().add("info-title");

        Label valueLabel = new Label(value != null ? value : "-");
        valueLabel.getStyleClass().add("info-value");

        row.getChildren().addAll(titleLabel, valueLabel);
        return row;
    }
}
