package moe.mcg.mcpanel.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import moe.mcg.mcpanel.api.IPanel;
import moe.mcg.mcpanel.api.i18n.Component;
import moe.mcg.mcpanel.api.i18n.ITranslatable;
import moe.mcg.mcpanel.api.i18n.TranslateManager;
import moe.mcg.mcpanel.api.pack.ServerInfo;

public class ServerInfoPanel extends VBox implements IPanel<ServerInfo>, ITranslatable {
    private static final Component TITLE_SERVER_NAME = Component.translatable("main.info.server_name");
    private static final Component TITLE_SERVER_INTRO = Component.translatable("main.info.server_intro");
    private static final Component TITLE_VERSION = Component.translatable("main.info.version");
    private static final Component TITLE_PLAYER_COUNT = Component.translatable("main.info.player_count");

    private final VBox serverInfoBox = new VBox(5);
    private final HBox serverNameRow = new HBox(10);
    private final HBox serverIntroRow = new HBox(10);
    private final HBox versionRow = new HBox(10);
    private final HBox playerCountRow = new HBox(10);
    private final Label serverNameLabel = new Label();
    private final Label serverIntroLabel = new Label();
    private final Label versionLabel = new Label();
    private final Label playerCountLabel = new Label();

    private ServerInfo serverInfo;

    public ServerInfoPanel() {
        TranslateManager.register(this);
        getChildren().add(serverInfoBox);
    }

    public void setServerInfo(ServerInfo info) {
        this.serverInfo = info;
        refresh(info);
    }

    public void refresh(ServerInfo serverInfo) {
        if (serverInfo.serverName() == null) return;
        this.serverInfo = serverInfo;

        serverInfoBox.getChildren().clear();

        serverInfoBox.setSpacing(10);
        serverInfoBox.setAlignment(Pos.CENTER);
        serverInfoBox.getStyleClass().add("server-info-card");

        trans();

        serverInfoBox.getChildren().addAll(
                serverNameRow,
                serverIntroRow,
                versionRow,
                playerCountRow
        );
    }

    private void updateInfoRow(HBox row, Label label, String title, String value, String titleStyle) {
        row.getChildren().clear();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 12, 8, 12));
        row.getStyleClass().add("info-row");

        label.setText(value != null ? value : "-");
        label.getStyleClass().add("info-value");

        Label titleLabel = new Label(title + ":");
        titleLabel.getStyleClass().add(titleStyle);

        row.getChildren().setAll(titleLabel, label);
    }


    @Override
    public void translate() {
        trans();
    }

    private void trans() {
        updateInfoRow(serverNameRow, serverNameLabel, TITLE_SERVER_NAME.getString(), this.serverInfo.serverName(), "server-name-row");
        updateInfoRow(serverIntroRow, serverIntroLabel, TITLE_SERVER_INTRO.getString(), this.serverInfo.serverIntro(), "server-info-title");
        updateInfoRow(versionRow, versionLabel, TITLE_VERSION.getString(), this.serverInfo.serverVersion(), "server-info-title");
        updateInfoRow(playerCountRow, playerCountLabel, TITLE_PLAYER_COUNT.getString(), this.serverInfo.playerCount(), "server-info-title");
    }
}

