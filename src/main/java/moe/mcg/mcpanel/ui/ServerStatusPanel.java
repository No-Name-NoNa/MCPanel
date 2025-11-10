package moe.mcg.mcpanel.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import moe.mcg.mcpanel.api.IPanel;
import moe.mcg.mcpanel.api.i18n.Component;

public class ServerStatusPanel extends VBox implements IPanel<Object> {
    private static final Component NOT_IMPLEMENTED = Component.translatable("main.info.not_implemented");
    private Label label = new Label();

    public ServerStatusPanel() {
        getChildren().setAll(label);
    }


    @Override
    public void refresh(Object data) {

    }
}