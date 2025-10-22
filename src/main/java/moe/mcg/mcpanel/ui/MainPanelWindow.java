package moe.mcg.mcpanel.ui;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import moe.mcg.mcpanel.api.config.PanelConfig;
import moe.mcg.mcpanel.api.i18n.Component;
import moe.mcg.mcpanel.api.i18n.ITranslatable;
import moe.mcg.mcpanel.api.i18n.TranslateManager;
import moe.mcg.mcpanel.api.minecraft.ServerPlayer;
import moe.mcg.mcpanel.css.ApplicationCSS;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.List;

import static moe.mcg.mcpanel.Main.LOGGER;

public class MainPanelWindow implements ITranslatable {
    private static final Component CONNECTED = Component.translatable("main.connected");
    private static final Component SERVER_NAME = Component.translatable("main.servername");
    private static final Component MOTD = Component.translatable("main.motd");
    private static final Component VERSION = Component.translatable("main.version");
    private static final Component PLAYERS = Component.translatable("main.players");

    private final Stage stage;
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final Gson gson = new Gson();

    private final VBox playerBox = new VBox(5);

    private Label serverNameLabel;
    private Label motdLabel;
    private Label versionLabel;
    private Label playerTitleLabel;
    private PanelConfig panelConfig;

    public MainPanelWindow(Stage stage, PanelConfig config, Socket socket, DataInputStream in, DataOutputStream out) {
        this.stage = stage;
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.panelConfig = config;
        LOGGER.info("Initializing MainPanelWindow");
        TranslateManager.register(this);
        initUI(config);
    }

    private void initUI(PanelConfig config) {
        startListenThread();
        VBox root = new VBox(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));
        root.getStyleClass().add("root");

        VBox card = new VBox(10);
        card.setAlignment(Pos.TOP_LEFT);
        card.setPadding(new Insets(20));
        card.getStyleClass().add("card");

        serverNameLabel = new Label(SERVER_NAME.getString() + config.serverName);
        motdLabel = new Label(MOTD.getString() + config.serverIntro);
        versionLabel = new Label(VERSION.getString() + config.serverVersion);

        playerTitleLabel = new Label(PLAYERS.getString());
        playerTitleLabel.getStyleClass().add("player-title");

        playerBox.setAlignment(Pos.TOP_LEFT);
        playerBox.setSpacing(5);

        card.getChildren().addAll(serverNameLabel, motdLabel, versionLabel, playerTitleLabel, playerBox);
        root.getChildren().add(card);

        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(ApplicationCSS.INSTANCE.getResource("mainpanel.css"));

        stage.setScene(scene);
        stage.show();
    }

    /**
     * 在独立线程中持续监听服务器发来的 DiffPacket JSON
     */
    private void startListenThread() {
        Thread listener = new Thread(() -> {
            try {
                while (!socket.isClosed()) {
                    System.out.println("received");
                    String json = in.readUTF(); // 阻塞等待服务器消息
                    handleServerMessage(json);
                }
                System.out.println("closed");
            } catch (Exception e) {
                System.out.println("Disconnected: " + e.getMessage());
                try {
                    socket.close();
                } catch (Exception ignored) {
                }
            }
        }, "ConfigWindow-Listener");
        listener.setDaemon(true);
        listener.start();
    }

    /**
     * 处理从服务器发来的消息
     */
    private void handleServerMessage(String json) {
        System.out.println("[MCPanel] Received from server: " + json);

        try {
            DiffPacket packet = gson.fromJson(json, DiffPacket.class);
            Platform.runLater(() -> updatePlayerList(packet));
        } catch (Exception e) {
            System.out.println("Invalid JSON: " + json);
        }
    }

    /**
     * 根据 DiffPacket 更新 UI
     */
    private void updatePlayerList(DiffPacket packet) {
/*
        playerBox.getChildren().clear();

        if (packet.added != null && !packet.added.isEmpty()) {
            playerBox.getChildren().add(new Label("Added Players:"));
            for (ServerPlayer p : packet.added) {
                playerBox.getChildren().add(new Label(" + " + p.name + " (" + p.dimension + ")"));
            }
        }

        if (packet.updated != null && !packet.updated.isEmpty()) {
            playerBox.getChildren().add(new Label("Updated Players:"));
            for (ServerPlayer p : packet.updated) {
                playerBox.getChildren().add(new Label(" * " + p.name + " -> " + p.location.toString()));
            }
        }

        if (packet.removed != null && !packet.removed.isEmpty()) {
            playerBox.getChildren().add(new Label("Removed Players:"));
            for (ServerPlayer p : packet.removed) {
                playerBox.getChildren().add(new Label(" - " + p.name));
            }
        }*/

        playerBox.getChildren().clear();
        if (packet.updated != null) {
            for (ServerPlayer p : packet.updated) {
                Label playerLabel = new Label(p.name + " | " + p.dimension + " | Ping: " + p.ping);
                playerLabel.getStyleClass().add("player-label");
                playerBox.getChildren().add(playerLabel);
            }
        }
    }

    @Override
    public void translate() {
        serverNameLabel.setText(SERVER_NAME.getString() + panelConfig.serverName);
        motdLabel.setText(MOTD.getString() + panelConfig.serverIntro);
        versionLabel.setText(VERSION.getString() + panelConfig.serverVersion);
        playerTitleLabel.setText(PLAYERS.getString());
    }

    /**
     * DiffPacket 对应服务器端的 JSON 格式
     */
    private static class DiffPacket {
        List<ServerPlayer> added;
        List<ServerPlayer> removed;
        List<ServerPlayer> updated;
    }
}
