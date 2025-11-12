package moe.mcg.mcpanel.api;

import lombok.Getter;

/**
 * 定义了面板中不同状态的枚举值。
 * <p>
 * 该枚举类用于表示 Minecraft 服务器面板的不同状态，每个状态代表面板的某个视图或操作模式。
 * </p>
 * <p>
 * 枚举值包括：
 * <ul>
 *     <li>{@link Status#INFO}：显示服务器信息。</li>
 *     <li>{@link Status#MODS}：显示已安装的模组列表。</li>
 *     <li>{@link Status#PLAYERS}：显示在线玩家列表。</li>
 *     <li>{@link Status#DETAILED_PLAYER}：显示详细的玩家信息。</li>
 *     <li>{@link Status#STATUS}：显示服务器状态。</li>
 *     <li>{@link Status#STATUS_CONTINUE}：继续显示服务器状态。</li>
 *     <li>{@link Status#CHAT}：显示服务器聊天栏。</li>
 *     <li>{@link Status#CHAT_CONTINUE}：继续显示聊天内容。</li>
 *     <li>{@link Status#PING}：显示服务器的响应时间。</li>
 *     <li>{@link Status#OPTION}：显示面板设置选项。</li>
 * </ul>
 * </p>
 * <p>
 * 此外，部分枚举值还包含用户名的设置，支持通过 {@link Status#setUsername(String)} 方法为状态设置一个特定的用户名。
 * </p>
 */
public enum Status {
    INFO,
    MODS,
    PLAYERS,
    DETAILED_PLAYER("steve"),
    STATUS,
    STATUS_CONTINUE,
    CHAT,
    CHAT_CONTINUE,
    PING,
    OPTION;

    @Getter
    private String username;

    Status() {
    }

    Status(String username) {
        this.username = "status";
    }

    public Status setUsername(String username) {
        this.username = username;
        return this;
    }
}
