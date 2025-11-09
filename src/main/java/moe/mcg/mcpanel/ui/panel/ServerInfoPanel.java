package moe.mcg.mcpanel.ui.panel;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import moe.mcg.mcpanel.api.IPanel;
import moe.mcg.mcpanel.api.i18n.Component;
import moe.mcg.mcpanel.api.pack.ServerInfo;

public class ServerInfoPanel extends VBox implements IPanel<ServerInfo> {
    private static final Component TITLE_SERVER_NAME = Component.translatable("main.info.server_name");
    private static final Component TITLE_SERVER_INTRO = Component.translatable("main.info.server_intro");
    private static final Component TITLE_VERSION = Component.translatable("main.info.version");
    private static final Component TITLE_PLAYER_COUNT = Component.translatable("main.info.player_count");

    public final VBox serverInfoBox = new VBox(5);

    private ServerInfo serverInfo;

    public ServerInfoPanel() {
        getChildren().add(serverInfoBox);
    }

    public void setServerInfo(ServerInfo info) {
        this.serverInfo = info;
        getChildren().setAll(serverInfoBox);
    }

    public void refresh(ServerInfo serverInfo) {
        if (serverInfo.serverName() == null) return;
        this.serverInfo = serverInfo;

        serverInfoBox.getChildren().clear();

        serverInfoBox.setSpacing(10);
        serverInfoBox.setAlignment(Pos.TOP_LEFT);

        serverInfoBox.getChildren().addAll(
                createInfoRow(TITLE_SERVER_NAME.getString(), this.serverInfo.serverName()),
                createInfoRow(TITLE_SERVER_INTRO.getString(), this.serverInfo.serverIntro()),
                createInfoRow(TITLE_VERSION.getString(), this.serverInfo.serverVersion()),
                createInfoRow(TITLE_PLAYER_COUNT.getString(), this.serverInfo.playerCount())
        );
    }

}
