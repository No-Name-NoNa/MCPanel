package moe.mcg.mcpanel.api.minecraft;

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
