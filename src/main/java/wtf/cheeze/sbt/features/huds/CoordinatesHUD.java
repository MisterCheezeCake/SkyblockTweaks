package wtf.cheeze.sbt.features.huds;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SkyblockTweaksConfig;
import wtf.cheeze.sbt.utils.NumberUtils;
import wtf.cheeze.sbt.utils.hud.HudLine;
import wtf.cheeze.sbt.utils.hud.HudInformation;
import wtf.cheeze.sbt.utils.hud.TextHUD;

import java.awt.Color;

public class CoordinatesHUD extends TextHUD {

    public CoordinatesHUD() {
        INFO = new HudInformation(
                () -> SkyblockTweaks.CONFIG.config.huds.coordinates.x,
                () -> SkyblockTweaks.CONFIG.config.huds.coordinates.y,
                () -> SkyblockTweaks.CONFIG.config.huds.coordinates.scale,
                () -> SkyblockTweaks.CONFIG.config.huds.coordinates.anchor,
                x -> SkyblockTweaks.CONFIG.config.huds.coordinates.x = (float) x,
                y -> SkyblockTweaks.CONFIG.config.huds.coordinates.y = (float) y,
                scale -> SkyblockTweaks.CONFIG.config.huds.coordinates.scale = (float) scale,
                anchor -> SkyblockTweaks.CONFIG.config.huds.coordinates.anchor = anchor
        );
        line = new HudLine(
                () -> SkyblockTweaks.CONFIG.config.huds.coordinates.color,
                () -> SkyblockTweaks.CONFIG.config.huds.coordinates.outlineColor,
                () -> SkyblockTweaks.CONFIG.config.huds.coordinates.mode,
                () ->  String.format("X: %s Y: %s Z: %s" , NumberUtils.formattedRound(SkyblockTweaks.mc.player.getX(), SkyblockTweaks.CONFIG.config.huds.coordinates.decimalPlaces), NumberUtils.formattedRound(SkyblockTweaks.mc.player.getY(), SkyblockTweaks.CONFIG.config.huds.coordinates.decimalPlaces), NumberUtils.formattedRound(SkyblockTweaks.mc.player.getZ(), SkyblockTweaks.CONFIG.config.huds.coordinates.decimalPlaces))
        );
    }
    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        if (((SkyblockTweaks.DATA.inSB  || SkyblockTweaks.CONFIG.config.huds.coordinates.showOutside) && SkyblockTweaks.CONFIG.config.huds.coordinates.enabled) || fromHudScreen) return true;
        return false;
    }

    @Override
    public String getName() {
        return "Coordinates HUD";
    }

    public static class Config {
        @SerialEntry
        public boolean enabled = false;

        @SerialEntry
        public boolean showOutside = false;

        @SerialEntry
        public int decimalPlaces = 0;

        @SerialEntry
        public HudLine.DrawMode mode = HudLine.DrawMode.SHADOW;

        @SerialEntry
        public int color = 0xFFFFFF;

        @SerialEntry
        public int outlineColor = 0x000000;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.60f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public AnchorPoint anchor = AnchorPoint.LEFT;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(Text.literal("Enable Coordinates HUD"))
                    .description(OptionDescription.of(Text.literal("Enables the Coordinates HUD")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.coordinates.enabled,
                            () -> config.huds.coordinates.enabled,
                            value -> config.huds.coordinates.enabled = (Boolean) value
                    )
                    .build();

            var outside = Option.<Boolean>createBuilder()
                    .name(Text.literal("Show outside of Skyblock"))
                    .description(OptionDescription.of(Text.literal("Whether to show the Coordinates HUD outside of Skyblock")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.coordinates.showOutside,
                            () -> config.huds.coordinates.showOutside,
                            value -> config.huds.coordinates.showOutside = (Boolean) value
                    )
                    .build();
            var decimalPlaces = Option.<Integer>createBuilder()
                    .name(Text.literal("Decimal Places"))
                    .description(OptionDescription.of(Text.literal("The number of decimal places to show in the Coordinates HUD")))
                    .controller(
                            opt -> IntegerSliderControllerBuilder.create(opt)
                                    .step(1)
                                    .range(0, 4)

                    )
                    .binding(
                            defaults.huds.coordinates.decimalPlaces,
                            () -> config.huds.coordinates.decimalPlaces,
                            value -> config.huds.coordinates.decimalPlaces = (Integer) value
                    )
                    .build();

            var color = Option.<Color>createBuilder()
                    .name(Text.literal("Coordinates HUD Color"))
                    .description(OptionDescription.of(Text.literal("The color of the Coordinates HUD")))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.coordinates.color),
                            () ->  new Color(config.huds.coordinates.color),
                            value -> config.huds.coordinates.color = value.getRGB()

                    )
                    .build();
            var outline = Option.<Color>createBuilder()
                    .name(Text.literal("Coordinates HUD Outline Color"))
                    .description(OptionDescription.of(Text.literal("The outline color of the Coordinates HUD")))
                    .controller(ColorControllerBuilder::create)
                    .available(config.huds.coordinates.mode == HudLine.DrawMode.OUTLINE)
                    .binding(
                            new Color(defaults.huds.coordinates.outlineColor),
                            () ->  new Color(config.huds.coordinates.outlineColor),
                            value -> config.huds.coordinates.outlineColor = value.getRGB()

                    )
                    .build();
            var mode = Option.<HudLine.DrawMode>createBuilder()
                    .name(Text.literal("Coordinates HUD Mode"))
                    .description(OptionDescription.of(Text.literal("The draw mode of the Coordinates HUD. Pure will render without shadow, Shadow will render with a shadow, and Outline will render with an outline\n§4Warning: §cOutline mode is still a work in progress and can cause annoying visual bugs in menus.")))
                    .controller(SkyblockTweaksConfig::generateDrawModeController)
                    .binding(
                            defaults.huds.coordinates.mode,
                            () -> config.huds.coordinates.mode,
                            value -> {
                                config.huds.coordinates.mode = value;
                                if (value == HudLine.DrawMode.OUTLINE) outline.setAvailable(true);
                                else outline.setAvailable(false);
                            }
                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(Text.literal("Coordinates HUD Scale"))
                    .description(OptionDescription.of(Text.literal("The scale of the Coordinates HUD")))
                    .controller(SkyblockTweaksConfig::generateScaleController)
                    .binding(
                            defaults.huds.coordinates.scale,
                            () -> config.huds.coordinates.scale,
                            value -> config.huds.coordinates.scale = value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(Text.literal("Coordinates HUD"))
                    .description(OptionDescription.of(Text.literal("Settings for the Coordinates HUD")))
                    .option(enabled)
                    .option(outside)
                    .option(decimalPlaces)
                    .option(mode)
                    .option(color)
                    .option(outline)
                    .option(scale)
                    .collapsed(true)
                    .build();
        }
    }
}
