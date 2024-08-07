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

public class SpeedHUD extends TextHUD {

    public SpeedHUD() {
        INFO = new HudInformation(
                () -> SkyblockTweaks.CONFIG.config.huds.speed.x,
                () -> SkyblockTweaks.CONFIG.config.huds.speed.y,
                () -> SkyblockTweaks.CONFIG.config.huds.speed.scale,
                () -> SkyblockTweaks.CONFIG.config.huds.speed.anchor,
                x -> SkyblockTweaks.CONFIG.config.huds.speed.x = (float) x,
                y -> SkyblockTweaks.CONFIG.config.huds.speed.y = (float) y,
                scale -> SkyblockTweaks.CONFIG.config.huds.speed.scale = (float) scale,
                anchor -> SkyblockTweaks.CONFIG.config.huds.speed.anchor = anchor
        );
        line = new HudLine(
                () -> SkyblockTweaks.CONFIG.config.huds.speed.color,
                () -> SkyblockTweaks.CONFIG.config.huds.speed.outlineColor,
                () -> SkyblockTweaks.CONFIG.config.huds.speed.mode,
                () ->  (SkyblockTweaks.DATA.getSpeed()+"").split("\\.")[0] + "%"
        );
    }
    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        if ((SkyblockTweaks.DATA.inSB && SkyblockTweaks.CONFIG.config.huds.speed.enabled) || fromHudScreen) return true;
        return false;
    }

    @Override
    public String getName() {
        return "Speed Percentage HUD";
    }

    public static class Config {
        @SerialEntry
        public boolean enabled = false;

        @SerialEntry
        public HudLine.DrawMode mode = HudLine.DrawMode.SHADOW;

        @SerialEntry
        public int color = 0xFFFFFF;

        @SerialEntry
        public int outlineColor = 0x000000;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.35f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public AnchorPoint anchor = AnchorPoint.LEFT;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(Text.literal("Enable Speed HUD"))
                    .description(OptionDescription.of(Text.literal("Enables the Speed HUD")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.speed.enabled,
                            () -> config.huds.speed.enabled,
                            value -> config.huds.speed.enabled = (Boolean) value
                    )
                    .build();

            var color = Option.<Color>createBuilder()
                    .name(Text.literal("Speed HUD Color"))
                    .description(OptionDescription.of(Text.literal("The color of the Speed HUD")))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.speed.color),
                            () ->  new Color(config.huds.speed.color),
                            value -> config.huds.speed.color = value.getRGB()

                    )
                    .build();
            var outline = Option.<Color>createBuilder()
                    .name(Text.literal("Speed HUD Outline Color"))
                    .description(OptionDescription.of(Text.literal("The outline color of the Speed HUD")))
                    .controller(ColorControllerBuilder::create)
                    .available(config.huds.speed.mode == HudLine.DrawMode.OUTLINE)
                    .binding(
                            new Color(defaults.huds.speed.outlineColor),
                            () ->  new Color(config.huds.speed.outlineColor),
                            value -> config.huds.speed.outlineColor = value.getRGB()

                    )
                    .build();
            var mode = Option.<HudLine.DrawMode>createBuilder()
                    .name(Text.literal("Speed HUD Mode"))
                    .description(OptionDescription.of(Text.literal("The draw mode of the Speed HUD. Pure will render without shadow, Shadow will render with a shadow, and Outline will render with an outline\n§4Warning: §cOutline mode is still a work in progress and can cause annoying visual bugs in menus.")))
                    .controller(SkyblockTweaksConfig::generateDrawModeController)
                    .binding(
                            defaults.huds.speed.mode,
                            () -> config.huds.speed.mode,
                            value -> {
                                config.huds.speed.mode = value;
                                if (value == HudLine.DrawMode.OUTLINE) outline.setAvailable(true);
                                else outline.setAvailable(false);
                            }
                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(Text.literal("Speed HUD Scale"))
                    .description(OptionDescription.of(Text.literal("The scale of the Speed HUD")))
                    .controller(SkyblockTweaksConfig::generateScaleController)
                    .binding(
                            defaults.huds.speed.scale,
                            () -> config.huds.speed.scale,
                            value -> config.huds.speed.scale = value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(Text.literal("Speed HUD"))
                    .description(OptionDescription.of(Text.literal("Settings for the Speed HUD")))
                    .option(enabled)
                    .option(mode)
                    .option(color)
                    .option(outline)
                    .option(scale)
                    .collapsed(true)
                    .build();
        }
    }
}

