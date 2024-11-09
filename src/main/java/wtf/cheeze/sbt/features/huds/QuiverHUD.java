package wtf.cheeze.sbt.features.huds;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.hud.bases.TextHUD;
import wtf.cheeze.sbt.hud.components.SingleHudLine;
import wtf.cheeze.sbt.hud.utils.AnchorPoint;
import wtf.cheeze.sbt.hud.utils.DrawMode;
import wtf.cheeze.sbt.hud.utils.HudInformation;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.skyblock.IconDict;
import wtf.cheeze.sbt.utils.skyblock.SkyblockUtils;

import java.awt.*;

public class QuiverHUD extends TextHUD {
    @Override
    public String getName() {
        return "Quiver HUD";
    }


    public QuiverHUD() {
        INFO = new HudInformation(
                () -> SBTConfig.huds().quiver.x,
                () -> SBTConfig.huds().quiver.y,
                () -> SBTConfig.huds().quiver.scale,
                () -> SBTConfig.huds().quiver.anchor,
                x -> SBTConfig.huds().quiver.x = (float) x,
                y -> SBTConfig.huds().quiver.y = (float) y,
                scale -> SBTConfig.huds().quiver.scale = (float) scale,
                anchor -> SBTConfig.huds().quiver.anchor = anchor
        );
        line = new SingleHudLine(
                () -> Colors.WHITE,
                () -> SBTConfig.huds().quiver.outlineColor,
                () -> SBTConfig.huds().quiver.mode,
                () -> {
                    var quiver = SkyblockUtils.getQuiverData();
                    return TextUtils.join(
                            quiver.arrowName,
                            TextUtils.SPACE,
                            TextUtils.withColor("x" + quiver.arrowCount, Colors.GRAY)
                    );
                },
                () -> IconDict.ARROW,
                () -> SBTConfig.huds().quiver.icon
        );

    }

    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        if ((SkyblockTweaks.DATA.inSB && SBTConfig.huds().quiver.enabled && SkyblockUtils.quiverActive()) || fromHudScreen) return true;
        return false;
    }


    public static class Config {
        @SerialEntry
        public boolean enabled = false;

        @SerialEntry
        public DrawMode mode = DrawMode.SHADOW;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.9f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public int outlineColor = Colors.BLACK;

        @SerialEntry
        public AnchorPoint anchor = AnchorPoint.LEFT;

        @SerialEntry
        public boolean icon = true;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(key("quiver.enabled"))
                    .description(keyD("quiver.enabled"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.quiver.enabled,
                            () -> config.huds.quiver.enabled,
                            value -> config.huds.quiver.enabled = (Boolean) value
                    )
                    .build();
            var icon = Option.<Boolean>createBuilder()
                    .name(key("quiver.icon"))
                    .description(keyD("quiver.icon"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.quiver.icon,
                            () -> config.huds.quiver.icon,
                            value -> config.huds.quiver.icon = (Boolean) value
                    )
                    .build();



            var outline = Option.<Color>createBuilder()
                    .name(key("quiver.outlineColor"))
                    .description(keyD("quiver.outlineColor"))
                    .controller(ColorControllerBuilder::create)
                    .available(config.huds.quiver.mode == DrawMode.OUTLINE)
                    .binding(
                            new Color(defaults.huds.quiver.outlineColor),
                            () ->  new Color(config.huds.quiver.outlineColor),
                            value -> config.huds.quiver.outlineColor = value.getRGB()

                    )
                    .build();

            var mode = Option.<DrawMode>createBuilder()
                    .name(key("quiver.mode"))
                    .description(keyD("quiver.mode"))
                    .controller(SBTConfig::generateDrawModeController)
                    .binding(
                            defaults.huds.quiver.mode,
                            () -> config.huds.quiver.mode,
                            value -> {
                                config.huds.quiver.mode = value;
                                if (value == DrawMode.OUTLINE) outline.setAvailable(true);
                                else outline.setAvailable(false);
                            }
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(key("quiver"))
                    .description(keyD("quiver"))
                    .option(enabled)
                    .option(icon)
                    .option(outline)
                    .option(mode)
                    .collapsed(true)
                    .build();
        }
    }

}
