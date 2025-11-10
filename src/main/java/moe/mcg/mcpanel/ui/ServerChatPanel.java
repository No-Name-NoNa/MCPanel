package moe.mcg.mcpanel.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import lombok.Getter;
import moe.mcg.mcpanel.api.IPanel;
import moe.mcg.mcpanel.api.i18n.Component;
import moe.mcg.mcpanel.api.i18n.ITranslatable;
import moe.mcg.mcpanel.api.i18n.TranslateManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.List;

import static moe.mcg.mcpanel.Main.LOGGER;

public class ServerChatPanel extends VBox implements IPanel<Object>, ITranslatable {

    private static final Component SEND = Component.translatable("main.chat.send");
    private static final Component WORD = Component.translatable("main.chat.word");

    @Getter
    private final TextArea chatArea;
    private final TextField inputField;
    private final Button sendButton;

    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;

    public ServerChatPanel(Socket socket, DataInputStream in, DataOutputStream out) {
        this.socket = socket;
        this.in = in;
        this.out = out;
        TranslateManager.register(this);
        chatArea = new TextArea();
        inputField = new TextField();
        sendButton = new Button(SEND.getString());

        chatArea.getStyleClass().add("chat-area");
        inputField.getStyleClass().add("input-field");
        sendButton.getStyleClass().add("send-button");

        chatArea.setEditable(false);
        chatArea.setWrapText(true);

        inputField.setPromptText(WORD.getString());

        sendButton.setOnAction(event -> send());
        inputField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                send();
            }
        });

        setSpacing(10);
        setPadding(new Insets(10));

        HBox inputBox = new HBox(10, inputField, sendButton);
        inputBox.getStyleClass().add("input-box");
        VBox.setVgrow(chatArea, Priority.ALWAYS);

        getChildren().addAll(chatArea, inputBox);
    }

    @Override
    public void refresh(Object data) {
    }

    private void send() {
        String message = inputField.getText();
        if (message.startsWith("/")) {
            sendMessage("COMMAND-" + message);
        } else {
            sendMessage("CHAT-" + message);
        }
        /*    chatArea.appendText("[Server]: " + message + "\n");
         */
        inputField.clear();
    }

    @Override
    public void translate() {
        sendButton.setText(SEND.getString());
        inputField.setPromptText(WORD.getString());
    }

    public void fill(List<String> text) {
        for (String s : text) {
            if (s.contains("main.player")) {
                String[] split = s.split(": ");
                String name = split[0];
                String key = split[1];
                getChatArea().appendText(name + " " + Component.translatable(key).getString() + '\n');
            } else {
                getChatArea().appendText(s + '\n');
            }
        }
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
}
