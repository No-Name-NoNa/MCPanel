package moe.mcg.mcpanel.ui;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import lombok.Getter;
import moe.mcg.mcpanel.Main;
import moe.mcg.mcpanel.api.i18n.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static moe.mcg.mcpanel.Main.LOGGER;

public class OptionPanel extends VBox implements ITranslatable {

    private static final Component LANGUAGE = Component.translatable("main.option.language");
    private static final Component APPLY = Component.translatable("main.option.apply");
    private static final Component SAVE = Component.translatable("main.option.save");
    private static final Component API_KEY_LABEL = Component.translatable("main.option.apikey");
    private static final Component APP_ID_LABEL = Component.translatable("main.option.appid");

    private static final String FILE_NAME = "apikey.json";
    private static final String[] language = new String[]{
            "English",
            "中文(简体)"
    };
    @Getter
    private static String KEY;
    @Getter
    private static String ID;
    private final ComboBox<String> languageComboBox;
    private final Label languageLabel;
    private final Button applyButton;
    private final TextField apiKeyField;
    private final TextField appIdField;
    private final Button saveButton;
    private final Label apiKeyLabel;
    private final Label appIdLabel;

    public OptionPanel() {
        TranslateManager.register(this);

        setSpacing(10);

        languageLabel = new Label(LANGUAGE.getString());
        languageLabel.getStyleClass().add("language-label");

        languageComboBox = new ComboBox<>();
        languageComboBox.getItems().addAll(language);
        languageComboBox.getStyleClass().add("language-combo-box");
        setDefaultLanguage();

        applyButton = new Button(APPLY.getString());
        applyButton.setOnAction(e -> changeLanguage());
        applyButton.getStyleClass().add("apply-button");

        apiKeyLabel = new Label(API_KEY_LABEL.getString());
        apiKeyField = new TextField();
        apiKeyField.getStyleClass().add("api-key-field");

        appIdLabel = new Label(APP_ID_LABEL.getString());
        appIdField = new TextField();
        appIdField.getStyleClass().add("app-id-field");

        saveButton = new Button(SAVE.getString());
        saveButton.setOnAction(e -> saveSettings());
        saveButton.getStyleClass().add("save-button");

        VBox languageBox = new VBox(10, languageLabel, languageComboBox);
        languageBox.getStyleClass().add("language-box");

        VBox apiSettingsBox = new VBox(10, apiKeyLabel, apiKeyField, appIdLabel, appIdField);
        apiSettingsBox.getStyleClass().add("api-settings-box");

        getChildren().addAll(languageBox, applyButton, apiSettingsBox, saveButton);
        loadSettings();
    }

    private void setDefaultLanguage() {
        String selectedLanguage = "English";
        if (I18n.getCurrentLanguage() == Language.ZH_CN) {
            selectedLanguage = "中文(简体)";
        }
        languageComboBox.getSelectionModel().select(selectedLanguage);
    }

    private void changeLanguage() {
        String selectedLanguage = languageComboBox.getValue();

        Language language = switch (selectedLanguage) {
            case "中文(简体)" -> Language.ZH_CN;
            default -> Language.EN_US;
        };
        if (I18n.getCurrentLanguage() != language) {
            ServerChatPanel.clearCache = true;
        }
        I18n.setLanguage(language);

        Main.updateLanguageInFile(language);
    }

    private void saveSettings() {
        String apiKey = apiKeyField.getText();
        String appId = appIdField.getText();

        if (apiKey.isEmpty() || appId.isEmpty()) {
            LOGGER.info("API Key or App ID is empty");
            return;
        }

        Gson gson = new Gson();
        ApiSettings settings = new ApiSettings(apiKey, appId);
        String json = gson.toJson(settings);

        try (FileWriter file = new FileWriter(FILE_NAME)) {
            file.write(json);
            KEY = apiKey;
            ID = appId;
            LOGGER.info("Settings saved");
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void loadSettings() {
        Gson gson = new Gson();

        try (FileReader reader = new FileReader(FILE_NAME)) {
            ApiSettings settings = gson.fromJson(reader, ApiSettings.class);

            if (settings != null) {
                apiKeyField.setText(settings.apiKey);
                appIdField.setText(settings.appId);
                KEY = settings.apiKey;
                ID = settings.appId;
                LOGGER.info("Settings loaded successfully");
            }
        } catch (IOException | JsonSyntaxException e) {
            LOGGER.warn("Error loading settings: {}", e.getMessage());
        }
    }

    @Override
    public void translate() {
        languageComboBox.setPromptText(LANGUAGE.getString());
        applyButton.setText(APPLY.getString());
        saveButton.setText(SAVE.getString());
        languageLabel.setText(LANGUAGE.getString());
        apiKeyLabel.setText(API_KEY_LABEL.getString());
        appIdLabel.setText(APP_ID_LABEL.getString());
    }

    private record ApiSettings(String apiKey, String appId) {
    }
}