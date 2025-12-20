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
import wtf.cheeze.sbt.hud.components.SingleHudLine;
import wtf.cheeze.sbt.hud.utils.AnchorPoint;
import wtf.cheeze.sbt.hud.utils.DrawMode;
import wtf.cheeze.sbt.hud.utils.HudInformation;
import wtf.cheeze.sbt.hud.utils.HudName;
import wtf.cheeze.sbt.utils.NumberUtils;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.skyblock.SkyblockData;

import java.awt.Color;

public class EhpHud extends TextHud {
    public static final EhpHud INSTANCE = new EhpHud();

    private EhpHud() {
        INFO = new HudInformation(
                () -> SBTConfig.huds().ehp.x,
                () -> SBTConfig.huds().ehp.y,
                () -> SBTConfig.huds().ehp.scale,
                () -> SBTConfig.huds().ehp.anchor,
                x -> SBTConfig.huds().ehp.x = x,
                y -> SBTConfig.huds().ehp.y = y,
                scale -> SBTConfig.huds().ehp.scale = scale,
                anchor -> SBTConfig.huds().ehp.anchor = anchor
        );
        line = new SingleHudLine(
                () -> SBTConfig.huds().ehp.color,
                () -> SBTConfig.huds().ehp.outlineColor,
                () -> SBTConfig.huds().ehp.mode,
                () -> Component.literal(NumberUtils.formatNumber((int) SkyblockData.Stats.effectiveHealth(), SBTConfig.huds().ehp.separator) + (SBTConfig.huds().ehp.icon ? "❤" : ""))
        );

    }

    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        return (SkyblockData.inSB && SBTConfig.huds().ehp.enabled) || fromHudScreen;
    }

    @Override
    public @NotNull HudName getName() {

        return new HudName("Effective Health HUD", "EHP HUD", Colors.GREEN);
    }

    public static class Config {
        @SerialEntry
        public boolean enabled = false;

        @SerialEntry
        public DrawMode mode = DrawMode.SHADOW;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.10f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public int color = Colors.GREEN;

        @SerialEntry
        public int outlineColor = Colors.BLACK;

        @SerialEntry
        public boolean icon = true;

        @SerialEntry
        public String separator = ",";

        @SerialEntry
        public AnchorPoint anchor = AnchorPoint.LEFT;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(key("ehp.enabled"))
                    .description(keyD("ehp.enabled"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.ehp.enabled,
                            () -> config.huds.ehp.enabled,
                            value -> config.huds.ehp.enabled = value
                    )
                    .build();
            var outline = Option.<Color>createBuilder()
                    .name(key("ehp.outlineColor"))
                    .description(keyD("ehp.outlineColor"))
                    .controller(ColorControllerBuilder::create)
                    .available(config.huds.ehp.mode == DrawMode.OUTLINE)
                    .binding(
                            new Color(defaults.huds.ehp.outlineColor),
                            () ->  new Color(config.huds.ehp.outlineColor),
                            value -> config.huds.ehp.outlineColor = value.getRGB()



                    )
                    .build();
            var mode = Option.<DrawMode>createBuilder()
                    .name(key("ehp.mode"))
                    .description(keyD("ehp.mode"))
                    .controller(SBTConfig::generateDrawModeController)
                    .binding(
                            defaults.huds.ehp.mode,
                            () -> config.huds.ehp.mode,
                            value -> {
                                config.huds.ehp.mode = value;
                                outline.setAvailable(value == DrawMode.OUTLINE);
                            }
                    )
                    .build();

            var color = Option.<Color>createBuilder()
                    .name(key("ehp.color"))
                    .description(keyD("ehp.color"))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.ehp.color),
                            () ->  new Color(config.huds.ehp.color),
                            value -> config.huds.ehp.color = value.getRGB()

                    )
                    .build();
            var icon = Option.<Boolean>createBuilder()
                    .name(key("ehp.icon"))
                    .description(keyD("ehp.icon"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.ehp.icon,
                            () -> config.huds.ehp.icon,
                            value -> config.huds.ehp.icon = value
                    )
                    .build();
            var separator = Option.<String>createBuilder()
                    .name(key("ehp.separator"))
                    .description(keyD("ehp.separator"))
                    .controller(StringControllerBuilder::create)
                    .binding(
                            defaults.huds.ehp.separator,
                            () -> config.huds.ehp.separator,
                            value -> config.huds.ehp.separator = value
                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(key("ehp.scale"))
                    .description(keyD("ehp.scale"))
                    .controller(SBTConfig::generateScaleController)
                    .binding(
                            defaults.huds.ehp.scale,
                            () -> config.huds.ehp.scale,
                            value -> config.huds.ehp.scale = value
                    )
                    .build();


            return OptionGroup.createBuilder()
                    .name(key("ehp"))
                    .description(keyD("ehp"))
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



