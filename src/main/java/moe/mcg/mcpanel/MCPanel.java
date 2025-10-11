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
import moe.mcg.mcpanel.image.ApplicationImage;

import java.util.Objects;

public class MCPanel extends Application {

    public static final String APP_ID = "mcpanel";
    public static final String APP_NAME = "Minecraft Server Panel";

    @Override
    public void start(Stage stage) throws Exception {
        Group group = new Group();

        Scene scene = new Scene(group);
        setScreenSize(stage);

        scene.setFill(ApplicationColor.BACKGROUND);

        Image icon = ApplicationImage.findImage("icon.png");
        stage.getIcons().add(icon);
        stage.setTitle(APP_NAME);
        stage.initStyle(StageStyle.DECORATED);
        stage.setScene(scene);

        stage.show();
    }

    public static void main(String[] args){
        launch(args);
    }

    public static void setScreenSize(Stage stage){
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = screenBounds.getWidth() * 0.85;
        double screenHeight = screenBounds.getHeight() * 0.85;
        stage.setWidth(screenWidth);
        stage.setHeight(screenHeight);
    }
}
