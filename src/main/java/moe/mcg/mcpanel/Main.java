package moe.mcg.mcpanel;

import com.google.gson.Gson;
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
import moe.mcg.mcpanel.api.i18n.*;
import moe.mcg.mcpanel.color.ApplicationColor;
import moe.mcg.mcpanel.image.ApplicationImage;
import moe.mcg.mcpanel.ui.LoginWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;


/**
 * 初始化
 */
public class Main extends Application implements ITranslatable {

    public static final String APP_ID = "mcpanel";
    public static final Component APP_NAME = Component.translatable("app.name");
    public static final float WIDTH_SCALE = 0.6f;
    public static final Logger LOGGER = LoggerFactory.getLogger("[MCPanel]");
    private static final float HEIGHT_SCALE = 0.8f;
    private static final Component ALERT = Component.translatable("app.alert");
    private static final Component ALERT_CONTENT = Component.translatable("app.alert.content");
    private static final String LANGUAGE_FILE_PATH = "language.json";
    private final Gson gson = new Gson();
    private Alert alert;
    private Scene scene;
    private Stage stage;


    public static void main(String[] args) {
        launch(args);
    }

    /**
     * 自适应大小
     */
    public static void setScreenSize(Stage stage) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = screenBounds.getWidth() * WIDTH_SCALE;
        double screenHeight = screenBounds.getHeight() * HEIGHT_SCALE;
        stage.setWidth(screenWidth);
        stage.setHeight(screenHeight);
    }

    public static void log(String log) {
        System.out.println("[MCPanel] " + log);
    }

    public static void updateLanguageInFile(Language language) {
        File languageFile = new File(LANGUAGE_FILE_PATH);
        try (FileWriter writer = new FileWriter(languageFile)) {
            String json = new Gson().toJson(new LanguageConfig(language.name()));
            writer.write(json);
            LOGGER.info("Updated language.json with language: {}", language);

        } catch (IOException e) {
            LOGGER.error("Failed to update language.json", e);
        }
    }

    @Override
    public void start(Stage stage) {
        LOGGER.info("Starting MCPanel");

        I18n.loadAll();
        I18n.setLanguage(checkAndCreateLanguageFile());

        Group group = new Group();
        scene = new Scene(group, 1000, 800, true);

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

    /**
     * 检测语言
     */
    private Language checkAndCreateLanguageFile() {
        File languageFile = new File(LANGUAGE_FILE_PATH);
        if (!languageFile.exists()) {
            try (FileWriter writer = new FileWriter(languageFile)) {
                writer.write(gson.toJson(new LanguageConfig("ZH_CN")));
                LOGGER.info("language.json file not found, created with default language (zh_cn)");
                return Language.ZH_CN;
            } catch (IOException e) {
                LOGGER.error("Failed to create language.json", e);
            }
        } else {
            try (FileReader reader = new FileReader(languageFile)) {
                LanguageConfig config = gson.fromJson(reader, LanguageConfig.class);
                if (config != null && !config.language().isEmpty()) {
                    return Language.valueOf(config.language());
                }
            } catch (IOException e) {
                LOGGER.error("Failed to read language.json", e);
            } catch (IllegalArgumentException e) {
                LOGGER.warn("Invalid language code in language.json, defaulting to zh_cn");
            }
        }

        return Language.ZH_CN;
    }

    @Override
    public void translate() {
        alert.setTitle(ALERT.getString());
        alert.setContentText(ALERT_CONTENT.getString());
        stage.setTitle(APP_NAME.getString());
    }
}
