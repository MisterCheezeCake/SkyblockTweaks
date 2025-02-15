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
package wtf.cheeze.sbt.features.mining;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.config.categories.Mining;
import wtf.cheeze.sbt.hud.bases.TextHUD;
import wtf.cheeze.sbt.hud.components.SingleHudLine;
import wtf.cheeze.sbt.hud.utils.AnchorPoint;
import wtf.cheeze.sbt.hud.utils.DrawMode;
import wtf.cheeze.sbt.hud.utils.HudInformation;
import wtf.cheeze.sbt.utils.NumberUtils;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.skyblock.MiningData;
import wtf.cheeze.sbt.utils.skyblock.SkyblockData;
import wtf.cheeze.sbt.utils.skyblock.SkyblockUtils;

import java.awt.*;

public class EventTimerHUD extends TextHUD {

    public EventTimerHUD() {
        super();
        INFO = new HudInformation(
                () -> SBTConfig.mining().eventTimer.x,
                () -> SBTConfig.mining().eventTimer.y,
                () -> SBTConfig.mining().eventTimer.scale,
                () -> SBTConfig.mining().eventTimer.anchor,
                x -> SBTConfig.mining().eventTimer.x = x,
                y -> SBTConfig.mining().eventTimer.y = y,
                scale -> SBTConfig.mining().eventTimer.scale = scale,
                anchor -> SBTConfig.mining().eventTimer.anchor = anchor
        );
        line = new SingleHudLine(
                () -> Colors.WHITE,
                () -> SBTConfig.mining().eventTimer.outlineColor,
                () -> SBTConfig.mining().eventTimer.mode,
                () -> {
                    if (!SkyblockData.miningData.event) return TextUtils.withColor("No Event", Colors.GRAY);
                    return TextUtils.join(
                            TextUtils.withColor(SkyblockData.miningData.eventName + ": ", SBTConfig.mining().eventTimer.colorPrimary),
                            // Format the time as MM:SS
                            TextUtils.withColor(
                                    NumberUtils.formatTime(SkyblockData.miningData.eventTimeLeft, false),
                                    SBTConfig.mining().eventTimer.colorSecondary
                            )
                    );
                },
                () -> MiningData.getEventIcon(SkyblockData.miningData.eventName),
                () -> (SBTConfig.mining().eventTimer.icons && MiningData.getEventIcon(SkyblockData.miningData.eventName) != null)
        );

    }

    @Override
    public Text getName() {
        return TextUtils.withColor("Event Timer", Colors.LIGHT_BLUE);
    }

    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        return (SkyblockUtils.inMiningIsland()) && (SBTConfig.mining().eventTimer.enabled || fromHudScreen);
    }

    public static class Config {

        @SerialEntry
        public boolean enabled = false;

        @SerialEntry
        public float x = 0.25f;

        @SerialEntry
        public float y = 0.25f;

        @SerialEntry
        public boolean icons = true;

        @SerialEntry
        public float scale = 1;

        @SerialEntry
        public int colorPrimary = Colors.CYAN;

        @SerialEntry
        public int colorSecondary = Colors.WHITE;


        @SerialEntry
        public int outlineColor = Colors.BLACK;

        @SerialEntry
        public DrawMode mode = DrawMode.SHADOW;

        @SerialEntry
        public AnchorPoint anchor = AnchorPoint.LEFT;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(Mining.key("eventHud.enabled"))
                    .description(Mining.keyD("eventHud.enabled"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.mining.eventTimer.enabled,
                            () -> config.mining.eventTimer.enabled,
                            value -> config.mining.eventTimer.enabled = (boolean) value
                    )
                    .build();
            var icons = Option.<Boolean>createBuilder()
                    .name(Mining.key("eventHud.icons"))
                    .description(Mining.keyD("eventHud.icons"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.mining.eventTimer.icons,
                            () -> config.mining.eventTimer.icons,
                            value -> config.mining.eventTimer.icons = (boolean) value
                    )
                    .build();
            var colorPrimary = Option.<Color>createBuilder()
                    .name(Mining.key("eventHud.colorPrimary"))
                    .description(Mining.keyD("eventHud.colorPrimary"))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.mining.eventTimer.colorPrimary),
                            () -> new Color(config.mining.eventTimer.colorPrimary),
                            value -> config.mining.eventTimer.colorPrimary = value.getRGB()
                    )
                    .build();

            var colorSecondary = Option.<Color>createBuilder()
                    .name(Mining.key("eventHud.colorSecondary"))
                    .description(Mining.keyD("eventHud.colorSecondary"))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.mining.eventTimer.colorSecondary),
                            () -> new Color(config.mining.eventTimer.colorSecondary),
                            value -> config.mining.eventTimer.colorSecondary = value.getRGB()
                    )
                    .build();

            var outlineColor = Option.<Color>createBuilder()
                    .name(Mining.key("eventHud.outlineColor"))
                    .description(Mining.keyD("eventHud.outlineColor"))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.mining.eventTimer.outlineColor),
                            () -> new Color(config.mining.eventTimer.outlineColor),
                            value -> config.mining.eventTimer.outlineColor = value.getRGB()
                    )
                    .build();


            var drawMode = Option.<DrawMode>createBuilder()
                    .name(Mining.key("eventHud.mode"))
                    .description(Mining.keyD("eventHud.mode"))
                    .controller(SBTConfig::generateDrawModeController)
                    .binding(
                            defaults.mining.eventTimer.mode,
                            () -> config.mining.eventTimer.mode,
                            value -> {
                                config.mining.eventTimer.mode = (DrawMode) value;
                                outlineColor.setAvailable(value == DrawMode.OUTLINE);
                            }
                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(Mining.key("eventHud.scale"))
                    .description(Mining.keyD("eventHud.scale"))
                    .controller(SBTConfig::generateScaleController)
                    .binding(
                            defaults.mining.eventTimer.scale,
                            () -> config.mining.eventTimer.scale,
                            value -> config.mining.eventTimer.scale = (float) value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(Mining.key("eventHud"))
                    .description(Mining.keyD("eventHud"))
                    .option(enabled)
                    .option(icons)
                    .option(drawMode)
                    .option(colorPrimary)
                    .option(colorSecondary)
                    .option(outlineColor)
                    .option(scale)
                    .collapsed(true)
                    .build();
        }

    }

}
