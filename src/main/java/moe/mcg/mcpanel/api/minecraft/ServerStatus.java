package moe.mcg.mcpanel.api.minecraft;

/**
 *
 * @param time 运行时间
 * @param tick 该运行时间的tick值
 */
public record ServerStatus(String time, float tick) {
}
