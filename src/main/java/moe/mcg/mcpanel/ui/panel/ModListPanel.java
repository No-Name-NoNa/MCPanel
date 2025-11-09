package moe.mcg.mcpanel.ui.panel;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import moe.mcg.mcpanel.api.IPanel;
import moe.mcg.mcpanel.api.i18n.Component;
import moe.mcg.mcpanel.api.pack.ModInfo;

import java.util.ArrayList;
import java.util.List;

public class ModListPanel extends VBox implements IPanel<List<ModInfo>> {
    private static final Component NO_MODS = Component.translatable("main.info.no_mods");
    private static final Component MOD_ID_COLUMN_TITLE = Component.translatable("main.info.mod_id");
    private static final Component MOD_NAME_COLUMN_TITLE = Component.translatable("main.info.mod_name");
    private static final Component MOD_VERSION_COLUMN_TITLE = Component.translatable("main.info.mod_version");
    private static final Component MOD_URL_COLUMN_TITLE = Component.translatable("main.info.mod_url");

    private List<ModInfo> modInfo = new ArrayList<>();
    private List<List<Label>> realModListLabel = new ArrayList<>();
    private VBox modListMainBox = new VBox(5);

    public ModListPanel() {
        getChildren().add(modListMainBox);
    }

    @Override
    public void refresh(List<ModInfo> data) {
        this.modInfo = data;
        modListMainBox.getChildren().clear();
        realModListLabel.clear();

        if (modInfo == null || modInfo.isEmpty()) {
            Label noModsLabel = new Label(NO_MODS.getString());
            noModsLabel.getStyleClass().add("no-mods-label");
            modListMainBox.getChildren().add(noModsLabel);
            return;
        }

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("mod-header");

        Label idHeader = new Label(MOD_ID_COLUMN_TITLE.getString());
        Label nameHeader = new Label(MOD_NAME_COLUMN_TITLE.getString());
        Label versionHeader = new Label(MOD_VERSION_COLUMN_TITLE.getString());
        Label urlHeader = new Label(MOD_URL_COLUMN_TITLE.getString());

        idHeader.setMinWidth(120);
        nameHeader.setMinWidth(180);
        versionHeader.setMinWidth(100);
        urlHeader.setMinWidth(200);

        idHeader.getStyleClass().add("mod-header-cell");
        nameHeader.getStyleClass().add("mod-header-cell");
        versionHeader.getStyleClass().add("mod-header-cell");
        urlHeader.getStyleClass().add("mod-header-cell");

        header.getChildren().addAll(idHeader, nameHeader, versionHeader, urlHeader);
        modListMainBox.getChildren().add(header);

        for (ModInfo info : modInfo) {
            HBox row = new HBox(15);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(6, 0, 6, 0));
            row.getStyleClass().add("mod-row");

            Label idLabel = new Label(info.getModId());
            Label nameLabel = new Label(info.getModName());
            Label versionLabel = new Label(info.getModVersion());
            Label urlLabel = new Label(info.getModUrl());

            idLabel.setMinWidth(120);
            nameLabel.setMinWidth(180);
            versionLabel.setMinWidth(100);
            urlLabel.setMinWidth(200);

            idLabel.getStyleClass().addAll("mod-cell", "mod-first-cell");
            nameLabel.getStyleClass().add("mod-cell");
            versionLabel.getStyleClass().add("mod-cell");
            urlLabel.getStyleClass().add("mod-cell");

            row.getChildren().addAll(idLabel, nameLabel, versionLabel, urlLabel);
            modListMainBox.getChildren().add(row);

            realModListLabel.add(List.of(idLabel, nameLabel, versionLabel, urlLabel));
        }
    }


}
