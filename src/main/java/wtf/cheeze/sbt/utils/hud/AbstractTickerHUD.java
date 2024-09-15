package wtf.cheeze.sbt.utils.hud;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.utils.render.RenderUtils;

/**
 * The abstract representation of the ticker HUD, for the implementation, see {@link wtf.cheeze.sbt.features.huds.TickerHUD}
 */
public abstract class AbstractTickerHUD extends HUD{
    private static final Identifier FULL = Identifier.of("skyblocktweaks", "ticker_full.png");
    private static final Identifier BLANK = Identifier.of("skyblocktweaks", "ticker_blank.png");
    private static final int DIMENSION = 9;

    public abstract int getMax(boolean fromHudScreen);
    public abstract int getUsable(boolean fromHudScreen);
    public abstract int getMax();


    @Override
    public void render(DrawContext context, boolean fromHudScreen, boolean hovered) {
        if (!shouldRender(fromHudScreen)) return;
        var bounds = getCurrentBounds();
        if (fromHudScreen) {
            drawBackground(context, hovered ? BACKGROUND_HOVERED : BACKGROUND_NOT_HOVERED);
        }
        if (bounds.scale != 1.0f) {
            RenderUtils.beginScale(context, bounds.scale);
        }
        var x2 = drawTickers(context, getUsable(fromHudScreen), bounds.x, bounds.y, bounds.scale, true);
        drawTickers(context, getMax(fromHudScreen) - getUsable(fromHudScreen), x2, bounds.y, bounds.scale, false);
        if (bounds.scale != 1.0f) {
            RenderUtils.endScale(context);
        }

    }

    private float drawTickers(DrawContext context, int number, float x, int y, float scale, boolean filled) {
        float drawX = x;
        for (int i = 0; i < number; i++) {
            context.drawTexture(filled ? FULL : BLANK, (int) (drawX / scale), (int) (y / scale), 0, 0, DIMENSION, DIMENSION, DIMENSION , DIMENSION);
            drawX = (2 + drawX + (DIMENSION * scale));
        }
        return drawX;
    }

    private int getWidth() {
        return DIMENSION * getMax() + 2 * getMax();
    }

    private int getRelativeWidth() {
        return getWidth() / SkyblockTweaks.mc.getWindow().getWidth();
    }

    @Override
    public Bounds getCurrentBounds() {
        var scale = (float) INFO.getScale.get();
        switch (INFO.getAnchorPoint.get()) {
            case LEFT -> {
                return new Bounds(getActualX((float) INFO.getX.get()), getActualY((float) INFO.getY.get()), getWidth() * scale, DIMENSION * scale, scale);
            }
            case RIGHT -> {
                return new Bounds((int) (getActualX((float) INFO.getX.get()) - getWidth() * scale), getActualY((float) INFO.getY.get()), getWidth() * scale, DIMENSION * scale, scale);
            }
            case CENTER -> {
                return new Bounds((int) (getActualX((float) INFO.getX.get()) - getWidth() * scale / 2), getActualY((float) INFO.getY.get()), getWidth() * scale, DIMENSION * scale, scale);
            }
            default -> throw new IllegalStateException("Unexpected value: " + INFO.getAnchorPoint.get());
        }
    }
    @Override
    public BoundsRelative getCurrentBoundsRelative() {
        var scale = (float) INFO.getScale.get();
        switch (INFO.getAnchorPoint.get()) {
            case LEFT -> {
                return new BoundsRelative((float) INFO.getX.get(), (float) INFO.getY.get(), getWidth() * scale, DIMENSION * scale, scale);
            }
            case RIGHT -> {
                return new BoundsRelative((float) INFO.getX.get() - getRelativeWidth() * scale, (float) INFO.getY.get(), getWidth() * scale, DIMENSION * scale, scale);
            }
            case CENTER -> {
                return new BoundsRelative((float) INFO.getX.get() - getRelativeWidth() * scale / 2, (float) INFO.getY.get(), getWidth() * scale, DIMENSION * scale, scale);
            }
            default -> throw new IllegalStateException("Unexpected value: " + INFO.getAnchorPoint.get());
        }
    }



}
