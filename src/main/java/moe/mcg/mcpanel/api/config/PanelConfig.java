package moe.mcg.mcpanel.api.config;

import java.util.List;

public class PanelConfig {

    public final String serverName;
    public final String serverIntro;
    public final String serverVersion;
    public List<String> players;

    public PanelConfig(String serverName, String serverIntro, String serverVersion) {
        this.serverName = serverName;
        this.serverIntro = serverIntro;
        this.serverVersion = serverVersion;
    }

}
