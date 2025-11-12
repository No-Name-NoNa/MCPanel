package moe.mcg.mcpanel.ui;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import moe.mcg.mcpanel.api.i18n.Component;
import moe.mcg.mcpanel.api.i18n.ITranslatable;
import moe.mcg.mcpanel.api.i18n.TranslationManager;
import moe.mcg.mcpanel.api.pack.ServerInfo;
import moe.mcg.mcpanel.css.ApplicationCSS;
import moe.mcg.mcpanel.image.ApplicationImage;

import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static moe.mcg.mcpanel.Main.APP_NAME;
import static moe.mcg.mcpanel.Main.LOGGER;

public class LoginWindow implements ITranslatable {

    private static final Component LABEL_IP = Component.translatable("login.label.ip");
    private static final Component LABEL_PORT = Component.translatable("login.label.port");
    private static final Component LABEL_ACCESS_KEY = Component.translatable("login.label.access_key");
    private static final Component BUTTON_LOGIN = Component.translatable("login.button.login");
    private static final Component PROMPT_IP = Component.translatable("login.prompt.ip");
    private static final Component PROMPT_PORT = Component.translatable("login.prompt.port");
    private static final Component PROMPT_ACCESS_KEY = Component.translatable("login.prompt.access_key");

    private static final File CACHE_FILE = new File("login_cache.json");
    private static final Component CONNECTION_ERROR = Component.translatable("login.alert.connection_error");
    private static final Component VALIDATION_ERROR = Component.translatable("login.alert.validation_error");
    private static final Component CONNECTION_ERROR_MESSAGE = Component.translatable("login,alert.connection_error.message");
    private static final Component CONNECTION_ERROR_HEADER = Component.translatable("login.alert.connection_error.header");
    private static final Component VALIDATION_ERROR_HEADER = Component.translatable("login.alert.validation_error.header");
    private static final Component VALIDATION_ERROR_MESSAGE = Component.translatable("login.alert.validation_error.message");
    private static final Component CONNECTING = Component.translatable("login.alert.connecting");
    private static final Component CONNECTION_CANCEL = Component.translatable("login.alert.connecting.cancel");
    private static final Component CONNECTING_TO_SERVER = Component.translatable("login.alert.connecting_to_server");
    private final Gson gson = new Gson();
    private final Stage stage;
    private ProgressDialog progressDialog;
    private Label titleLabel;
    private Label ipLabel;
    private Label portLabel;
    private Label keyLabel;
    private TextField ipField;
    private TextField portField;
    private PasswordField keyField;
    private Button loginButton;
    private boolean cancelConnection = false;

    public LoginWindow(Stage stage) {
        this.stage = stage;
        LOGGER.info("Initializing LoginWindow");
        TranslationManager.register(this);
        initUI();
    }

    private void initUI() {
        stage.setTitle(APP_NAME.getString());

        StackPane root = new StackPane();
        root.getStyleClass().add("root");

        GridPane card = new GridPane();
        card.setAlignment(Pos.CENTER);
        card.setHgap(10);
        card.setVgap(20);
        card.setPadding(new Insets(25));
        card.getStyleClass().add("login-card");

        ImageView logo = new ImageView(ApplicationImage.INSTANCE.getResource("icon.png"));
        logo.setFitHeight(64);
        logo.setFitWidth(64);

        titleLabel = new Label(APP_NAME.getString());
        titleLabel.setFont(new Font(20));

        VBox headerBox = new VBox(10, logo, titleLabel);
        headerBox.setAlignment(Pos.CENTER);
        card.add(headerBox, 0, 0, 2, 1);

        ipLabel = new Label(LABEL_IP.getString());
        ipLabel.setFont(new Font(14));
        ipField = new TextField();
        ipField.setPromptText(PROMPT_IP.getString());
        ipField.getStyleClass().add("text-field");
        card.add(ipLabel, 0, 1);
        card.add(ipField, 1, 1);

        portLabel = new Label(LABEL_PORT.getString());
        portLabel.setFont(new Font(14));
        portField = new TextField();
        portField.setPromptText(PROMPT_PORT.getString());
        portField.getStyleClass().add("text-field");
        card.add(portLabel, 0, 2);
        card.add(portField, 1, 2);

        keyLabel = new Label(LABEL_ACCESS_KEY.getString());
        keyLabel.setFont(new Font(14));
        keyField = new PasswordField();
        keyField.setPromptText(PROMPT_ACCESS_KEY.getString());
        keyField.getStyleClass().add("password-field");
        card.add(keyLabel, 0, 3);
        card.add(keyField, 1, 3);

        loginButton = new Button(BUTTON_LOGIN.getString());
        loginButton.setDefaultButton(true);
        loginButton.getStyleClass().add("button");
        loginButton.setOnAction(event -> handleConnect(ipField, portField, keyField));

        HBox hbBtn = new HBox();
        hbBtn.setAlignment(Pos.CENTER);
        hbBtn.setPadding(new Insets(15, 0, 0, 0));
        hbBtn.getChildren().add(loginButton);
        card.add(hbBtn, 0, 4, 2, 1);

        root.getChildren().add(card);

        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(ApplicationCSS.INSTANCE.getResource("login.css"));

        progressDialog = new ProgressDialog();

        stage.setScene(scene);
        stage.show();

        loadCache(ipField, portField, keyField);
    }

