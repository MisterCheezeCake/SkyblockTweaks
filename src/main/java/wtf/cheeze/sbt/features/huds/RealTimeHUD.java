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
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.utils.hud.HudLine;
import wtf.cheeze.sbt.utils.hud.HudInformation;
import wtf.cheeze.sbt.utils.hud.TextHUD;

import java.awt.Color;
import java.time.LocalDateTime;

public class RealTimeHUD extends TextHUD {

    public RealTimeHUD() {
        INFO = new HudInformation(
                () -> SBTConfig.huds().time.x,
                () -> SBTConfig.huds().time.y,
                () -> SBTConfig.huds().time.scale,
                () -> SBTConfig.huds().time.anchor,
                x -> SBTConfig.huds().time.x = (float) x,
                y -> SBTConfig.huds().time.y = (float) y,
                scale -> SBTConfig.huds().time.scale = (float) scale,
                anchor -> SBTConfig.huds().time.anchor = anchor
        );
        line = new HudLine(
                () -> SBTConfig.huds().time.color,
                () -> SBTConfig.huds().time.outlineColor,
                () -> SBTConfig.huds().time.mode,
                () ->  {
                    var time = LocalDateTime.now();
                    var hour = time.getHour();
                    var minute = time.getMinute();
                    var second = time.getSecond();
                    var secondString = "";

                    if (SBTConfig.huds().time.seconds) {
                        secondString = String.format(":%02d", second);
                    }
                    if (SBTConfig.huds().time.twelveHour) {
                        var amPM = "";
                        if (hour >= 12) {
                            amPM = SBTConfig.huds().time.amPM ? "PM" : "";
                            if (hour > 12) hour -= 12;
                        } else {
                            amPM = SBTConfig.huds().time.amPM ? "AM" : "";
                            if (hour == 0) hour = 12;
                        }
                        return Text.literal(String.format("%d:%02d%s %s", hour, minute, secondString, amPM));
                    } else {
                        return Text.literal(String.format("%02d:%02d%s", hour, minute, secondString));
                    }
                }
        );
    }
    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        if (((SkyblockTweaks.DATA.inSB  || SBTConfig.huds().time.showOutside) && SBTConfig.huds().time.enabled) || fromHudScreen) return true;
        return false;
    }


    @Override
    public String getName() {
        return "Real Time HUD";
    }

    public static class Config {
        @SerialEntry
        public boolean enabled = false;

        @SerialEntry
        public boolean showOutside = false;

        @SerialEntry
        public boolean seconds = false;

        @SerialEntry
        public boolean twelveHour = true;

        @SerialEntry
        public boolean amPM = true;

        @SerialEntry
        public HudLine.DrawMode mode = HudLine.DrawMode.SHADOW;

        @SerialEntry
        public int color = 0xFFFFFF;

        @SerialEntry
        public int outlineColor = 0x000000;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.65f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public AnchorPoint anchor = AnchorPoint.LEFT;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(Text.literal("Enable Real Time HUD"))
                    .description(OptionDescription.of(Text.literal("Enables the Real Time HUD")))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.time.enabled,
                            () -> config.huds.time.enabled,
                            value -> config.huds.time.enabled = (Boolean) value
                    )
                    .build();

            var outside = Option.<Boolean>createBuilder()
                    .name(Text.literal("Show outside of Skyblock"))
                    .description(OptionDescription.of(Text.literal("Whether to show the Real Time HUD outside of Skyblock")))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.time.showOutside,
                            () -> config.huds.time.showOutside,
                            value -> config.huds.time.showOutside = (Boolean) value
                    )
                    .build();

            var seconds = Option.<Boolean>createBuilder()
                    .name(Text.literal("Show Seconds"))
                    .description(OptionDescription.of(Text.literal("Whether to show the seconds in the Real Time HUD")))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.time.seconds,
                            () -> config.huds.time.seconds,
                            value -> config.huds.time.seconds = (Boolean) value
                    )
                    .build();

            var amPM = Option.<Boolean>createBuilder()
                    .name(Text.literal("Show AM/PM"))
                    .description(OptionDescription.of(Text.literal("Whether to show AM/PM in the Real Time HUD")))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.time.amPM,
                            () -> config.huds.time.amPM,
                            value -> config.huds.time.amPM = (Boolean) value
                    )
                    .available(config.huds.time.twelveHour)
                    .build();
            var twelveHour = Option.<Boolean>createBuilder()
                    .name(Text.literal("12 Hour Time"))
                    .description(OptionDescription.of(Text.literal("Whether to use the 12 hour format in the Real Time HUD")))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.time.twelveHour,
                            () -> config.huds.time.twelveHour,
                            value -> {
                                config.huds.time.twelveHour = (Boolean) value;
                                amPM.setAvailable((boolean) value);
                            }
                    )
                    .build();

            var color = Option.<Color>createBuilder()
                    .name(Text.literal("Real Time HUD Color"))
                    .description(OptionDescription.of(Text.literal("The color of the Real Time HUD")))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.time.color),
                            () ->  new Color(config.huds.time.color),
                            value -> config.huds.time.color = value.getRGB()

                    )
                    .build();
            var outline = Option.<Color>createBuilder()
                    .name(Text.literal("Real Time HUD Outline Color"))
                    .description(OptionDescription.of(Text.literal("The outline color of the Real Time HUD")))
                    .controller(ColorControllerBuilder::create)
                    .available(config.huds.time.mode == HudLine.DrawMode.OUTLINE)
                    .binding(
                            new Color(defaults.huds.time.outlineColor),
                            () ->  new Color(config.huds.time.outlineColor),
                            value -> config.huds.time.outlineColor = value.getRGB()

                    )
                    .build();
            var mode = Option.<HudLine.DrawMode>createBuilder()
                    .name(Text.literal("Real Time HUD Mode"))
                    .description(OptionDescription.of(Text.literal("The draw mode of the Real Time HUD. Pure will render without shadow, Shadow will render with a shadow, and Outline will render with an outline\n§4Warning: §cOutline mode is still a work in progress and can cause annoying visual bugs in menus.")))
                    .controller(SBTConfig::generateDrawModeController)
                    .binding(
                            defaults.huds.time.mode,
                            () -> config.huds.time.mode,
                            value -> {
                                config.huds.time.mode = value;
                                if (value == HudLine.DrawMode.OUTLINE) outline.setAvailable(true);
                                else outline.setAvailable(false);
                            }
                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(Text.literal("Real Time HUD Scale"))
                    .description(OptionDescription.of(Text.literal("The scale of the Real Time HUD")))
                    .controller(SBTConfig::generateScaleController)
                    .binding(
                            defaults.huds.time.scale,
                            () -> config.huds.time.scale,
                            value -> config.huds.time.scale = value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(Text.literal("Real Time HUD"))
                    .description(OptionDescription.of(Text.literal("Settings for the Real Time HUD")))
                    .option(enabled)
                    .option(outside)
                    .option(seconds)
                    .option(twelveHour)
                    .option(amPM)
                    .option(mode)
                    .option(color)
                    .option(outline)
                    .option(scale)
                    .collapsed(true)
                    .build();
        }
    }
}
