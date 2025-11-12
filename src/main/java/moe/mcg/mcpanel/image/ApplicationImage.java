package moe.mcg.mcpanel.image;

import javafx.scene.image.Image;
import moe.mcg.mcpanel.api.IResource;

import java.util.Objects;

public class ApplicationImage implements IResource<Image> {
    public static final ApplicationImage INSTANCE = new ApplicationImage();
    private static final String ROOT_PATH = "/assets/mcpanel/img/";

    private ApplicationImage() {
    }

    @Override
    public Image getResource(String path) {
        return new Image(Objects.requireNonNull(ApplicationImage.class.getResource(ROOT_PATH + path)).toExternalForm());
    }
}
