package moe.mcg.mcpanel.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LangGen {

    private static final Map<String, Map<String, String>> LANGUAGE = new HashMap<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void addTranslation(String key, String en, String zh) {
        LANGUAGE.computeIfAbsent("en_us", k -> new HashMap<>()).put(key, en);
        LANGUAGE.computeIfAbsent("zh_cn", k -> new HashMap<>()).put(key, zh);
    }

    @SuppressWarnings("all")
    public static void generate(File outputDir) {
        if (!outputDir.exists()) outputDir.mkdirs();
        LANGUAGE.forEach((lang, map) -> {
            File outFile = new File(outputDir, lang + ".json");
            try (FileWriter writer = new FileWriter(outFile)) {
                GSON.toJson(map, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        System.out.println("Language files generated in " + outputDir.getAbsolutePath());
    }

    public static void main(String[] args) {
        addTranslation("app.name", "Minecraft Server Panel", "我的世界服务器管理面板");
        addTranslation("login.label.ip", "IP:", "IP：");
        addTranslation("login.label.port", "Port:", "端口：");
        addTranslation("login.prompt.ip", "127.0.0.1", "127.0.0.1");
        addTranslation("login.prompt.port", "25565", "25565");
        addTranslation("login.label.access_key", "Access Key:", "访问密钥：");
        addTranslation("login.button.login", "Connect to Server", "连接到服务器");
        addTranslation("login.error.empty_access_key", "Access key cannot be empty!", "访问密钥不能为空！");
        addTranslation("login.prompt.ip_port", "127.0.0.1:25565", "127.0.0.1:25565");
        addTranslation("login.prompt.access_key", "Your access key", "你的访问密钥");
        addTranslation("connect.error.empty_ip", "IP cannot be empty!", "IP不能为空！");
        addTranslation("connect.error.empty_port", "Port cannot be empty!", "端口不能为空！");
        addTranslation("connect.error.invalid_port", "Invalid port number!", "无效的端口号！");
        addTranslation("main.connected", "Connected to Server", "已连接到服务器");
        addTranslation("main.servername", "Server Name:", "服务器名称：");
        addTranslation("main.motd", "MOTD:", "服务器简介：");
        addTranslation("main.version", "Version:", "版本：");
        addTranslation("main.players", "Players", "玩家列表");
        addTranslation("app.alert", "Exit Confirmation", "确认退出");
        addTranslation("app.alert.content", "Are you sure you want to exit MCPanel?", "你确定要退出 MCPanel 吗？");

        generate(new File("src/main/resources/assets/mcpanel/lang"));
    }
}