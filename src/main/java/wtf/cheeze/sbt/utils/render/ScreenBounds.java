package wtf.cheeze.sbt.utils.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

public class ScreenBounds {
    public int width;
    public int height;

    ScreenBounds() {
        Window window = MinecraftClient.getInstance().getWindow();
        this.width = window.getScaledWidth();
        this.height = window.getScaledHeight();
    }
}
