package moe.mcg.mcpanel.api.pack;

import lombok.Getter;
import lombok.Setter;
import moe.mcg.mcpanel.api.minecraft.SimpleServerPlayer;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SimpleServerPlayerList {
    private List<SimpleServerPlayer> playerList = new ArrayList<>();
}
