package moe.mcg.mcpanel;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import moe.mcg.mcpanel.color.ApplicationColor;
import moe.mcg.mcpanel.i18n.Component;
import moe.mcg.mcpanel.i18n.I18n;
import moe.mcg.mcpanel.i18n.Language;
import moe.mcg.mcpanel.image.ApplicationImage;

public class MCPanel extends Application {

    public static final String APP_ID = "mcpanel";
    public static final Component APP_NAME = Component.translatable("app.name");
    public static final float SCALE = 0.8f;

    public static void main(String[] args) {
        launch(args);
    }

    public static void setScreenSize(Stage stage) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = screenBounds.getWidth() * SCALE;
        double screenHeight = screenBounds.getHeight() * SCALE;
        stage.setWidth(screenWidth);
        stage.setHeight(screenHeight);
    }

    @Override
    public void start(Stage stage) throws Exception {

        I18n.load(Language.EN_US);

        Group group = new Group();

        Scene scene = new Scene(group);
        setScreenSize(stage);

        scene.setFill(ApplicationColor.BACKGROUND);

        Image icon = ApplicationImage.findImage("icon.png");
        stage.getIcons().add(icon);
        stage.setTitle(APP_NAME.getString());
        stage.initStyle(StageStyle.DECORATED);
        stage.setScene(scene);

        stage.show();
    }
}
