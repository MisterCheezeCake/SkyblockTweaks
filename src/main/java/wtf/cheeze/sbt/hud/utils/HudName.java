package wtf.cheeze.sbt.hud.utils;

import net.minecraft.text.Text;
import wtf.cheeze.sbt.utils.text.TextUtils;
import wtf.cheeze.sbt.utils.render.RenderUtils;

public record HudName(String pName, String sName, int color) {
    public HudName(String pName, int color) {
        this(pName, pName, color);
    }

    public Text primaryName() {
        return TextUtils.withColor(pName, color);

    }

    public Text shortName() {
        return TextUtils.withColor(sName, color);
    }

    public Text name(int width) {
        if (RenderUtils.getStringWidth(primaryName()) > width) {
            return shortName();
        } else {
            return primaryName();
        }
    }



}
