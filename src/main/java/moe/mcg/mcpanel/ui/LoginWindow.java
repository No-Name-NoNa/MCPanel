package moe.mcg.mcpanel.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import moe.mcg.mcpanel.api.i18n.Component;
import moe.mcg.mcpanel.css.ApplicationCSS;
import moe.mcg.mcpanel.image.ApplicationImage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import static moe.mcg.mcpanel.Main.APP_NAME;

public class LoginWindow {

    private static final Component LABEL_IP_PORT = Component.translatable("login.label.ip_port");
    private static final Component LABEL_ACCESS_KEY = Component.translatable("login.label.access_key");
    private static final Component BUTTON_LOGIN = Component.translatable("login.button.login");
    private static final Component PROMPT_IP_PORT = Component.translatable("login.prompt.ip_port");
    private static final Component PROMPT_ACCESS_KEY = Component.translatable("login.prompt.access_key");
    private Stage stage;

    public LoginWindow(Stage stage) {
        this.stage = stage;
        initUI();
    }

    private void initUI() {
        stage.setTitle(APP_NAME.getString());

        StackPane root = new StackPane();
        root.getStyleClass().add("root");

        GridPane card = new GridPane();
        card.setAlignment(Pos.CENTER);
        card.setHgap(10);
        card.setVgap(10);
        card.setPadding(new Insets(25));
        card.getStyleClass().add("login-card");

        ImageView logo = new ImageView(ApplicationImage.INSTANCE.getResource("icon.png"));
        logo.setFitHeight(64);
        logo.setFitWidth(64);

        Label title = new Label(APP_NAME.getString());
        title.setFont(new Font(20));

        VBox headerBox = new VBox(10, logo, title);
        headerBox.setAlignment(Pos.CENTER);
        card.add(headerBox, 0, 0, 2, 1);

        Label ipLabel = new Label(LABEL_IP_PORT.getString());
        ipLabel.setFont(new Font(14));
        TextField ipField = new TextField();
        ipField.setPromptText(PROMPT_IP_PORT.getString());
        ipField.getStyleClass().add("text-field");
        card.add(ipLabel, 0, 1);
        card.add(ipField, 1, 1);

        Label keyLabel = new Label(LABEL_ACCESS_KEY.getString());
        keyLabel.setFont(new Font(14));
        PasswordField keyField = new PasswordField();
        keyField.setPromptText(PROMPT_ACCESS_KEY.getString());
        keyField.getStyleClass().add("password-field");
        card.add(keyLabel, 0, 2);
        card.add(keyField, 1, 2);

        Button loginButton = new Button(BUTTON_LOGIN.getString());
        loginButton.setDefaultButton(true);
        loginButton.getStyleClass().add("button");
        loginButton.setOnAction(event -> handleConnect(ipField.getText(), keyField.getText()));

        HBox hbBtn = new HBox();
        hbBtn.setAlignment(Pos.CENTER);
        hbBtn.setPadding(new Insets(15, 0, 0, 0));
        hbBtn.getChildren().add(loginButton);
        card.add(hbBtn, 0, 3, 2, 1);

        root.getChildren().add(card);

        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(ApplicationCSS.INSTANCE.getResource("login.css"));

        stage.setScene(scene);
        stage.show();
    }


    private void handleConnect(String ipPort, String accessKey) {
        if (ipPort == null || ipPort.isEmpty()) {
            System.out.println(Component.translatable("connect.error.empty_ip_port").getString());
            return;
        }
        if (accessKey == null || accessKey.isEmpty()) {
            System.out.println(Component.translatable("connect.error.empty_access_key").getString());
            return;
        }

        String ip;
        int port;
        try {
            String[] parts = ipPort.split(":");
            ip = parts[0];
            port = Integer.parseInt(parts[1]);
        } catch (Exception e) {
            System.out.println(Component.translatable("connect.error.invalid_ip_port").getString());
            return;
        }

        new Thread(() -> {
            try (Socket socket = new Socket(ip, port);
                 DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                 DataInputStream in = new DataInputStream(socket.getInputStream())) {

                out.writeUTF(accessKey);
                out.flush();

                String response = in.readUTF();
                System.out.println("Server response: " + response);

                // TODO: 根据 response 判断验证是否成功
                if ("OK".equals(response)) {
                    System.out.println("Connection and authentication successful!");
                } else {
                    System.out.println("Authentication failed: " + response);
                }

            } catch (Exception e) {
                System.out.println("Failed to connect: " + e.getMessage());
            }
        }).start();
    }
}