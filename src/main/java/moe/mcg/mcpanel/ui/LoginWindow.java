package moe.mcg.mcpanel.ui;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import moe.mcg.mcpanel.api.MinecraftSkin2D;
import moe.mcg.mcpanel.api.i18n.Component;
import moe.mcg.mcpanel.api.i18n.ITranslatable;
import moe.mcg.mcpanel.api.i18n.TranslateManager;
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

    private final Gson gson = new Gson();
    private final Stage stage;
    private Label titleLabel;
    private Label ipLabel;
    private Label portLabel;
    private Label keyLabel;
    private TextField ipField;
    private TextField portField;
    private PasswordField keyField;
    private Button loginButton;

    public LoginWindow(Stage stage) {
        this.stage = stage;
        LOGGER.info("Initializing LoginWindow");
        TranslateManager.register(this);
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

        // IP
        ipLabel = new Label(LABEL_IP.getString());
        ipLabel.setFont(new Font(14));
        ipField = new TextField();
        ipField.setPromptText(PROMPT_IP.getString());
        ipField.getStyleClass().add("text-field");
        card.add(ipLabel, 0, 1);
        card.add(ipField, 1, 1);

        // PORT
        portLabel = new Label(LABEL_PORT.getString());
        portLabel.setFont(new Font(14));
        portField = new TextField();
        portField.setPromptText(PROMPT_PORT.getString());
        portField.getStyleClass().add("text-field");
        card.add(portLabel, 0, 2);
        card.add(portField, 1, 2);

        // ACCESS KEY
        keyLabel = new Label(LABEL_ACCESS_KEY.getString());
        keyLabel.setFont(new Font(14));
        keyField = new PasswordField();
        keyField.setPromptText(PROMPT_ACCESS_KEY.getString());
        keyField.getStyleClass().add("password-field");
        card.add(keyLabel, 0, 3);
        card.add(keyField, 1, 3);

        // LOGIN BUTTON
        loginButton = new Button(BUTTON_LOGIN.getString());
        loginButton.setDefaultButton(true);
        loginButton.getStyleClass().add("button");
        loginButton.setOnAction(event -> handleConnect(ipField, portField, keyField));

        HBox hbBtn = new HBox();
        hbBtn.setAlignment(Pos.CENTER);
        hbBtn.setPadding(new Insets(15, 0, 0, 0));
        hbBtn.getChildren().add(loginButton);
        card.add(hbBtn, 0, 4, 2, 1);

        Group skin3DContainer = new Group();
        skin3DContainer.setTranslateX(400); // 在右侧显示
        skin3DContainer.setTranslateY(200); // 调整垂直位置

        Image skin = ApplicationImage.INSTANCE.getResource("tenshi.png");

        Image skinPixelated = new Image(
                skin.getUrl(),
                skin.getWidth(),
                skin.getHeight(),
                false,
                false
        );

        MinecraftSkin2D skin3D = new MinecraftSkin2D(skinPixelated);
        skin3DContainer.getChildren().add(skin3D);

        root.getChildren().add(skin3DContainer);


        root.getChildren().add(card);

        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(ApplicationCSS.INSTANCE.getResource("login.css"));

        stage.setScene(scene);
        stage.show();


        loadCache(ipField, portField, keyField);
    }

    private void handleConnect(TextField ipField, TextField portField, PasswordField keyField) {
        String ip = ipField.getText();
        String portStr = portField.getText();
        String accessKey = keyField.getText();

        if (ip == null || ip.isEmpty()) {
            System.out.println(Component.translatable("connect.error.empty_ip").getString());
            return;
        }
        if (portStr == null || portStr.isEmpty()) {
            System.out.println(Component.translatable("connect.error.empty_port").getString());
            return;
        }
        if (accessKey == null || accessKey.isEmpty()) {
            System.out.println(Component.translatable("connect.error.empty_access_key").getString());
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            System.out.println(Component.translatable("connect.error.invalid_port").getString());
            return;
        }

        new Thread(() -> {
            Socket socket = null;
            DataOutputStream out;
            DataInputStream in;
            try {
                socket = new Socket(ip, port);
                out = new DataOutputStream(socket.getOutputStream());
                in = new DataInputStream(socket.getInputStream());

                out.writeUTF(accessKey);
                out.flush();

                String response = in.readUTF();
                System.out.println("Server response: " + response);

                if ("OK".equals(response)) {
                    String json = in.readUTF();
                    ServerInfo serverInfo = new Gson().fromJson(json, ServerInfo.class);
                    System.out.println("Received config: " + json);

                    saveCache(ip, portStr, accessKey);

                    Socket finalSocket = socket;
                    Platform.runLater(() -> {
                        try {
                            new MainPanelWindow(stage, serverInfo, finalSocket, in, out);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else {
                    System.out.println("Authentication failed: " + response);
                    socket.close();
                }

            } catch (Exception e) {
                System.out.println("Failed to connect: " + e.getMessage());
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
}