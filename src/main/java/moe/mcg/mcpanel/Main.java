package moe.mcg.mcpanel;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import moe.mcg.mcpanel.api.i18n.Component;
import moe.mcg.mcpanel.api.i18n.I18n;
import moe.mcg.mcpanel.api.i18n.Language;
import moe.mcg.mcpanel.color.ApplicationColor;
import moe.mcg.mcpanel.image.ApplicationImage;
import moe.mcg.mcpanel.ui.LoginWindow;

public class Main extends Application {

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

    public static void log(String log) {
        System.out.println("[MCPanel] " + log);
    }

    @Override
    public void start(Stage stage) {
        I18n.loadAll();
        I18n.setLanguage(Language.ZH_CN);

        Group group = new Group();

        Scene scene = new Scene(group);
        setScreenSize(stage);

        scene.setFill(ApplicationColor.BACKGROUND);

        Image icon = ApplicationImage.INSTANCE.getResource("icon.png");
        stage.getIcons().add(icon);
        stage.setTitle(APP_NAME.getString());
        stage.initStyle(StageStyle.DECORATED);
        stage.setScene(scene);

        new LoginWindow(stage);

        stage.show();
    }
}
