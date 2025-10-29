package moe.mcg.mcpanel.ui;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import moe.mcg.mcpanel.api.Status;
import moe.mcg.mcpanel.api.i18n.Component;
import moe.mcg.mcpanel.api.i18n.ITranslatable;
import moe.mcg.mcpanel.api.i18n.TranslateManager;
import moe.mcg.mcpanel.api.pack.ModInfo;
import moe.mcg.mcpanel.api.pack.ServerInfo;
import moe.mcg.mcpanel.api.pack.SimpleServerPlayer;
import moe.mcg.mcpanel.css.ApplicationCSS;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static moe.mcg.mcpanel.Main.LOGGER;

public class MainPanelWindow implements ITranslatable {
    private static final Component MENU_SERVER_INFO = Component.translatable("main.menu.server_info");
    private static final Component MENU_MOD_LIST = Component.translatable("main.menu.mod_list");
    private static final Component MENU_PLAYER_LIST = Component.translatable("main.menu.player_list");
    private static final Component MENU_SERVER_STATUS = Component.translatable("main.menu.server_status");

    private static final Component TITLE_SERVER_NAME = Component.translatable("main.info.server_name");
    private static final Component TITLE_SERVER_INTRO = Component.translatable("main.info.server_intro");
    private static final Component TITLE_VERSION = Component.translatable("main.info.version");
    private static final Component TITLE_PLAYER_COUNT = Component.translatable("main.info.player_count");
    private static final Component TITLE_MOD_LIST = Component.translatable("main.info.mod_list");
    private static final Component TITLE_PLAYER_LIST = Component.translatable("main.info.player_list");
    private static final Component TITLE_SERVER_STATUS = Component.translatable("main.info.server_status");
    private static final Component NO_MODS = Component.translatable("main.info.no_mods");
    private static final Component NOT_IMPLEMENTED = Component.translatable("main.info.not_implemented");

    private static final Component MOD_ID_COLUMN_TITLE = Component.translatable("main.info.mod_id");
    private static final Component MOD_NAME_COLUMN_TITLE = Component.translatable("main.info.mod_name");
    private static final Component MOD_VERSION_COLUMN_TITLE = Component.translatable("main.info.mod_version");
    private static final Component MOD_URL_COLUMN_TITLE = Component.translatable("main.info.mod_url");

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final Stage stage;
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final Gson gson = new Gson();

    private final VBox playerBox = new VBox(5);
    private final VBox serverInfoBox = new VBox(5);
    private SimpleServerPlayer simpleServerPlayer;
    private List<ModInfo> modInfo = new ArrayList<>();
    private Label playerTitleLabel;
    private ServerInfo serverInfo;
    private Button btnServerInfo;
    private Button btnMods;
    private Button btnPlayers;
    private Button btnStatus;
    private VBox rightPanel;
    private Label serverNameLabel;
    private Label motdLabel;
    private Label versionLabel;
    private Label playerCountLabel;
    private Label titleLabel;
    private VBox modListBox;
    private VBox playerListBox;
    private Status status = Status.INFO;

    private TableView<ModInfo> tableView = new TableView<>();

    private TableColumn<ModInfo, String> modIdColumn = new TableColumn<>(MOD_ID_COLUMN_TITLE.getString());
    private TableColumn<ModInfo, String> modNameColumn = new TableColumn<>(MOD_NAME_COLUMN_TITLE.getString());
    private TableColumn<ModInfo, String> modVersionColumn = new TableColumn<>(MOD_VERSION_COLUMN_TITLE.getString());
    private TableColumn<ModInfo, String> modUrlColumn = new TableColumn<>(MOD_URL_COLUMN_TITLE.getString());

    public MainPanelWindow(Stage stage, ServerInfo config, Socket socket, DataInputStream in, DataOutputStream out) throws IOException {
        this.stage = stage;
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.serverInfo = config;
        LOGGER.info("Initializing MainPanelWindow");
        TranslateManager.register(this);
        initUI(config);
    }

