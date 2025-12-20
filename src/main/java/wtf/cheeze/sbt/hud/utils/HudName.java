package wtf.cheeze.sbt.hud.utils;

import net.minecraft.network.chat.Component;
import wtf.cheeze.sbt.utils.render.RenderUtils;
import wtf.cheeze.sbt.utils.text.TextUtils;

public record HudName(String pName, String sName, int color) {
    public HudName(String pName, int color) {
        this(pName, pName, color);
    }

    public Component primaryName() {
        return TextUtils.withColor(pName, color);
    }

    public Component shortName() {
        return TextUtils.withColor(sName, color);
    }

    public Component name(int width) {
        if (RenderUtils.getStringWidth(primaryName()) > width) {
            return shortName();
        } else {
            return primaryName();
        }
    }
}