    private void handleConnect(TextField ipField, TextField portField, PasswordField keyField) {
        String ip = ipField.getText();
        String portStr = portField.getText();
        String accessKey = keyField.getText();

        if (ip == null || ip.isEmpty()) {
            LOGGER.warn("IP is empty");
            return;
        }
        if (portStr == null || portStr.isEmpty()) {
            LOGGER.warn("Port is empty");
            return;
        }
        if (accessKey == null || accessKey.isEmpty()) {
            LOGGER.warn("Access key is empty");
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            LOGGER.error("Invalid port: {}", portStr);
            return;
        }

        progressDialog.show();

        new Thread(() -> {
            Socket socket = null;
            DataOutputStream out;
            DataInputStream in;
            try {
                socket = new Socket(ip, port);
                out = new DataOutputStream(socket.getOutputStream());
                in = new DataInputStream(socket.getInputStream());

                out.writeUTF(accessKey);
                out.flush(); //发送

                String response = in.readUTF(); //等待响应

                if ("OK".equals(response)) {// key 验证通过
                    String json = in.readUTF();
                    ServerInfo serverInfo = new Gson().fromJson(json, ServerInfo.class);
                    LOGGER.info("Connected to server");

                    saveCache(ip, portStr, accessKey); //保存Ip,Port和Key

                    Socket finalSocket = socket;
                    Platform.runLater(() -> {
                        try {
                            progressDialog.close();
                            new MainPanelWindow(stage, serverInfo, finalSocket, in, out); //打开主面板
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else { //验证不通过
                    LOGGER.warn("failed to connect");
                    progressDialog.close();
                    showValidationErrorPopup();
                    socket.close();
                }

            } catch (Exception e) {
                LOGGER.error("Failed to read response{}", e.getMessage());
                progressDialog.close();
                showConnectionErrorPopup();
                try {
                    if (socket != null) socket.close();
                } catch (Exception ignored) {
                }
            }
        }).start();
    }

    private void saveCache(String ip, String port, String key) {
        Map<String, String> map = new HashMap<>();
        map.put("ip", ip);
        map.put("port", port);
        map.put("key", key);
        try (FileWriter writer = new FileWriter(CACHE_FILE)) {
            gson.toJson(map, writer);
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void loadCache(TextField ipField, TextField portField, PasswordField keyField) {
        if (!CACHE_FILE.exists()) return;
        try (FileReader reader = new FileReader(CACHE_FILE)) {
            Type type = new TypeToken<Map<String, String>>() {
            }.getType();
            Map<String, String> map = gson.fromJson(reader, type);
            if (map != null) {
                ipField.setText(map.getOrDefault("ip", ""));
                portField.setText(map.getOrDefault("port", ""));
                keyField.setText(map.getOrDefault("key", ""));
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void showConnectionErrorPopup() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, CONNECTION_ERROR.getString(), ButtonType.OK);
            alert.setTitle(CONNECTION_ERROR_MESSAGE.getString());
            alert.setHeaderText(CONNECTION_ERROR_HEADER.getString());
            alert.showAndWait();
        });
    }

    private void showValidationErrorPopup() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, VALIDATION_ERROR.getString(), ButtonType.OK);
            alert.setTitle(VALIDATION_ERROR_MESSAGE.getString());
            alert.setHeaderText(VALIDATION_ERROR_HEADER.getString());
            alert.showAndWait();
        });
    }

    @Override
    public void translate() {
        stage.setTitle(APP_NAME.getString());
        titleLabel.setText(APP_NAME.getString());
        ipLabel.setText(LABEL_IP.getString());
        ipField.setPromptText(PROMPT_IP.getString());
        portLabel.setText(LABEL_PORT.getString());
        portField.setPromptText(PROMPT_PORT.getString());
        keyLabel.setText(LABEL_ACCESS_KEY.getString());
        keyField.setPromptText(PROMPT_ACCESS_KEY.getString());
        loginButton.setText(BUTTON_LOGIN.getString());
    }

    /**
     * ProgressDialog 类表示一个进度对话框，用于显示连接过程中的进度信息。
     * 该对话框包含一条消息标签、一个进度条以及一个取消按钮。用户可以通过点击取消按钮来终止连接操作。
     * 对话框的模态性设置为应用级别模态，意味着在关闭此对话框前，用户不能与应用程序的其他部分进行交互。
     */
    private class ProgressDialog extends Stage {

        public ProgressDialog() {
            setTitle(CONNECTING.getString());
            initModality(Modality.APPLICATION_MODAL);
            setResizable(false);

            VBox vbox = new VBox(10);
            vbox.setAlignment(Pos.CENTER);
            vbox.setPadding(new Insets(20));

            Label messageLabel = new Label(CONNECTING_TO_SERVER.getString());
            ProgressBar progressBar = new ProgressBar();
            progressBar.setProgress(-1);

            Button cancelButton = new Button(CONNECTION_CANCEL.getString());
            cancelButton.setOnAction(event -> {
                cancelConnection = true;
                close();
            });

            vbox.getChildren().addAll(messageLabel, progressBar, cancelButton);
            Scene scene = new Scene(vbox, 300, 150);
            setScene(scene);
        }

        public boolean isCancelled() {
            return cancelConnection;
        }
    }
}