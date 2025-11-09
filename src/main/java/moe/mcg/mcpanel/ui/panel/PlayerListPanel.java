package moe.mcg.mcpanel.ui.panel;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import moe.mcg.mcpanel.api.IPanel;
import moe.mcg.mcpanel.api.i18n.Component;
import moe.mcg.mcpanel.api.minecraft.SimpleServerPlayer;
import moe.mcg.mcpanel.api.pack.SimpleServerPlayerList;

import java.util.ArrayList;
import java.util.List;


public class PlayerListPanel extends VBox implements IPanel<SimpleServerPlayerList> {
    private static final Component NO_PLAYER = Component.translatable("main.player.no_player");
    private static final Component PLAYER_NAME_COLUMN_TITLE = Component.translatable("main.player.name");
    private static final Component PLAYER_UUID_COLUMN_TITLE = Component.translatable("main.player.uuid");
    private static final Component PLAYER_PING_COLUMN_TITLE = Component.translatable("main.player.ping");

    private List<SimpleServerPlayer> playerList = new ArrayList<>();

    private GridPane playerGrid = new GridPane();

    public PlayerListPanel() {
        getChildren().add(playerGrid);
    }

    @Override
    public void refresh(SimpleServerPlayerList data) {
        this.playerList = data.getPlayerList();
        playerGrid.getChildren().clear();

        playerGrid.setVgap(10);
        playerGrid.setHgap(15);
        playerGrid.setPadding(new Insets(10));

        // No players case
        if (playerList == null || playerList.isEmpty()) {
            Label noPlayerLabel = new Label(NO_PLAYER.getString());
            noPlayerLabel.getStyleClass().add("no-player-label");
            playerGrid.add(noPlayerLabel, 0, 0, 3, 1);  // Span across 3 columns
            return;
        }

        // Column Headers
        Label nameHeader = new Label(PLAYER_NAME_COLUMN_TITLE.getString());
        Label uuidHeader = new Label(PLAYER_UUID_COLUMN_TITLE.getString());
        Label pingHeader = new Label(PLAYER_PING_COLUMN_TITLE.getString());

        nameHeader.getStyleClass().add("player-header-cell");
        uuidHeader.getStyleClass().add("player-header-cell");
        pingHeader.getStyleClass().add("player-header-cell");

        playerGrid.add(nameHeader, 0, 0);
        playerGrid.add(uuidHeader, 1, 0);
        playerGrid.add(pingHeader, 2, 0);

        // Set column constraints to control width distribution
        playerGrid.getColumnConstraints().clear();
        playerGrid.getColumnConstraints().addAll(
                createColumnConstraint(120),  // Name column
                createColumnConstraint(180),  // UUID column
                createColumnConstraint(100)   // Ping column
        );

        // Player rows
        int rowIndex = 1; // Start from row 1 after headers
        for (SimpleServerPlayer p : playerList) {
            Label nameLabel = new Label(p.name());
            Label uuidLabel = new Label(p.uuid());
            Label pingLabel = new Label(p.ping());

            nameLabel.getStyleClass().add("player-name");
            uuidLabel.getStyleClass().add("player-uuid");
            pingLabel.getStyleClass().add("player-ping");

            // Ping styling
            try {
                int pingValue = Integer.parseInt(p.ping());
                pingLabel.getStyleClass().removeAll("ping-low", "ping-medium", "ping-high");
                if (pingValue < 100) {
                    pingLabel.getStyleClass().add("ping-low");
                } else if (pingValue < 200) {
                    pingLabel.getStyleClass().add("ping-medium");
                } else {
                    pingLabel.getStyleClass().add("ping-high");
                }
            } catch (NumberFormatException ignored) {
            }

            // Handle row click
            nameLabel.setOnMouseClicked(event -> showPlayerDetails(p));

            // Add player data to grid
            playerGrid.add(nameLabel, 0, rowIndex);
            playerGrid.add(uuidLabel, 1, rowIndex);
            playerGrid.add(pingLabel, 2, rowIndex);
            nameLabel.getStyleClass().add("player-row");
            uuidLabel.getStyleClass().add("player-row");
            pingLabel.getStyleClass().add("player-row");

            rowIndex++;
        }
    }

    private void showPlayerDetails(SimpleServerPlayer player) {
        // TODO: Implement player detail view
        System.out.println("Clicked player: " + player.name());
    }

    // Helper method to create column constraints with a given min width
    private ColumnConstraints createColumnConstraint(double minWidth) {
        ColumnConstraints column = new ColumnConstraints();
        column.setMinWidth(minWidth);
        column.setHgrow(Priority.ALWAYS);
        return column;
    }

}