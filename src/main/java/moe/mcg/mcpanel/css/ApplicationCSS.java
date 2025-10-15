package moe.mcg.mcpanel.css;

import moe.mcg.mcpanel.api.IResource;

import java.net.URL;
import java.util.Objects;

public class ApplicationCSS implements IResource<String> {
    public static final ApplicationCSS INSTANCE = new ApplicationCSS();
    private static final String ROOT_PATH = "/assets/mcpanel/css/";

    private ApplicationCSS() {
    }

    @Override
    public String getResource(String fileName) {
        URL url = ApplicationCSS.class.getResource(ROOT_PATH + fileName);
        return Objects.requireNonNull(url, "CSS not found: " + ROOT_PATH + fileName).toExternalForm();
    }
}
