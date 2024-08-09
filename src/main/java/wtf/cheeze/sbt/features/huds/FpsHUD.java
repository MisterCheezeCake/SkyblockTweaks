/*
 * Copyright (C) 2024 MisterCheezeCake
 *
 * This file is part of SkyblockTweaks.
 *
 * SkyblockTweaks is free software: you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * SkyblockTweaks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with SkyblockTweaks. If not, see <https://www.gnu.org/licenses/>.
 */
package wtf.cheeze.sbt.features.huds;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SkyblockTweaksConfig;
import wtf.cheeze.sbt.utils.hud.HudLine;
import wtf.cheeze.sbt.utils.hud.HudInformation;
import wtf.cheeze.sbt.utils.hud.TextHUD;

import java.awt.Color;

public class FpsHUD extends TextHUD {

    public FpsHUD() {
        INFO = new HudInformation(
                () -> SkyblockTweaks.CONFIG.config.huds.fps.x,
                () -> SkyblockTweaks.CONFIG.config.huds.fps.y,
                () -> SkyblockTweaks.CONFIG.config.huds.fps.scale,
                () -> SkyblockTweaks.CONFIG.config.huds.fps.anchor,
                x -> SkyblockTweaks.CONFIG.config.huds.fps.x = (float) x,
                y -> SkyblockTweaks.CONFIG.config.huds.fps.y = (float) y,
                scale -> SkyblockTweaks.CONFIG.config.huds.fps.scale = (float) scale,
                anchor -> SkyblockTweaks.CONFIG.config.huds.fps.anchor = anchor
        );
        line = new HudLine(
                () -> SkyblockTweaks.CONFIG.config.huds.fps.color,
                () -> SkyblockTweaks.CONFIG.config.huds.fps.outlineColor,
                () -> SkyblockTweaks.CONFIG.config.huds.fps.mode,
                () ->  SkyblockTweaks.CONFIG.config.huds.fps.reverse ?  "FPS: " + SkyblockTweaks.mc.getCurrentFps() : SkyblockTweaks.mc.getCurrentFps() + " FPS"

        );
    }
    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        if (((SkyblockTweaks.DATA.inSB  || SkyblockTweaks.CONFIG.config.huds.fps.showOutside) && SkyblockTweaks.CONFIG.config.huds.fps.enabled) || fromHudScreen) return true;
        return false;
    }

    @Override
    public String getName() {
        return "FPS HUD";
    }

    public static class Config {
        @SerialEntry
        public boolean enabled = false;

        @SerialEntry
        public boolean showOutside = false;

        @SerialEntry
        public boolean reverse = false;

        @SerialEntry
        public HudLine.DrawMode mode = HudLine.DrawMode.SHADOW;

        @SerialEntry
        public int color = 0xFFFFFF;

        @SerialEntry
        public int outlineColor = 0x000000;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.55f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public AnchorPoint anchor = AnchorPoint.LEFT;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(Text.literal("Enable FPS HUD"))
                    .description(OptionDescription.of(Text.literal("Enables the FPS HUD")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.fps.enabled,
                            () -> config.huds.fps.enabled,
                            value -> config.huds.fps.enabled = (Boolean) value
                    )
                    .build();

            var outside = Option.<Boolean>createBuilder()
                    .name(Text.literal("Show outside of Skyblock"))
                    .description(OptionDescription.of(Text.literal("Whether to show the FPS HUD outside of Skyblock")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.fps.showOutside,
                            () -> config.huds.fps.showOutside,
                            value -> config.huds.fps.showOutside = (Boolean) value
                    )
                    .build();

            var reverse = Option.<Boolean>createBuilder()
                    .name(Text.literal("Reverse FPS HUD"))
                    .description(OptionDescription.of(Text.literal("Whether to show the FPS as 'FPS: 123' instead of '123 FPS'")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.fps.reverse,
                            () -> config.huds.fps.reverse,
                            value -> config.huds.fps.reverse = (Boolean) value
                    )
                    .build();
            var color = Option.<Color>createBuilder()
                    .name(Text.literal("FPS HUD Color"))
                    .description(OptionDescription.of(Text.literal("The color of the FPS HUD")))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.fps.color),
                            () ->  new Color(config.huds.fps.color),
                            value -> config.huds.fps.color = value.getRGB()

                    )
                    .build();
            var outline = Option.<Color>createBuilder()
                    .name(Text.literal("FPS HUD Outline Color"))
                    .description(OptionDescription.of(Text.literal("The outline color of the FPS HUD")))
                    .controller(ColorControllerBuilder::create)
                    .available(config.huds.fps.mode == HudLine.DrawMode.OUTLINE)
                    .binding(
                            new Color(defaults.huds.fps.outlineColor),
                            () ->  new Color(config.huds.fps.outlineColor),
                            value -> config.huds.fps.outlineColor = value.getRGB()

                    )
                    .build();
            var mode = Option.<HudLine.DrawMode>createBuilder()
                    .name(Text.literal("FPS HUD Mode"))
                    .description(OptionDescription.of(Text.literal("The draw mode of the FPS HUD. Pure will render without shadow, Shadow will render with a shadow, and Outline will render with an outline\n§4Warning: §cOutline mode is still a work in progress and can cause annoying visual bugs in menus.")))
                    .controller(SkyblockTweaksConfig::generateDrawModeController)
                    .binding(
                            defaults.huds.fps.mode,
                            () -> config.huds.fps.mode,
                            value -> {
                                config.huds.fps.mode = value;
                                if (value == HudLine.DrawMode.OUTLINE) outline.setAvailable(true);
                                else outline.setAvailable(false);
                            }
                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(Text.literal("FPS HUD Scale"))
                    .description(OptionDescription.of(Text.literal("The scale of the FPS HUD")))
                    .controller(SkyblockTweaksConfig::generateScaleController)
                    .binding(
                            defaults.huds.fps.scale,
                            () -> config.huds.fps.scale,
                            value -> config.huds.fps.scale = value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(Text.literal("FPS HUD"))
                    .description(OptionDescription.of(Text.literal("Settings for the FPS HUD")))
                    .option(enabled)
                    .option(outside)
                    .option(reverse)
                    .option(mode)
                    .option(color)
                    .option(outline)
                    .option(scale)
                    .collapsed(true)
                    .build();
        }
    }
}