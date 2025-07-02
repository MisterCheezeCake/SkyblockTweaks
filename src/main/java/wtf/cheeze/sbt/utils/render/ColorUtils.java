package wtf.cheeze.sbt.utils.render;

public class ColorUtils {
    private static final float[] PROGRESS_THRESHOLDS = {.25f, .5f, .75f};

    public static Color3f getColor3f(int color) {
        return new Color3f(color);
    }

    public static int fromFloatProgress(float f) {
        if (f < PROGRESS_THRESHOLDS[0]) return Colors.RED;
        if (f < PROGRESS_THRESHOLDS[1]) return Colors.ORANGE;
        if (f < PROGRESS_THRESHOLDS[2]) return Colors.YELLOW;
        return Colors.LIME;
    }
}
