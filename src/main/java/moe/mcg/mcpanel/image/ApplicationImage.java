package moe.mcg.mcpanel.image;

import javafx.scene.image.Image;

import java.util.Objects;

public class ApplicationImage {
    public static Image findImage(String img) {
        return new Image(Objects.requireNonNull(ApplicationImage.class.getResource("/assets/mcpanel/img/" + img)).toExternalForm());
    }
}
