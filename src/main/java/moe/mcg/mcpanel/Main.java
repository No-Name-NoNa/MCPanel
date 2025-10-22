package moe.mcg.mcpanel;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import moe.mcg.mcpanel.api.i18n.Component;
import moe.mcg.mcpanel.api.i18n.I18n;
import moe.mcg.mcpanel.api.i18n.ITranslatable;
import moe.mcg.mcpanel.api.i18n.Language;
import moe.mcg.mcpanel.color.ApplicationColor;
import moe.mcg.mcpanel.image.ApplicationImage;
import moe.mcg.mcpanel.ui.LoginWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class Main extends Application implements ITranslatable {

    public static final String APP_ID = "mcpanel";
    public static final Component APP_NAME = Component.translatable("app.name");
    public static final float SCALE = 0.8f;
    public static final Logger LOGGER = LoggerFactory.getLogger("[MCPanel]");
    private static final Component ALERT = Component.translatable("app.alert");
    private static final Component ALERT_CONTENT = Component.translatable("app.alert.content");
    private Alert alert;
    private Scene scene;
    private Stage stage;


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
        LOGGER.info("Starting MCPanel");

        I18n.loadAll();

        LOGGER.info("Default Language ZH_CN");

        I18n.setLanguage(Language.ZH_CN);

        Group group = new Group();
        scene = new Scene(group);

        setScreenSize(stage);
        scene.setFill(ApplicationColor.BACKGROUND);
        Image icon = ApplicationImage.INSTANCE.getResource("icon.png");

        this.stage = stage;
        stage.getIcons().add(icon);
        stage.setTitle(APP_NAME.getString());
        stage.initStyle(StageStyle.DECORATED);
        stage.setScene(scene);

        new LoginWindow(stage);
        stage.setOnCloseRequest(event -> {
            event.consume();
            confirmExit();
        });

        stage.show();
    }

    private void confirmExit() {
        alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(ALERT.getString());
        alert.setHeaderText(null);
        alert.setContentText(ALERT_CONTENT.getString());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            LOGGER.info("Exiting MCPanel");
            Platform.exit();
            System.exit(0);
        }
    }


    @Override
    public void translate() {
        this.alert.setTitle(ALERT.getString());
        this.alert.setContentText(ALERT_CONTENT.getString());
    }
}
