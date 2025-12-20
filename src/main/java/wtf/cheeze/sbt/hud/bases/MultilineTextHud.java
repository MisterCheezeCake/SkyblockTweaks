package wtf.cheeze.sbt.hud.bases;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;
import wtf.cheeze.sbt.hud.bounds.Bounds;
import wtf.cheeze.sbt.hud.bounds.BoundsRelative;
import wtf.cheeze.sbt.hud.HUD;
import wtf.cheeze.sbt.hud.components.HudComponent;
import wtf.cheeze.sbt.utils.render.RenderUtils;

public abstract class MultilineTextHud extends HUD {
    private static final Minecraft mc = Minecraft.getInstance();

    protected MultilineTextHud() {
        this.supportsNonLeftAnchors = false;
    }
    public HudComponent[] lines;

    public int getLongestLineWidth(boolean relative) {
        int longest = 0;
        for(var line : lines){
           int width = line.getWidth();
           if (width > longest) longest = width;
        }
        return longest;
    }
    public int getLongestLineWidth(){
        return getLongestLineWidth(false);
    }
    private int getLineNo(){
        int i = 0;
        for(var line : lines){
            i+=line.getlines();
        }
        return i;
    }

    @Override
    public @NotNull Bounds getCurrentBounds() {
        var scale = (float) INFO.getScale.get();
        var w = getLongestLineWidth();
        var h = getLineNo();
        return new Bounds(getActualX(INFO.getX.get()), getActualY(INFO.getY.get()), w * scale, h * mc.font.lineHeight * scale, scale);
    }

    @Override
    public @NotNull BoundsRelative getCurrentBoundsRelative() {
        var scale = (float) INFO.getScale.get();
        var w = getLongestLineWidth();
        var h = getLineNo();
        return new BoundsRelative(INFO.getX.get(), INFO.getY.get(), w * scale, h * mc.font.lineHeight * scale, scale);
    }

    @Override
    public void render(GuiGraphics guiGraphics, boolean fromHudScreen, boolean hovered) {
        if (!shouldRender(fromHudScreen)) return;

        var bounds = getCurrentBounds();
        var lineHeight = (int) (9 * bounds.scale);
        if (fromHudScreen) {
            drawBackground(guiGraphics, hovered ? BACKGROUND_HOVERED : BACKGROUND_NOT_HOVERED);
        }
        RenderUtils.beginScale(guiGraphics, bounds.scale);
//
//        for(int i = 0; i < lines.length; i++){
//            var line = lines[i];
//            var y = bounds.y + i * lineHeight;
//            line.render(context,  bounds.x, y, bounds.scale);
//        }
        int i = 0;
        for (var line : lines) {
            var y = bounds.y + lineHeight * i;
            i+=line.render(guiGraphics, bounds.x, y, bounds.scale);
           //i+=line.render(context, bounds.x, y, bounds.scale);
        }
        RenderUtils.popMatrix(guiGraphics);
    }

//    private int getXPosition(HudComponent line, int hudX) {
//        var anchor = INFO.getAnchorPoint.get();
//        var width = line.getWidth();
//        return switch (anchor) {
//            case LEFT -> hudX;
//            case RIGHT -> hudX - width;
//            case CENTER -> hudX - width / 2;
//
//        };
//    }
//    private int fixBoundsX(int x, float width){
//        var anchor = INFO.getAnchorPoint.get();
//        return switch (anchor) {
//            case LEFT -> x;
//            case RIGHT ->  (int) (x - width);
//            case CENTER -> (int) (x - width / 2);
//
//        };
//    }
//    private float fixBoundsXRelative(float x, float width){
//        var anchor = INFO.getAnchorPoint.get();
//        return switch (anchor) {
//            case LEFT -> x;
//            case RIGHT -> x - width;
//            case CENTER -> x - width / 2;
//
//        };
//    }
}
