package moe.mcg.mcpanel.api.minecraft;

/**
 * 不带图片的详细玩家数据
 */
public record ServerPlayer(String name,
                           String uuid,
                           SimpleVec3 location,
                           String dimension,
                           GameMode gamemode,
                           float health,
                           int food,
                           int ping,
                           int permissionLevel) {
}
