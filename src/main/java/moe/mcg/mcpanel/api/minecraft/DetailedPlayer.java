package moe.mcg.mcpanel.api.minecraft;

import lombok.Getter;
import lombok.Setter;
import moe.mcg.mcpanel.api.MinecraftSkin2D;
import moe.mcg.mcpanel.image.ApplicationImage;

@Getter
@Setter
public class DetailedPlayer {
    private String username;
    private String uuid;
    private String ping;
    private MinecraftSkin2D icon;
    private GameMode gamemode;
    private SimpleVec3 location;
    private String dimension;
    private String health;
    private String hungry;
    private String permission;

    public DetailedPlayer(String username, String uuid, String ping, MinecraftSkin2D icon, GameMode gamemode, SimpleVec3 location, String dimension, String health, String hungry, String permission) {
        this.username = username;
        this.uuid = uuid;
        this.ping = ping;
        this.icon = icon;
        this.gamemode = gamemode;
        this.location = location;
        this.dimension = dimension;
        this.health = health;
        this.hungry = hungry;
        this.permission = permission;
    }

    public DetailedPlayer(String username, String uuid, MinecraftSkin2D icon) {
        this.username = "";
        this.uuid = "";
        this.icon = new MinecraftSkin2D(ApplicationImage.INSTANCE.getResource("steve.png"));
        this.gamemode = GameMode.SURVIVAL;
        this.location = new SimpleVec3(0, 0, 0);
        this.dimension = "";
        this.health = "20";
        this.hungry = "20";
        this.permission = "0";
        this.ping = "0";
    }
}
