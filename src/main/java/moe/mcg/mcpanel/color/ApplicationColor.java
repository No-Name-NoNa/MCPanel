package moe.mcg.mcpanel.color;

import javafx.scene.paint.Color;

public class ApplicationColor {
    public static final Color BACKGROUND = rgb(235, 235, 235);
    public static final Color TITLE_BACKGROUND = rgb(60, 63, 65);

    public static Color rgb(int r, int g, int b) {
        return Color.rgb(r, g, b);
    }

    public static Color rgba(int r, int g, int b, double alpha) {
        return Color.rgb(r, g, b, alpha);
    }

}
