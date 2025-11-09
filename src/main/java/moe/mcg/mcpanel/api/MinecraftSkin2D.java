package moe.mcg.mcpanel.api;

import javafx.scene.Group;
import javafx.scene.image.*;

public class MinecraftSkin2D extends Group {
    public static final FaceUV HEAD_FRONT = new FaceUV("HEAD_FRONT", 8, 8, 8, 8);
    public static final FaceUV HAT_FRONT = new FaceUV("HAT_FRONT", 40, 8, 8, 8);
    private static final float SCALE = 5.0f;
    private static final float OVERLAY_SCALE = SCALE * 9.0f / 8.0f;

    public MinecraftSkin2D(Image skin) {

        ImageView headFront = createFace(skin, HEAD_FRONT, SCALE);
        getChildren().add(headFront);

        if (!HAT_FRONT.isEmpty()) {
            ImageView hatFront = createFace(skin, HAT_FRONT, OVERLAY_SCALE);
            getChildren().add(hatFront);
        }
    }

    private ImageView createFace(Image skin, FaceUV face, double scale) {
        Image faceImage = cropAndScale(skin, face, scale);
        ImageView view = new ImageView(faceImage);

        double offsetX = (face.w * scale - face.w * SCALE) / 2.0;
        double offsetY = (face.h * scale - face.h * SCALE) / 2.0;
        view.setTranslateX(-offsetX);
        view.setTranslateY(-offsetY);

        return view;
    }

    private Image cropAndScale(Image skin, FaceUV face, double scale) {
        PixelReader reader = skin.getPixelReader();
        int width = (int) Math.round(face.w * scale);
        int height = (int) Math.round(face.h * scale);

        WritableImage result = new WritableImage(width, height);
        PixelWriter writer = result.getPixelWriter();

        for (int y = 0; y < face.h; y++) {
            for (int x = 0; x < face.w; x++) {
                int argb = reader.getArgb((int) face.u + x, (int) face.v + y);
                int px = (int) Math.round(x * scale);
                int py = (int) Math.round(y * scale);
                int nextPx = (int) Math.round((x + 1) * scale);
                int nextPy = (int) Math.round((y + 1) * scale);

                for (int dy = py; dy < nextPy; dy++) {
                    for (int dx = px; dx < nextPx; dx++) {
                        writer.setArgb(dx, dy, argb);
                    }
                }
            }
        }

        return result;
    }

    public record FaceUV(String name, double u, double v, double w, double h) {
        public FaceUV(String name) {
            this(name, 0, 0, 0, 0);
        }

        public boolean isEmpty() {
            return w == 0 || h == 0;
        }
    }
}
