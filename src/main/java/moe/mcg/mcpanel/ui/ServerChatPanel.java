package moe.mcg.mcpanel.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import lombok.Getter;
import moe.mcg.mcpanel.api.BaiduTranslationApi;
import moe.mcg.mcpanel.api.IPanel;
import moe.mcg.mcpanel.api.i18n.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static moe.mcg.mcpanel.Main.LOGGER;

public class ServerChatPanel extends VBox implements IPanel<List<String>>, ITranslatable {

    private static final Component SEND = Component.translatable("main.chat.send");
    private static final Component WORD = Component.translatable("main.chat.word");
    private static final Map<Integer, String> translatedTexts = new ConcurrentHashMap<>();
    public static boolean clearCache = false;
    private final ScrollPane chatScrollPane;
    @Getter
    private final TextFlow chatArea;
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

        chatArea = new TextFlow();
        chatScrollPane = new ScrollPane(chatArea);
        chatScrollPane.setFitToWidth(true);
        chatScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        inputField = new TextField();
        sendButton = new Button(SEND.getString());

        chatArea.getStyleClass().add("chat-area");
        inputField.getStyleClass().add("input-field");
        sendButton.getStyleClass().add("send-button");

        inputField.setPromptText(WORD.getString());

        sendButton.setOnAction(event -> send());
        inputField.setOnKeyPressed(event -> {
            if (event.getCode().toString().equals("ENTER")) {
                send();
            }
        });

        setSpacing(10);
        setPadding(new Insets(10));

        HBox inputBox = new HBox(10, inputField, sendButton);
        inputBox.getStyleClass().add("input-box");
        VBox.setVgrow(chatScrollPane, Priority.ALWAYS);
        getChildren().setAll(chatScrollPane, inputBox);
    }

    public static String replaceLast(String text, String target, String replacement) {
        if (text == null || target == null || target.isEmpty()) {
            return text;
        }

        int lastIndex = text.lastIndexOf(target);
        if (lastIndex == -1) {
            return text;
        }

        return text.substring(0, lastIndex) + replacement + text.substring(lastIndex + target.length());
    }

    @Override
    public void refresh(List<String> data) {
        if (clearCache) {
            for (int i = 0; i < chatArea.getChildren().size(); i++) {
                if (translatedTexts.containsKey(i)) {
                    Text t = (Text) chatArea.getChildren().get(i);
                    String text = replaceLast(t.getText(), translatedTexts.get(i), "");
                    Text text1 = new Text(text);
                    text1.setFill(Color.BLACK);
                    chatArea.getChildren().set(i, text1);
                }
            }
            clearCache = false;
            translatedTexts.clear();
        }
        fill(data);
    }

    private void send() {
        String message = inputField.getText();
        if (message.isEmpty()) return;
        if (message.startsWith("/")) {
            sendMessage("COMMAND-" + message);
        } else {
            sendMessage("CHAT-" + message);
        }
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
                Text playerKeyText = new Text(name + " " + Component.translatable(key).getString() + "\n");
                playerKeyText.setFill(Color.ORANGE);
                chatArea.getChildren().addAll(playerKeyText);
            } else {
                Text normalText = new Text(s + "\n");
                normalText.setFill(Color.BLACK);
                chatArea.getChildren().add(normalText);
            }

            chatScrollPane.setVvalue(1.0);
        }

        for (int i = 0; i < chatArea.getChildren().size(); i++) {
            if (translatedTexts.containsKey(i)) continue;
            String originalText = getTextFromLine(i);
            if (originalText != null && originalText.startsWith("/")) {
                translatedTexts.put(i, "");
                continue;
            }
            if (originalText != null && OptionPanel.getKEY() != null && OptionPanel.getID() != null) {
                translatedTexts.put(i, "");
                translateTextAndAddToChat(i, I18n.getCurrentLanguage().equals(Language.ZH_CN) ? "zh" : "en");
                break;
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

    public void translateLanguage(int lineIndex, String text) {
        if (lineIndex < 0 || lineIndex >= chatArea.getChildren().size()) {
            LOGGER.warn("Invalid line index: {}", lineIndex);
            translatedTexts.remove(lineIndex);
            return;
        }
        String temp = getTextFromLine(lineIndex).replace("\n", "");

        if (Objects.equals(temp, text)) {
            return;
        }
        Text existingText = (Text) chatArea.getChildren().get(lineIndex);
        existingText.setFill(Color.GRAY);

        existingText.setText(existingText.getText().replace("\n", ""));
        existingText.setText(existingText.getText() + "   " + text + '\n');
        translatedTexts.put(lineIndex, text);
        chatScrollPane.setVvalue(1.0);
    }

    public String getTextFromLine(int lineIndex) {
        if (lineIndex < 0 || lineIndex >= chatArea.getChildren().size()) {
            LOGGER.warn("Invalid line index: {}", lineIndex);
            return "Error: Invalid line index.";
        }
        Text textNode = (Text) chatArea.getChildren().get(lineIndex);
        String node = textNode.getText();
        if (node.contains(": ")) {
            String[] str = node.split(": ", 2);
            return str[1];
        }
        return textNode.getText();
    }

    public void translateTextAndAddToChat(int lineIndex, String targetLang) {
        String originalText = getTextFromLine(lineIndex);
        if (originalText.equals("Error: Invalid line index.")) {
            translatedTexts.remove(lineIndex);
            return;
        }

        if (OptionPanel.getKEY() != null && OptionPanel.getID() != null) {
            CompletableFuture<String> futureResponse = BaiduTranslationApi.sendTranslationRequestAsync(OptionPanel.getKEY(), OptionPanel.getID(), Objects.equals(targetLang, "zh") ? "en" : "zh", targetLang, originalText);
            futureResponse.thenAccept(response -> {
                String translatedText = extractTranslatedText(response);
                if (translatedText != null) {
                    translateLanguage(lineIndex, translatedText);
                }
            });
        }
    }

    private String extractTranslatedText(String response) {
        if (response.contains("trans_result")) {
            int startIndex = response.indexOf("\"dst\":\"") + 7;
            int endIndex = response.indexOf("\"", startIndex);
            if (endIndex != -1) {
                return response.substring(startIndex, endIndex);
            }
        }
        return null;
    }


}
