package moe.mcg.mcpanel.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * 语言文件生成器。
 * <p>
 * 该类用于管理并生成 Minecraft 服务器面板的语言文件。支持添加不同语言（如英语、中文）的翻译，并将翻译数据
 * 生成到指定目录下的 JSON 文件中。生成的 JSON 文件符合标准的语言文件格式，可以用于界面文本的国际化处理。
 * </p>
 * <p>
 * 主要功能包括：
 * <ul>
 *     <li>通过 {@link #addTranslation(String, String, String)} 方法添加不同语言的翻译。</li>
 *     <li>通过 {@link #generate(File)} 方法生成包含所有翻译内容的 JSON 文件。</li>
 * </ul>
 * </p>
 * <p>
 * 默认生成的语言包括英语（"en_us"）和简体中文（"zh_cn"），可以通过 {@link #addTranslation(String, String, String)}
 * 方法动态添加更多翻译项。
 * </p>
 */
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

        addTranslation("login.alert.connection_error", "Connection Error", "连接错误");
        addTranslation("login.alert.validation_error", "Validation Error", "验证错误");
        addTranslation("login.alert.connection_error.title", "Failed to Connect", "连接失败");
        addTranslation("login.alert.connection_error.header", "Unable to establish connection.", "无法建立连接");
        addTranslation("login.alert.validation_error.header", "Invalid Access Key", "无效的访问密钥");
        addTranslation("login.alert.validation_error.message", "Please check your access key and try again.", "请检查您的访问密钥并重试");
        addTranslation("login.alert.connecting", "Connecting...", "正在连接...");
        addTranslation("login.alert.connecting.cancel", "Cancel", "取消连接");
        addTranslation("login.alert.connecting_to_server", "Connecting to server...", "正在连接到服务器...");

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
        addTranslation("main.menu.chat", "Server Chat", "服务器聊天栏");
        addTranslation("main.menu.option", "Option", "设置");

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
        addTranslation("main.player.username", "Player Username:", "玩家名称：");
        addTranslation("main.player.uuid", "Player UUID:", "玩家 UUID：");
        addTranslation("main.player.ping", "Player Ping:", "玩家 Ping：");
        addTranslation("main.player.location", "Player Position:", "玩家坐标：");
        addTranslation("main.player.dimension", "Player Dimension:", "玩家维度：");
        addTranslation("main.player.health", "Player Health:", "玩家生命值：");
        addTranslation("main.player.food", "Player Food:", "玩家饥饿度：");
        addTranslation("main.player.return", "Return", "返回");
        addTranslation("main.player.permission", "Permission Level:", "权限等级：");
        addTranslation("main.player.joined", "has joined the game", "加入了游戏");
        addTranslation("main.player.left", "has left the game", "离开了游戏");

        addTranslation("main.chat.send", "Send Message", "发送消息");
        addTranslation("main.chat.word", "Enter Message...", "输入消息...");

        addTranslation("main.status.x", "Time (Minutes)", "时间(分钟)");
        addTranslation("main.status.y", "tps", "tps");
        addTranslation("main.status.average", "Average TPS:", "平均 tps：");
        addTranslation("main.status.title", "Server Status", "服务器状态");

        addTranslation("main.option.language", "Language", "语言");
        addTranslation("main.option.apply", "Apply", "应用");
        addTranslation("main.option.apikey", "Baidu API KEY", "百度API KEY");
        addTranslation("main.option.appid", "Baidu APP ID", "百度APP ID");
        addTranslation("main.option.save", "Save", "保存");
        addTranslation("main.option.enabled", "Enabled Translation", "已启用翻译");
        addTranslation("main.option.disabled", "Disabled Translation", "已停用翻译");

        generate(new File("src/main/resources/assets/mcpanel/lang"));
    }
}