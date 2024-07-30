package wtf.cheeze.sbt.utils.hud;

import net.minecraft.client.gui.DrawContext;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.utils.RenderUtils;

public abstract  class MultilineTextHUD extends HUD {

    public HudLine[] lines;

    public int getLongestLineWidth(){
        int longest = 0;
        for(var line : lines){
            int width = RenderUtils.getStringWidth(line.text.get());
            if (line.useIcon.get()) {
                width += 10;
            }
            if(width > longest){
                longest = width;
            }
        }
        return longest;
    }

    @Override
    public Bounds getCurrentBounds() {
        var scale = (float) INFO.getScale.get();
        return new Bounds(getActualX((float) INFO.getX.get()), getActualY((float) INFO.getY.get()), getLongestLineWidth() * scale, lines.length * SkyblockTweaks.mc.textRenderer.fontHeight * scale, scale);
    }
    @Override
    public BoundsRelative getCurrentBoundsRelative() {
        var scale = (float) INFO.getScale.get();
        return new BoundsRelative((float) INFO.getX.get(), (float) INFO.getY.get(), getLongestLineWidth() * scale, lines.length * SkyblockTweaks.mc.textRenderer.fontHeight * scale, scale);
    }


    @Override
    public void render(DrawContext context, boolean fromHudScreen, boolean hovered) {
        if (!shouldRender(fromHudScreen)) return;


        var bounds = getCurrentBounds();
        var lineHeight = (int) (9 * bounds.scale);
        if (fromHudScreen) {
            drawBackground(context, hovered ? BACKGROUND_HOVERED : BACKGROUND_NOT_HOVERED);
        }
        RenderUtils.beginScale(context, bounds.scale);
        for(int i = 0; i < lines.length; i++){
            var line = lines[i];
            var y = bounds.y + i * lineHeight;
            line.render(context, bounds.x, y, bounds.scale);
        }
        RenderUtils.endScale(context);


    }
}
