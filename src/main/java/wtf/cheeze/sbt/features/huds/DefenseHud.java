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
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.hud.bases.TextHud;
import wtf.cheeze.sbt.hud.utils.AnchorPoint;
import wtf.cheeze.sbt.hud.utils.HudName;
import wtf.cheeze.sbt.utils.NumberUtils;
import wtf.cheeze.sbt.hud.utils.DrawMode;
import wtf.cheeze.sbt.hud.utils.HudInformation;
import wtf.cheeze.sbt.hud.components.SingleHudLine;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.skyblock.SkyblockData;

import java.awt.Color;

public class DefenseHud extends TextHud {

    public static final DefenseHud INSTANCE = new DefenseHud();

    private DefenseHud() {
        INFO = new HudInformation(
                () -> SBTConfig.huds().defense.x,
                () -> SBTConfig.huds().defense.y,
                () -> SBTConfig.huds().defense.scale,
                () -> SBTConfig.huds().defense.anchor,
                x -> SBTConfig.huds().defense.x = x,
                y -> SBTConfig.huds().defense.y = y,
                scale -> SBTConfig.huds().defense.scale = scale,
                anchor -> SBTConfig.huds().defense.anchor = anchor
        );

        line = new SingleHudLine(
                () -> SBTConfig.huds().defense.color,
                () -> SBTConfig.huds().defense.outlineColor,
                () -> SBTConfig.huds().defense.mode,
                () -> Component.literal(NumberUtils.formatNumber(SkyblockData.Stats.defense, SBTConfig.huds().defense.separator) + (SBTConfig.huds().defense.icon ? "❈" : ""))
        );
    }
    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        return (SkyblockData.inSB && SBTConfig.huds().defense.enabled) || fromHudScreen;
    }



    @Override
    public @NotNull HudName getName() {
        return new HudName("Defense HUD", "Def HUD", Colors.LIME);
    }

    public static class Config {
        @SerialEntry
        public boolean enabled = false;

        @SerialEntry
        public DrawMode mode = DrawMode.SHADOW;

        @SerialEntry
        public int color = Colors.LIME;

        @SerialEntry
        public int outlineColor = Colors.BLACK;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.05f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public AnchorPoint anchor = AnchorPoint.LEFT;

        @SerialEntry
        public boolean icon = true;

        @SerialEntry
        public String separator  = ",";

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(key("defense.enabled"))
                    .description(keyD("defense.enabled"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.defense.enabled,
                            () -> config.huds.defense.enabled,
                            value -> config.huds.defense.enabled = value
                    )
                    .build();
            var color = Option.<Color>createBuilder()
                    .name(key("defense.color"))
                    .description(keyD("defense.color"))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.defense.color),
                            () ->  new Color(config.huds.defense.color),
                            value -> config.huds.defense.color = value.getRGB()

                    )
                    .build();
            var outline = Option.<Color>createBuilder()
                    .name(key("defense.outlineColor"))
                    .description(keyD("defense.outlineColor"))
                    .controller(ColorControllerBuilder::create)
                    .available(config.huds.defense.mode == DrawMode.OUTLINE)
                    .binding(
                            new Color(defaults.huds.defense.outlineColor),
                            () ->  new Color(config.huds.defense.outlineColor),
                            value -> config.huds.defense.outlineColor = value.getRGB()

                    )
                    .build();
            var mode = Option.<DrawMode>createBuilder()
                    .name(key("defense.mode"))
                    .description(keyD("defense.mode"))
                    .controller(SBTConfig::generateDrawModeController)
                    .binding(
                            defaults.huds.defense.mode,
                            () -> config.huds.defense.mode,
                            value -> {
                                config.huds.defense.mode = value;
                                outline.setAvailable(value == DrawMode.OUTLINE);
                            }
                    )
                    .build();
            var icon = Option.<Boolean>createBuilder()
                    .name(key("defense.icon"))
                    .description(keyD("defense.icon"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.defense.icon,
                            () -> config.huds.defense.icon,
                            value -> config.huds.defense.icon = value
                    )
                    .build();
            var separator = Option.<String>createBuilder()
                    .name(key("defense.separator"))
                    .description(keyD("defense.separator"))
                    .controller(StringControllerBuilder::create)
                    .binding(
                            defaults.huds.defense.separator,
                            () -> config.huds.defense.separator,
                            value -> config.huds.defense.separator = value
                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(key("defense.scale"))
                    .description(keyD("defense.scale"))
                    .controller(SBTConfig::generateScaleController)
                    .binding(
                            defaults.huds.defense.scale,
                            () -> config.huds.defense.scale,
                            value -> config.huds.defense.scale = value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(key("defense"))
                    .description(keyD("defense"))
                    .option(enabled)
                    .option(mode)
                    .option(color)
                    .option(outline)
                    .option(icon)
                    .option(separator)
                    .option(scale)
                    .collapsed(true)
                    .build();
        }
    }
}


