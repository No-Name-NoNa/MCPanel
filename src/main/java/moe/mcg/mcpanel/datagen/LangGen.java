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
        addTranslation("app.alert", "Exit Confirmation", "确认退出");
        addTranslation("app.alert.content", "Are you sure you want to exit MCPanel?", "你确定要退出 MCPanel 吗？");


        addTranslation("main.menu.server_info", "Server Info", "服务器信息");
        addTranslation("main.menu.mod_list", "Mod List", "模组列表");
        addTranslation("main.menu.player_list", "Players", "玩家列表");
        addTranslation("main.menu.server_status", "Server Status", "服务器状态");

        addTranslation("main.info.server_name", "Server Name", "服务器名称");
        addTranslation("main.info.server_intro", "Description", "服务器简介");
        addTranslation("main.info.version", "Version", "版本");
        addTranslation("main.info.player_count", "Players", "玩家数量");
        addTranslation("main.info.mod_list", "Installed Mods", "已安装模组");
        addTranslation("main.info.player_list", "Online Players", "在线玩家");
        addTranslation("main.info.server_status", "Server Status", "服务器状态");
        addTranslation("main.info.no_mods", "No mods installed.", "没有安装任何模组。");
        addTranslation("main.info.not_implemented", "Not yet implemented.", "功能尚未实现。");
        addTranslation("main.info.mod_id", "Mod ID", "模组 ID");
        addTranslation("main.info.mod_name", "Mod Name", "模组名称");
        addTranslation("main.info.mod_version", "Mod Version", "模组版本");
        addTranslation("main.info.mod_url", "Mod URL", "模组链接");

        addTranslation("main.player.no_player", "No Player right now", "当前无玩家");
        addTranslation("main.player.name", "Player Name", "玩家名称");
        addTranslation("main.player.uuid", "Player UUID", "玩家 UUID");
        addTranslation("main.player.ping", "Player Ping", "玩家 Ping");

        generate(new File("src/main/resources/assets/mcpanel/lang"));
    }
}