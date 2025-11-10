package moe.mcg.mcpanel.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Setter;
import moe.mcg.mcpanel.api.IPanel;
import moe.mcg.mcpanel.api.MinecraftSkin2D;
import moe.mcg.mcpanel.api.i18n.Component;
import moe.mcg.mcpanel.api.i18n.ITranslatable;
import moe.mcg.mcpanel.api.i18n.TranslateManager;
import moe.mcg.mcpanel.api.minecraft.DetailedPlayer;
import moe.mcg.mcpanel.api.minecraft.ServerPlayer;
import moe.mcg.mcpanel.api.minecraft.SimpleVec3;

public class PlayerDetailPanel extends VBox implements IPanel<ServerPlayer>, ITranslatable {

    private static final Component USERNAME = Component.translatable("main.player.username");
    private static final Component UUID = Component.translatable("main.player.uuid");
    private static final Component PERMISSION = Component.translatable("main.player.permission");
    private static final Component PING = Component.translatable("main.player.ping");
    private static final Component LOCATION = Component.translatable("main.player.location");
    private static final Component DIMENSION = Component.translatable("main.player.dimension");
    private static final Component HEALTH = Component.translatable("main.player.health");
    private static final Component FOOD = Component.translatable("main.player.food");
    private static final Component RETURN = Component.translatable("main.player.return");
    private final Label usernameLabel;
    private final Label uuidLabel;
    private final Label pingLabel;
    private final Label locationLabel;
    private final Label dimensionLabel;
    private final Label healthLabel;
    private final Label hungryLabel;
    private final Label permissionLabel;
    private final PlayerListPanel playerListPanel;
    private final Button backButton;
    @Setter
    private MinecraftSkin2D skin2D;
    private ServerPlayer serverPlayer;

    public PlayerDetailPanel(PlayerListPanel playerListPanel, DetailedPlayer data) {
        TranslateManager.register(this);
        setSpacing(10);
        this.playerListPanel = playerListPanel;
        setPadding(new Insets(15));
        skin2D = data.getIcon();
        usernameLabel = new Label();
        uuidLabel = new Label();
        pingLabel = new Label();
        locationLabel = new Label();
        dimensionLabel = new Label();
        healthLabel = new Label();
        hungryLabel = new Label();
        permissionLabel = new Label();
        backButton = new Button(RETURN.getString());
        backButton.getStyleClass().add("back-button");
        backButton.setOnAction(event -> back());

        // Create Block 1: Player's Avatar and Basic Information
        VBox playerInfoBox = new VBox(10, skin2D, usernameLabel, uuidLabel, pingLabel);
        playerInfoBox.getStyleClass().add("player-info-box");  // Apply CSS class for styling

        // Create Block 2: Health, Hunger, Location, Dimension
        VBox statsBox = new VBox(10, healthLabel, hungryLabel, locationLabel, dimensionLabel);
        statsBox.getStyleClass().add("stats-box");  // Apply CSS class for styling

        // Back button container at the bottom
        HBox buttonBox = new HBox(10, backButton);
        buttonBox.getStyleClass().add("button-box");  // Apply CSS class for styling

        // Combine all elements into the layout
        getChildren().addAll(playerInfoBox, statsBox, buttonBox);

        refresh(new ServerPlayer(data.getUsername(), data.getUuid(), data.getLocation(), data.getDimension(),
                data.getGamemode(), Float.parseFloat(data.getHealth()),
                Integer.parseInt(data.getHungry()), Integer.parseInt(data.getPing()),
                Integer.parseInt(data.getPermission())));
    }

    @Override
    public void refresh(ServerPlayer data) {
        serverPlayer = data;
        getChildren().clear();
        if (data != null) {
            trans(data);
        }

        // Create Blocks for layout again after refreshing
        VBox playerInfoBox = new VBox(10, skin2D, usernameLabel, uuidLabel, permissionLabel, pingLabel);
        playerInfoBox.getStyleClass().add("player-info-box");

        VBox statsBox = new VBox(10, healthLabel, hungryLabel, locationLabel, dimensionLabel);
        statsBox.getStyleClass().add("stats-box");

        HBox buttonBox = new HBox(10, backButton);
        buttonBox.getStyleClass().add("button-box");

        // Combine all elements back into the layout
        getChildren().addAll(playerInfoBox, statsBox, buttonBox);
    }

    private String formatLocation(SimpleVec3 location) {
        if (location != null) {
            return "(" + location.x + ", " + location.y + ", " + location.z + ")";
        }
        return "Unknown";
    }

    public void back() {
        this.playerListPanel.getChildren().clear();
        this.playerListPanel.getChildren().setAll(this.playerListPanel.getPlayerContainer());
    }

    @Override
    public void translate() {
        trans(serverPlayer);
        backButton.setText(RETURN.getString());
    }

    private void trans(ServerPlayer serverPlayer) {
        usernameLabel.setText(USERNAME.getString() + serverPlayer.name());
        uuidLabel.setText(UUID.getString() + serverPlayer.uuid());
        pingLabel.setText(PING.getString() + serverPlayer.ping());
        permissionLabel.setText(PERMISSION.getString() + serverPlayer.permissionLevel());
        locationLabel.setText(LOCATION.getString() + formatLocation(serverPlayer.location()));
        dimensionLabel.setText(DIMENSION.getString() + serverPlayer.dimension());
        healthLabel.setText(HEALTH.getString() + serverPlayer.health());
        hungryLabel.setText(FOOD.getString() + serverPlayer.food());
    }
}
