/*
 * Copyright (C) 2025 MisterCheezeCake
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
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.hud.bases.TextHud;
import wtf.cheeze.sbt.hud.components.SingleHudLine;
import wtf.cheeze.sbt.hud.utils.AnchorPoint;
import wtf.cheeze.sbt.hud.utils.DrawMode;
import wtf.cheeze.sbt.hud.utils.HudInformation;
import wtf.cheeze.sbt.hud.utils.HudName;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.skyblock.SkyblockData;
import wtf.cheeze.sbt.utils.text.Symbols;

import java.awt.*;

public class PressureHud extends TextHud {

    public static final PressureHud INSTANCE = new PressureHud();

    private static final String BOTH = Symbols.PRESSURE + "%d%%";
    private static final String PERCENT = "%d%%";
    private static final String ICON = Symbols.PRESSURE + "%d";
    private static final String NEITHER = "%d";

    private PressureHud() {
        INFO = new HudInformation(
                () -> SBTConfig.huds().pressure.x,
                () -> SBTConfig.huds().pressure.y,
                () -> SBTConfig.huds().pressure.scale,
                () -> SBTConfig.huds().pressure.anchor,
                x -> SBTConfig.huds().pressure.x = x,
                y -> SBTConfig.huds().pressure.y = y,
                scale -> SBTConfig.huds().pressure.scale = scale,
                anchor -> SBTConfig.huds().pressure.anchor = anchor
        );
        line = new SingleHudLine(
                () -> SBTConfig.huds().pressure.color,
                () -> SBTConfig.huds().pressure.outlineColor,
                () -> SBTConfig.huds().pressure.mode,
                () -> Component.literal(getFormatString().formatted(SkyblockData.Stats.pressure))
        );
    }

    private String getFormatString() {
        if (SBTConfig.huds().pressure.percent && SBTConfig.huds().pressure.icon) {
            return BOTH;
        } else if (SBTConfig.huds().pressure.percent) {
            return PERCENT;
        } else if (SBTConfig.huds().pressure.icon) {
            return ICON;
        } else {
            return NEITHER;
        }
    }

    @Override
    public @NotNull HudName getName() {
        return new HudName("Pressure HUD", "Pressure", Colors.BLUE);
    }

    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        return (SkyblockData.inSB && SkyblockData.Stats.pressureActive && SBTConfig.huds().pressure.enabled) || fromHudScreen;
    }

    public static class Config {
        @SerialEntry
        public boolean enabled = false;

        @SerialEntry
        public DrawMode mode = DrawMode.SHADOW;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0.2f;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.40f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public int color = Colors.BLUE;

        @SerialEntry
        public int outlineColor = Colors.BLACK;

        @SerialEntry
        public boolean icon = true;

        @SerialEntry
        public boolean percent = true;

        @SerialEntry
        public AnchorPoint anchor = AnchorPoint.LEFT;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(key("pressure.enabled"))
                    .description(keyD("pressure.enabled"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.pressure.enabled,
                            () -> config.huds.pressure.enabled,
                            value -> config.huds.pressure.enabled = value
                    )
                    .build();

            var color = Option.<Color>createBuilder()
                    .name(key("pressure.color"))
                    .description(keyD("pressure.color"))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.pressure.color),
                            () -> new Color(config.huds.pressure.color),
                            value -> config.huds.pressure.color = value.getRGB()

                    )
                    .build();

            var outline = Option.<Color>createBuilder()
                    .name(key("pressure.outlineColor"))
                    .description(keyD("pressure.outlineColor"))
                    .controller(ColorControllerBuilder::create)
                    .available(config.huds.pressure.mode == DrawMode.OUTLINE)
                    .binding(
                            new Color(defaults.huds.pressure.outlineColor),
                            () -> new Color(config.huds.pressure.outlineColor),
                            value -> config.huds.pressure.outlineColor = value.getRGB()

                    )
                    .build();
            var mode = Option.<DrawMode>createBuilder()
                    .name(key("pressure.mode"))
                    .description(keyD("pressure.mode"))
                    .controller(SBTConfig::generateDrawModeController)
                    .binding(
                            defaults.huds.pressure.mode,
                            () -> config.huds.pressure.mode,
                            value -> {
                                config.huds.pressure.mode = value;
                                outline.setAvailable(value == DrawMode.OUTLINE);
                            }
                    )
                    .build();

            var icon = Option.<Boolean>createBuilder()
                    .name(key("pressure.icon"))
                    .description(keyD("pressure.icon"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.pressure.icon,
                            () -> config.huds.pressure.icon,
                            value -> config.huds.pressure.icon = value
                    )
                    .build();

            var percent = Option.<Boolean>createBuilder()
                    .name(key("pressure.percent"))
                    .description(keyD("pressure.percent"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.pressure.percent,
                            () -> config.huds.pressure.percent,
                            value -> config.huds.pressure.percent = value
                    )
                    .build();

            var scale = Option.<Float>createBuilder()
                    .name(key("pressure.scale"))
                    .description(keyD("pressure.scale"))
                    .controller(SBTConfig::generateScaleController)
                    .binding(
                            defaults.huds.pressure.scale,
                            () -> config.huds.pressure.scale,
                            value -> config.huds.pressure.scale = value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(key("pressure"))
                    .description(keyD("pressure"))
                    .option(enabled)
                    .option(mode)
                    .option(color)
                    .option(outline)
                    .option(icon)
                    .option(percent)
                    .option(scale)
                    .collapsed(true)
                    .build();
        }
    }
}
