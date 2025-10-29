package moe.mcg.mcpanel.api.pack;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ModInfo {
    private final String modId;
    private final String modName;
    private final String modVersion;
    private final String modUrl;
}