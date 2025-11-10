package moe.mcg.mcpanel.ui;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;
import moe.mcg.mcpanel.api.IPanel;
import moe.mcg.mcpanel.api.MinecraftSkin2D;
import moe.mcg.mcpanel.api.Status;
import moe.mcg.mcpanel.api.i18n.Component;
import moe.mcg.mcpanel.api.i18n.ITranslatable;
import moe.mcg.mcpanel.api.i18n.TranslateManager;
import moe.mcg.mcpanel.api.minecraft.DetailedPlayer;
import moe.mcg.mcpanel.api.minecraft.ServerPlayer;
import moe.mcg.mcpanel.api.minecraft.SimpleServerPlayer;
import moe.mcg.mcpanel.api.pack.SimpleServerPlayerList;
import moe.mcg.mcpanel.image.ApplicationImage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static moe.mcg.mcpanel.Main.LOGGER;
import static moe.mcg.mcpanel.ui.MainPanelWindow.executor;
import static moe.mcg.mcpanel.ui.MainPanelWindow.status;


public class PlayerListPanel extends VBox implements IPanel<SimpleServerPlayerList>, ITranslatable {
    private static final Component NO_PLAYER = Component.translatable("main.player.no_player");

    private static final String MOJANG_API_URL = "https://api.mojang.com/users/profiles/minecraft/";
    private static final String SESSION_API_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";

    private final Map<String, MinecraftSkin2D> cachedMinecraftSkin2D = new HashMap<>();
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private List<SimpleServerPlayer> playerList = new ArrayList<>();
    @Getter
    @Setter
    private ServerPlayer player;
    @Getter
    @Setter
    private DetailedPlayer detailedPlayer = new DetailedPlayer("dev", "123", new MinecraftSkin2D(ApplicationImage.INSTANCE.getResource("tenshi.png")));
    @Getter
    @Setter
    private PlayerDetailPanel playerDetailPanel;
    @Getter
    private VBox playerContainer = new VBox();

    public PlayerListPanel(Socket socket, DataInputStream in, DataOutputStream out) {
        this.socket = socket;
        this.in = in;
        this.out = out;
        playerDetailPanel = new PlayerDetailPanel(this, new DetailedPlayer("", "", new MinecraftSkin2D(ApplicationImage.INSTANCE.getResource("steve.png"))));
        getStyleClass().add("player-list-panel");
        getChildren().setAll(playerContainer);
        TranslateManager.register(this);
    }


    @Override
    public void refresh(SimpleServerPlayerList data) {
        this.playerList = data.getPlayerList();
        playerContainer.getChildren().clear();

        // No players case
        if (playerList == null || playerList.isEmpty()) {
            Label noPlayerLabel = new Label(NO_PLAYER.getString());
            noPlayerLabel.getStyleClass().add("no-player-label");
            playerContainer.getChildren().add(noPlayerLabel);
            return;
        }

        // Player rows (with index in front of player name)
        for (int i = 0; i < playerList.size(); i++) {
            SimpleServerPlayer p = playerList.get(i);

            // Create a label with the player index and name combined
            Label playerLabel = new Label((i + 1) + ". " + p.name());

            playerLabel.getStyleClass().add("player-row");

            // Handle row click
            playerLabel.setOnMouseClicked(event -> showPlayerDetails(p));

            playerContainer.getChildren().add(playerLabel);
        }
    }

    private void showPlayerDetails(SimpleServerPlayer player) {
        String username = player.name();

        if (cachedMinecraftSkin2D.containsKey(username)) {
            playerDetailPanel.setSkin2D(cachedMinecraftSkin2D.get(username));
        } else {
            playerDetailPanel.setSkin2D(new MinecraftSkin2D(ApplicationImage.INSTANCE.getResource("steve.png")));
            fetchPlayerDetails(username);
        }
        setStatus(Status.DETAILED_PLAYER.setUsername(username), username);
        getChildren().setAll(playerDetailPanel);
    }

