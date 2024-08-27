package wtf.cheeze.sbt.utils.render;

public class Color3f {

    public float red;
    public float green;
    public float blue;

    Color3f (int color) {
        this.red = (float) (color >> 16 & 255) / 255.0F;
        this.green = (float) (color >> 8 & 255) / 255.0F;
        this.blue = (float) (color & 255) / 255.0F;
    }
}
