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
import wtf.cheeze.sbt.hud.utils.AnchorPoint;
import wtf.cheeze.sbt.hud.utils.HudName;
import wtf.cheeze.sbt.utils.NumberUtils;
import wtf.cheeze.sbt.hud.utils.DrawMode;
import wtf.cheeze.sbt.hud.components.SingleHudLine;
import wtf.cheeze.sbt.hud.utils.HudInformation;
import wtf.cheeze.sbt.hud.bases.TextHud;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.skyblock.SkyblockData;

import java.awt.*;

public class OverflowManaHud extends TextHud {

    public static final OverflowManaHud INSTANCE = new OverflowManaHud();

    private OverflowManaHud() {
        INFO = new HudInformation(
                () -> SBTConfig.huds().overflowMana.x,
                () -> SBTConfig.huds().overflowMana.y,
                () -> SBTConfig.huds().overflowMana.scale,
                () -> SBTConfig.huds().overflowMana.anchor,
                x -> SBTConfig.huds().overflowMana.x = x,
                y -> SBTConfig.huds().overflowMana.y = y,
                scale -> SBTConfig.huds().overflowMana.scale = scale,
                anchor -> SBTConfig.huds().overflowMana.anchor = anchor
        );
        line = new SingleHudLine(
                () -> SBTConfig.huds().overflowMana.color,
                () -> SBTConfig.huds().overflowMana.outlineColor,
                () -> SBTConfig.huds().overflowMana.mode,
                () -> Component.literal(NumberUtils.formatNumber((int) SkyblockData.Stats.overflowMana, SBTConfig.huds().overflowMana.separator) + (SBTConfig.huds().overflowMana.icon ? "ʬ" : ""))
        );

    }
    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        return (SkyblockData.inSB && SBTConfig.huds().overflowMana.enabled && (!SBTConfig.huds().overflowMana.hideWhenZero || SkyblockData.Stats.overflowMana != 0)) || fromHudScreen;
    }

    @Override
//    public String getName() {
//        return TextUtils.SECTION +  "3Oveflow Mana HUD";
//    }
    public @NotNull HudName getName() {

        return new HudName("Overflow Mana HUD", "OF Mana HUD", Colors.CYAN);
    }

    public static class Config {

        @SerialEntry
        public boolean enabled = false;

        @SerialEntry
        public boolean hideWhenZero = true;

        @SerialEntry
        public DrawMode mode = DrawMode.SHADOW;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.40f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public int color = Colors.CYAN;

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
                    .name(key("overflowMana.enabled"))
                    .description(keyD("overflowMana.enabled"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.overflowMana.enabled,
                            () -> config.huds.overflowMana.enabled,
                            value -> config.huds.overflowMana.enabled = value
                    )
                    .build();
            var hideWhenZero = Option.<Boolean>createBuilder()
                    .name(key("overflowMana.hideWhenZero"))
                    .description(keyD("overflowMana.hideWhenZero"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.overflowMana.hideWhenZero,
                            () -> config.huds.overflowMana.hideWhenZero,
                            value -> config.huds.overflowMana.hideWhenZero = value
                    )
                    .build();
            var color = Option.<Color>createBuilder()
                    .name(key("overflowMana.color"))
                    .description(keyD("overflowMana.color"))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.overflowMana.color),
                            () ->  new Color(config.huds.overflowMana.color),
                            value -> config.huds.overflowMana.color = value.getRGB()

                    )
                    .build();
            var outline = Option.<Color>createBuilder()
                    .name(key("overflowMana.outlineColor"))
                    .description(keyD("overflowMana.outlineColor"))
                    .controller(ColorControllerBuilder::create)
                    .available(config.huds.overflowMana.mode == DrawMode.OUTLINE)
                    .binding(
                            new Color(defaults.huds.overflowMana.outlineColor),
                            () ->  new Color(config.huds.overflowMana.outlineColor),
                            value -> config.huds.overflowMana.outlineColor = value.getRGB()

                    )
                    .build();
            var mode = Option.<DrawMode>createBuilder()
                    .name(key("overflowMana.mode"))
                    .description(keyD("overflowMana.mode"))
                    .controller(SBTConfig::generateDrawModeController)
                    .binding(
                            defaults.huds.overflowMana.mode,
                            () -> config.huds.overflowMana.mode,
                            value -> {
                                config.huds.overflowMana.mode = value;
                                outline.setAvailable(value == DrawMode.OUTLINE);
                            }
                    )
                    .build();
            var icon = Option.<Boolean>createBuilder()
                    .name(key("overflowMana.icon"))
                    .description(keyD("overflowMana.icon"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.overflowMana.icon,
                            () -> config.huds.overflowMana.icon,
                            value -> config.huds.overflowMana.icon = value
                    )
                    .build();
            var separator = Option.<String>createBuilder()
                    .name(key("overflowMana.separator"))
                    .description(keyD("overflowMana.separator"))
                    .controller(StringControllerBuilder::create)
                    .binding(
                            defaults.huds.overflowMana.separator,
                            () -> config.huds.overflowMana.separator,
                            value -> config.huds.overflowMana.separator = value
                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(key("overflowMana.scale"))
                    .description(keyD("overflowMana.scale"))
                    .controller(SBTConfig::generateScaleController)
                    .binding(
                            defaults.huds.overflowMana.scale,
                            () -> config.huds.overflowMana.scale,
                            value -> config.huds.overflowMana.scale = value
                    )
                    .build();
            return OptionGroup.createBuilder()
                    .name(key("overflowMana"))
                    .description(keyD("overflowMana"))
                    .option(enabled)
                    .option(hideWhenZero)
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