    private void fetchPlayerDetails(String username) {
        Task<String> uuidTask = new Task<>() {
            @Override
            protected String call() {
                return getPlayerUUID(username);
            }
        };

        uuidTask.setOnSucceeded(event -> {
            String uuid = uuidTask.getValue();
            if (uuid != null) {
                // Step 2: Fetch skin and cape details
                fetchSkinDetails(uuid, username);
            }
        });

        uuidTask.setOnFailed(event -> {
            Throwable ex = uuidTask.getException();
            LOGGER.error(ex.getMessage());
        });

        // Start the task in the background
        new Thread(uuidTask).start();
    }

    private String getPlayerUUID(String username) {
        String urlString = MOJANG_API_URL + username;
        try {
            URL url = URI.create(urlString).toURL();

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            // 获取响应代码
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) { // 如果请求成功
                // 读取响应数据
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // 解析 JSON 返回
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(response.toString(), JsonObject.class);
                return jsonObject.get("id").getAsString(); // 返回 UUID
            } else {
                System.out.println("GET request failed. Response Code: " + responseCode);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    private void fetchSkinDetails(String uuid, String username) {
        Task<Void> skinTask = new Task<>() {
            @Override
            protected Void call() {
                String urlString = SESSION_API_URL + uuid;
                try {

                    LOGGER.info("STARTING SKIN DETAILS URL: {}", urlString);

                    // 创建 URL 对象
                    URL url = URI.create(urlString).toURL();

                    // 打开连接
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000); // 设置连接超时
                    connection.setReadTimeout(5000);    // 设置读取超时

                    // 获取响应代码
                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) { // 如果请求成功

                        // 读取响应数据
                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        // 解析 JSON 返回
                        Gson gson = new Gson();
                        JsonObject jsonObject = gson.fromJson(response.toString(), JsonObject.class);

                        // 解析 properties 字段中的 textures
                        JsonArray properties = jsonObject.getAsJsonArray("properties");
                        for (int i = 0; i < properties.size(); i++) {
                            JsonObject property = properties.get(i).getAsJsonObject();
                            if (property.get("name").getAsString().equals("textures")) {
                                String base64Value = property.get("value").getAsString();

                                // 进行 Base64 解码
                                String decodedValue = new String(java.util.Base64.getDecoder().decode(base64Value));
                                JsonObject textures = gson.fromJson(decodedValue, JsonObject.class);

                                // 提取皮肤的 URL
                                JsonObject skin1 = textures.getAsJsonObject("textures");
                                JsonObject skin = skin1.getAsJsonObject("SKIN");
                                if (skin != null) {
                                    String skinUrl = skin.get("url").getAsString();
                                    LOGGER.info("skinUrl: {}", skinUrl);
                                    Platform.runLater(() -> displayPlayerSkin(skinUrl, username));
                                } else {
                                    // 如果没有 "SKIN" 属性，输出日志并处理缺失
                                    System.out.println("No SKIN property found in textures.");
                                }
                                return null;
                            }
                        }
                    } else {
                        System.out.println("GET request failed. Response Code: " + responseCode);
                    }
                } catch (IOException e) {
                    LOGGER.error(e.getMessage());
                }
                return null;
            }
        };

        skinTask.setOnFailed(event -> {
            Throwable ex = skinTask.getException();
            LOGGER.error(ex.getMessage());
        });

        new Thread(skinTask).start();
    }

    private void displayPlayerSkin(String skinUrl, String username) {
        Task<UserImage> loadImageTask = new Task<>() {
            @Override
            protected UserImage call() {
                return new UserImage(new Image(skinUrl), username);
            }
        };
        loadImageTask.setOnSucceeded(event -> {
            UserImage skinUserImage = loadImageTask.getValue();
            playerDetailPanel.setSkin2D(new MinecraftSkin2D(skinUserImage.image()));
            cachedMinecraftSkin2D.put(username, new MinecraftSkin2D(skinUserImage.image()));

            getChildren().set(0, playerDetailPanel);
        });
        loadImageTask.setOnFailed(event -> {
            Throwable ex = loadImageTask.getException();
            LOGGER.error("Failed to load skin image: {}", ex.getMessage());
        });
        new Thread(loadImageTask).start();
    }

    private void setStatus(Status mods, String username) {
        executor.submit(() -> {
            status = mods;
            sendMessage(status.name() + "-" + username);
        });
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

    @Override
    public void translate() {

    }

    record UserImage(javafx.scene.image.Image image, String username) {
    }

}