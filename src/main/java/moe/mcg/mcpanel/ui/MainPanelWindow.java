package moe.mcg.mcpanel.ui;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import moe.mcg.mcpanel.api.Status;
import moe.mcg.mcpanel.api.i18n.Component;
import moe.mcg.mcpanel.api.i18n.ITranslatable;
import moe.mcg.mcpanel.api.i18n.TranslationManager;
import moe.mcg.mcpanel.api.minecraft.ServerPlayer;
import moe.mcg.mcpanel.api.minecraft.ServerStatus;
import moe.mcg.mcpanel.api.pack.ModInfo;
import moe.mcg.mcpanel.api.pack.ServerInfo;
import moe.mcg.mcpanel.api.pack.SimpleServerPlayerList;
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
    public static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Component MENU_SERVER_INFO = Component.translatable("main.menu.server_info");
    private static final Component MENU_MOD_LIST = Component.translatable("main.menu.mod_list");
    private static final Component MENU_PLAYER_LIST = Component.translatable("main.menu.player_list");
    private static final Component MENU_SERVER_STATUS = Component.translatable("main.menu.server_status");
    private static final Component MENU_CHAT = Component.translatable("main.menu.chat");
    private static final Component MENU_OPTION = Component.translatable("main.menu.option");

    private static final Component TITLE_MOD_LIST = Component.translatable("main.info.mod_list");
    private static final Component TITLE_PLAYER_LIST = Component.translatable("main.info.player_list");
    private static final Component TITLE_SERVER_STATUS = Component.translatable("main.info.server_status");
    private static final Component NOT_IMPLEMENTED = Component.translatable("main.info.not_implemented");
    public static Status status = Status.INFO;
    private final Stage stage;
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final Gson gson = new Gson();
    private final ModListPanel modListPanel = new ModListPanel();
    private final PlayerListPanel playerListPanel;
    private final ServerInfoPanel serverInfoPanel = new ServerInfoPanel();
    private final ServerStatusPanel serverStatusPanel = new ServerStatusPanel();
    private final OptionPanel optionPanel = new OptionPanel();
    private final ServerChatPanel serverChatPanel;
    ToggleButton btnServerInfo = new ToggleButton(MENU_SERVER_INFO.getString());
    ToggleButton btnMods = new ToggleButton(MENU_MOD_LIST.getString());
    ToggleButton btnPlayers = new ToggleButton(MENU_PLAYER_LIST.getString());
    ToggleButton btnStatus = new ToggleButton(MENU_SERVER_STATUS.getString());
    ToggleButton btnChat = new ToggleButton(MENU_CHAT.getString());
    ToggleButton btnOpt = new ToggleButton(MENU_OPTION.getString());
    ToggleGroup group = new ToggleGroup();
    private SimpleServerPlayerList simpleServerPlayerList;
    private List<ModInfo> modInfo = new ArrayList<>();
    private ServerPlayer serverPlayer;
    private ServerInfo serverInfo;
    private VBox rightPanel = new VBox(5);
    private Label titleLabel;
    private boolean chatted = false;
    private boolean statuses = false;

    public MainPanelWindow(Stage stage, ServerInfo config, Socket socket, DataInputStream in, DataOutputStream out) throws IOException {
        this.stage = stage;
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.serverInfo = config;
        playerListPanel = new PlayerListPanel(socket, in, out);
        serverChatPanel = new ServerChatPanel(socket, in, out);
        LOGGER.info("Initializing MainPanelWindow");
        TranslationManager.register(this);
        initUI(config);
    }

    private void initUI(ServerInfo serverInfo) {
        startListenThread();
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.getStyleClass().add("root");

        VBox leftMenu = new VBox(15);
        leftMenu.setAlignment(Pos.TOP_LEFT);
        leftMenu.setPadding(new Insets(10, 20, 10, 10));
        leftMenu.getStyleClass().add("sidebar");

        btnServerInfo.setToggleGroup(group);
        btnMods.setToggleGroup(group);
        btnPlayers.setToggleGroup(group);
        btnStatus.setToggleGroup(group);

        for (ToggleButton b : new ToggleButton[]{btnServerInfo, btnMods, btnPlayers, btnStatus, btnChat, btnOpt}) {
            b.getStyleClass().add("sidebar-button");
        }

        leftMenu.getChildren().addAll(btnServerInfo, btnMods, btnPlayers, btnStatus, btnChat, btnOpt);

        ScrollPane leftScrollPane = new ScrollPane(leftMenu);
        leftScrollPane.setFitToWidth(true);
        leftScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        leftScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        rightPanel = new VBox(10);
        rightPanel.setAlignment(Pos.TOP_LEFT);
        rightPanel.setPadding(new Insets(20));
        rightPanel.getStyleClass().add("card");
        serverInfoPanel.setServerInfo(serverInfo);
        rightPanel.getChildren().setAll(serverInfoPanel);
        leftScrollPane.setFitToHeight(true);

        ScrollPane rightScrollPane = new ScrollPane(rightPanel);
        rightScrollPane.setFitToWidth(true);
        rightScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        rightScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        rightScrollPane.setFitToHeight(true);

        //撑开容器
        VBox.setVgrow(serverInfoPanel, Priority.ALWAYS);
        VBox.setVgrow(serverChatPanel, Priority.ALWAYS);
        VBox.setVgrow(serverStatusPanel, Priority.ALWAYS);
        VBox.setVgrow(optionPanel, Priority.ALWAYS);
        optionPanel.getStyleClass().add("option-panel");

        titleLabel = new Label();
        titleLabel.getStyleClass().add("player-title");


        //各种按钮
        btnServerInfo.setOnAction(e -> {
            setActiveButton(btnServerInfo);
            try {
                showServerInfo();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        btnMods.setOnAction(e -> {
            setActiveButton(btnMods);
            try {
                showModList();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        btnPlayers.setOnAction(e -> {
            setActiveButton(btnPlayers);
            try {
                showPlayerList();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        btnStatus.setOnAction(e -> {
            setActiveButton(btnStatus);
            try {
                showServerStatus();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        btnChat.setOnAction(e -> {
            setActiveButton(btnChat);
            showChat();
        });

        btnOpt.setOnAction(e -> {
            setActiveButton(btnOpt);
            showOption();
        });

        root.setCenter(rightScrollPane);
        root.setLeft(leftScrollPane);

        Scene scene = new Scene(root, 800, 500);
        leftScrollPane.getStyleClass().add("scroll-pane");
        rightScrollPane.getStyleClass().add("scroll-pane");

        scene.getStylesheets().add(ApplicationCSS.INSTANCE.getResource("mainpanel.css"));
        scene.getStylesheets().add(ApplicationCSS.INSTANCE.getResource("mod.css"));
        scene.getStylesheets().add(ApplicationCSS.INSTANCE.getResource("playerlist.css"));
        scene.getStylesheets().add(ApplicationCSS.INSTANCE.getResource("info.css"));
        scene.getStylesheets().add(ApplicationCSS.INSTANCE.getResource("sidebar.css"));
        scene.getStylesheets().add(ApplicationCSS.INSTANCE.getResource("chat.css"));
        scene.getStylesheets().add(ApplicationCSS.INSTANCE.getResource("chart.css"));
        scene.getStylesheets().add(ApplicationCSS.INSTANCE.getResource("option.css"));
        stage.setScene(scene);
        stage.show();
    }


    private void startListenThread() {
        Thread listener = new Thread(() -> {
            try {
                while (!socket.isClosed()) {
                    handleServerMessage();
                }
            } catch (Exception e) {
                LOGGER.error("Disconnected: {}", e.getMessage());
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
                    Platform.runLater(() -> serverInfoPanel.refresh(serverInfo));
                    break;
                }
                case "MODS": {
                    Type listType = new TypeToken<List<ModInfo>>() {
                    }.getType();
                    modInfo = gson.fromJson(json, listType);
                    Platform.runLater(() -> modListPanel.refresh(modInfo));
                    break;
                }
                case "PLAYERS": {
                    simpleServerPlayerList = gson.fromJson(json, SimpleServerPlayerList.class);
                    Platform.runLater(() -> playerListPanel.refresh(simpleServerPlayerList));
                    break;
                }
                case "DETAILED_PLAYER": {
                    serverPlayer = gson.fromJson(json, ServerPlayer.class);
                    Platform.runLater(() -> playerListPanel.getPlayerDetailPanel().refresh(serverPlayer));
                    break;
                }

                case "CHAT": {
                    Type listType = new TypeToken<List<String>>() {
                    }.getType();
                    List<String> chatList = gson.fromJson(json, listType);
                    Platform.runLater(() -> {
                        serverChatPanel.getChatArea().getChildren().clear();
                        serverChatPanel.refresh(chatList);
                    });
                    break;
                }
                case "CHAT_CONTINUE": {
                    Type listType = new TypeToken<List<String>>() {
                    }.getType();
                    List<String> chatList = gson.fromJson(json, listType);
                    Platform.runLater(() -> serverChatPanel.refresh(chatList));
                    break;
                }

                case "STATUS": {
                    Type listType = new TypeToken<List<ServerStatus>>() {
                    }.getType();
                    List<ServerStatus> statusList = gson.fromJson(json, listType);
                    Platform.runLater(() -> {
                        serverStatusPanel.getTickQueue().clear();
                        serverStatusPanel.refresh(statusList);
                    });
                    break;
                }

                case "STATUS_CONTINUE": {
                    Type listType = new TypeToken<List<ServerStatus>>() {
                    }.getType();
                    List<ServerStatus> statusList = gson.fromJson(json, listType);
                    Platform.runLater(() -> serverStatusPanel.refresh(statusList));
                    break;
                }

                default:
                    LOGGER.warn("Unknown message type : {}", messageType);
            }
        } catch (Exception ignored) {
        }
    }


    private void showServerInfo() throws IOException {
        /*   rightPanel.getChildren().setAll(serverInfoBox);*/
        rightPanel.getChildren().setAll(serverInfoPanel);
        setStatus(Status.INFO);
    }

    private void showModList() throws IOException {
/*        rightPanel.getChildren().setAll(modListMainBox);
        modListMainBox.getChildren().clear();
        for (List<Label> labels: realModListLabel) {
            modListMainBox.getChildren().addAll(labels);
        }
        titleLabel.setText(TITLE_MOD_LIST.getString());*/
        rightPanel.getChildren().setAll(modListPanel);
        setStatus(Status.MODS);
    }


    private void showPlayerList() throws IOException {
        rightPanel.getChildren().setAll(playerListPanel);
        setStatus(Status.PLAYERS);
    }

    private void showServerStatus() throws IOException {
        rightPanel.getChildren().setAll(serverStatusPanel);

        if (!statuses) {
            setStatus(Status.STATUS);
            statuses = true;
        } else {
            setStatus(Status.STATUS_CONTINUE);
        }

    }

    private void showChat() {
        rightPanel.getChildren().setAll(serverChatPanel);
        if (!chatted) {
            setStatus(Status.CHAT);
            chatted = true;
        } else {
            setStatus(Status.CHAT_CONTINUE);
        }
    }

    private void showOption() {
        rightPanel.getChildren().setAll(optionPanel);
        setStatus(Status.OPTION);
    }

    private void setStatus(Status mods) {
        executor.submit(() -> {
            status = mods;
            sendMessage(status.name());
        });
    }

    private void setActiveButton(ToggleButton selectedButton) {
        btnServerInfo.getStyleClass().remove("selected");
        btnMods.getStyleClass().remove("selected");
        btnPlayers.getStyleClass().remove("selected");
        btnStatus.getStyleClass().remove("selected");
        btnChat.getStyleClass().remove("selected");
        btnOpt.getStyleClass().remove("selected");
        selectedButton.getStyleClass().add("selected");
    }


    @Override
    public void translate() {
        btnServerInfo.setText(MENU_SERVER_INFO.getString());
        btnMods.setText(MENU_MOD_LIST.getString());
        btnPlayers.setText(MENU_PLAYER_LIST.getString());
        btnStatus.setText(MENU_SERVER_STATUS.getString());
        btnChat.setText(MENU_CHAT.getString());
        btnOpt.setText(MENU_OPTION.getString());
        titleLabel.setText(MENU_SERVER_INFO.getString());
    }

}
