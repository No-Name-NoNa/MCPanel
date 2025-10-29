package moe.mcg.mcpanel.api.pack;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SimpleServerPlayer {
    private List<String> playerList = new ArrayList<>();
}
