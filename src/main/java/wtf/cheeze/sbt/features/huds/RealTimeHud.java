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
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.hud.bases.TextHud;
import wtf.cheeze.sbt.hud.utils.AnchorPoint;
import wtf.cheeze.sbt.hud.utils.DrawMode;
import wtf.cheeze.sbt.hud.components.SingleHudLine;
import wtf.cheeze.sbt.hud.utils.HudInformation;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.skyblock.SkyblockData;

import java.awt.Color;
import java.time.LocalDateTime;

public class RealTimeHud extends TextHud {

    public RealTimeHud() {
        INFO = new HudInformation(
                () -> SBTConfig.huds().time.x,
                () -> SBTConfig.huds().time.y,
                () -> SBTConfig.huds().time.scale,
                () -> SBTConfig.huds().time.anchor,
                x -> SBTConfig.huds().time.x = x,
                y -> SBTConfig.huds().time.y = y,
                scale -> SBTConfig.huds().time.scale = scale,
                anchor -> SBTConfig.huds().time.anchor = anchor
        );
        line = new SingleHudLine(
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
        return ((SkyblockData.inSB || SBTConfig.huds().time.showOutside) && SBTConfig.huds().time.enabled) || fromHudScreen;
    }


    @Override
    public Text getName() {
        return Text.literal("Real Time HUD");
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
        public DrawMode mode = DrawMode.SHADOW;

        @SerialEntry
        public int color = Colors.WHITE;

        @SerialEntry
        public int outlineColor = Colors.BLACK;

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
                    .name(key("time.enabled"))
                    .description(keyD("time.enabled"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.time.enabled,
                            () -> config.huds.time.enabled,
                            value -> config.huds.time.enabled = (boolean) value
                    )
                    .build();

            var outside = Option.<Boolean>createBuilder()
                    .name(key("time.showOutside"))
                    .description(keyD("time.showOutside"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.time.showOutside,
                            () -> config.huds.time.showOutside,
                            value -> config.huds.time.showOutside = (boolean) value
                    )
                    .build();

            var seconds = Option.<Boolean>createBuilder()
                    .name(key("time.seconds"))
                    .description(keyD("time.seconds"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.time.seconds,
                            () -> config.huds.time.seconds,
                            value -> config.huds.time.seconds = (boolean) value
                    )
                    .build();

            var amPM = Option.<Boolean>createBuilder()
                    .name(key("time.amPM"))
                    .description(keyD("time.amPM"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.time.amPM,
                            () -> config.huds.time.amPM,
                            value -> config.huds.time.amPM = (boolean) value
                    )
                    .available(config.huds.time.twelveHour)
                    .build();
            var twelveHour = Option.<Boolean>createBuilder()
                    .name(key("time.twelveHour"))
                    .description(keyD("time.twelveHour"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.time.twelveHour,
                            () -> config.huds.time.twelveHour,
                            value -> {
                                config.huds.time.twelveHour = (boolean) value;
                                amPM.setAvailable((boolean) value);
                            }
                    )
                    .build();

            var color = Option.<Color>createBuilder()
                    .name(key("time.color"))
                    .description(keyD("time.color"))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.time.color),
                            () ->  new Color(config.huds.time.color),
                            value -> config.huds.time.color = value.getRGB()

                    )
                    .build();
            var outline = Option.<Color>createBuilder()
                    .name(key("time.outlineColor"))
                    .description(keyD("time.outlineColor"))
                    .controller(ColorControllerBuilder::create)
                    .available(config.huds.time.mode == DrawMode.OUTLINE)
                    .binding(
                            new Color(defaults.huds.time.outlineColor),
                            () ->  new Color(config.huds.time.outlineColor),
                            value -> config.huds.time.outlineColor = value.getRGB()

                    )
                    .build();
            var mode = Option.<DrawMode>createBuilder()
                    .name(key("time.mode"))
                    .description(keyD("time.mode"))
                    .controller(SBTConfig::generateDrawModeController)
                    .binding(
                            defaults.huds.time.mode,
                            () -> config.huds.time.mode,
                            value -> {
                                config.huds.time.mode = value;
                                outline.setAvailable(value == DrawMode.OUTLINE);
                            }
                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(key("time.scale"))
                    .description(keyD("time.scale"))
                    .controller(SBTConfig::generateScaleController)
                    .binding(
                            defaults.huds.time.scale,
                            () -> config.huds.time.scale,
                            value -> config.huds.time.scale = value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(key("time"))
                    .description(keyD("time"))
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