    private void initUI(ServerInfo serverInfo) throws IOException {
        startListenThread();
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.getStyleClass().add("root");

        VBox leftMenu = new VBox(15);
        leftMenu.setAlignment(Pos.TOP_LEFT);
        leftMenu.setPadding(new Insets(10, 20, 10, 10));
        leftMenu.getStyleClass().add("sidebar");

        btnServerInfo = new Button(MENU_SERVER_INFO.getString());
        btnMods = new Button(MENU_MOD_LIST.getString());
        btnPlayers = new Button(MENU_PLAYER_LIST.getString());
        btnStatus = new Button(MENU_SERVER_STATUS.getString());

        for (Button b : new Button[]{btnServerInfo, btnMods, btnPlayers, btnStatus}) {
            b.getStyleClass().add("sidebar-button");
        }

        leftMenu.getChildren().addAll(btnServerInfo, btnMods, btnPlayers, btnStatus);


        rightPanel = new VBox(10);
        rightPanel.setAlignment(Pos.TOP_LEFT);
        rightPanel.setPadding(new Insets(20));
        rightPanel.getStyleClass().add("card");

        titleLabel = new Label();
        titleLabel.getStyleClass().add("player-title");

        serverNameLabel = new Label();
        motdLabel = new Label();
        versionLabel = new Label();
        playerCountLabel = new Label();

        modListBox = new VBox(5);
        playerListBox = new VBox(5);

        showServerInfo();

        btnServerInfo.setOnAction(e -> {
            try {
                showServerInfo();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        btnMods.setOnAction(e -> {
            try {
                showModList();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        btnPlayers.setOnAction(e -> {
            try {
                showPlayerList();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        btnStatus.setOnAction(e -> {
            try {
                showServerStatus();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        modIdColumn.setCellValueFactory(new PropertyValueFactory<>("modId"));
        modNameColumn.setCellValueFactory(new PropertyValueFactory<>("modName"));
        modVersionColumn.setCellValueFactory(new PropertyValueFactory<>("modVersion"));
        modUrlColumn.setCellValueFactory(new PropertyValueFactory<>("modUrl"));
        modIdColumn.getStyleClass().add("modId-column-cell");
        modNameColumn.getStyleClass().add("modName-column-cell");
        modVersionColumn.getStyleClass().add("modVersion-column-cell");
        modUrlColumn.getStyleClass().add("modUrl-column-cell");
        tableView.getColumns().addAll(modIdColumn, modNameColumn, modVersionColumn, modUrlColumn);

        root.setCenter(rightPanel);
        root.setLeft(leftMenu);

        Scene scene = new Scene(root, 800, 500);
        scene.getStylesheets().add(ApplicationCSS.INSTANCE.getResource("mainpanel.css"));
        stage.setScene(scene);
        stage.show();
    }


    private void startListenThread() {
        Thread listener = new Thread(() -> {
            try {
                while (!socket.isClosed()) {
                    System.out.println("received");
                    handleServerMessage();
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

    private synchronized void sendMessage(String message) {
        try {
            if (socket != null && !socket.isClosed()) {
                out.writeUTF(message);
                out.flush();
            }
        } catch (Exception e) {
            LOGGER.error("Failed to send message: {}", e.getMessage());
        }
    }

    /**
     * 处理从服务器发来的消息
     */
    private void handleServerMessage() {
        try {

            String messageType = in.readUTF();
            String json = in.readUTF();
            switch (messageType) {
                case "INFO": {
                    serverInfo = gson.fromJson(json, ServerInfo.class);
                    Platform.runLater(this::updateServerInfo);
                    break;
                }
                case "MODS": {
                    Type listType = new TypeToken<List<ModInfo>>() {
                    }.getType();
                    modInfo = gson.fromJson(json, listType);
                    Platform.runLater(this::updateModList);
                    break;
                }
                case "PLAYERS": {
                    simpleServerPlayer = gson.fromJson(json, SimpleServerPlayer.class);
                    Platform.runLater(this::updatePlayerList);
                    break;
                }
                default:
                    System.out.println("Unknown message type: " + messageType);
            }
        } catch (Exception ignored) {
        }
    }


    private void showServerInfo() throws IOException {
        rightPanel.getChildren().setAll(titleLabel, serverInfoBox);
        titleLabel.setText(MENU_SERVER_INFO.getString());
        setStatus(Status.INFO);
    }

    private void showModList() throws IOException {
        rightPanel.getChildren().setAll(titleLabel, modListBox);
        titleLabel.setText(TITLE_MOD_LIST.getString());
        setStatus(Status.MODS);
    }


    private void showPlayerList() throws IOException {
        rightPanel.getChildren().setAll(titleLabel, playerBox);
        titleLabel.setText(TITLE_PLAYER_LIST.getString());
        setStatus(Status.PLAYERS);
    }

    private void showServerStatus() throws IOException {
        rightPanel.getChildren().setAll(titleLabel, new Label(NOT_IMPLEMENTED.getString()));
        titleLabel.setText(TITLE_SERVER_STATUS.getString());
        setStatus(Status.STATUS);
    }

    private void setStatus(Status mods) {
        executor.submit(() -> {
            status = mods;
            sendMessage(status.name());
        });
    }

    private void updateServerInfo() {
        if (serverInfo.serverName() == null) return;
        serverNameLabel.setText(serverInfo.serverName());
        motdLabel.setText(serverInfo.serverIntro());
        versionLabel.setText(serverInfo.serverVersion());
        playerCountLabel.setText(serverInfo.playerCount());
        serverInfoBox.getChildren().setAll(serverNameLabel, motdLabel, versionLabel, playerCountLabel);
    }

    private void updatePlayerList() {
        playerBox.getChildren().clear();
        if (simpleServerPlayer == null) return;
        for (String p : simpleServerPlayer.getPlayerList()) {
            Label playerLabel = new Label(p);
            playerLabel.getStyleClass().add("player-label");
            playerBox.getChildren().add(playerLabel);
        }
    }

    private void updateModList() {
        modListBox.getChildren().clear();
        if (modInfo != null) {
            tableView.getItems().setAll(modInfo);
        }
        modListBox.getChildren().setAll(titleLabel, tableView);
    }

    @Override
    public void translate() {
        btnServerInfo.setText(MENU_SERVER_INFO.getString());
        btnMods.setText(MENU_MOD_LIST.getString());
        btnPlayers.setText(MENU_PLAYER_LIST.getString());
        btnStatus.setText(MENU_SERVER_STATUS.getString());

        titleLabel.setText(MENU_SERVER_INFO.getString());
        serverNameLabel.setText(TITLE_SERVER_NAME.getString() + ": " + serverInfo.serverName());
        motdLabel.setText(TITLE_SERVER_INTRO.getString() + ": " + serverInfo.serverIntro());
        versionLabel.setText(TITLE_VERSION.getString() + ": " + serverInfo.serverVersion());
        playerCountLabel.setText(TITLE_PLAYER_COUNT.getString() + ": " + serverInfo.playerCount());
    }

}
