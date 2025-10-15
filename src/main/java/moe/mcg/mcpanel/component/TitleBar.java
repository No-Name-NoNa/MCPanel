package moe.mcg.mcpanel.component;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.stage.Stage;
import moe.mcg.mcpanel.image.ApplicationImage;

@Deprecated
public class TitleBar extends HBox {

    private double dragOffsetX;
    private double dragOffsetY;
    private double restoreX, restoreY, restoreWidth, restoreHeight;

    public TitleBar(Stage stage, String titleText) {
        getStyleClass().add("title-bar");

        ImageView appIcon = new ImageView(ApplicationImage.INSTANCE.getResource("icon.png"));
        appIcon.setFitWidth(20);
        appIcon.setFitHeight(20);

        Label title = new Label(titleText);
        title.getStyleClass().add("title-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnMin = new Button("—");
        btnMin.getStyleClass().addAll("title-btn-base", "title-btn-min");
        btnMin.setOnAction(e -> stage.setIconified(true));

        Button btnMax = new Button("□");
        btnMax.getStyleClass().addAll("title-btn-base", "title-btn-max");
        btnMax.setOnAction(e -> {
            boolean maximized = stage.isMaximized();

            if (!maximized) {
                restoreX = stage.getX();
                restoreY = stage.getY();
                restoreWidth = stage.getWidth();
                restoreHeight = stage.getHeight();

                var bounds = Screen.getPrimary().getVisualBounds();
                stage.setX(bounds.getMinX());
                stage.setY(bounds.getMinY());
                stage.setWidth(bounds.getWidth());
                stage.setHeight(bounds.getHeight());

                btnMax.setText("❐");
            } else {
                stage.setX(restoreX);
                stage.setY(restoreY);
                stage.setWidth(restoreWidth);
                stage.setHeight(restoreHeight);

                btnMax.setText("□");
            }

            stage.setMaximized(!maximized);
        });

        Button btnClose = new Button("╳");
        btnClose.getStyleClass().addAll("title-btn-base", "title-btn-close");
        btnClose.setOnAction(e -> stage.close());

        getChildren().addAll(appIcon, title, spacer, btnMin, btnMax, btnClose);

        setOnMousePressed((MouseEvent e) -> {
            if (!stage.isMaximized()) {
                dragOffsetX = e.getSceneX();
                dragOffsetY = e.getSceneY();
            }
        });

        setOnMouseDragged((MouseEvent e) -> {
            if (!stage.isMaximized()) {
                stage.setX(e.getScreenX() - dragOffsetX);
                stage.setY(e.getScreenY() - dragOffsetY);
            }
        });
    }
}