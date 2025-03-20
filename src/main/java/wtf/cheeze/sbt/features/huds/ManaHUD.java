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
import net.minecraft.text.Text;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.hud.utils.AnchorPoint;
import wtf.cheeze.sbt.utils.NumberUtils;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.hud.utils.DrawMode;
import wtf.cheeze.sbt.hud.utils.HudInformation;
import wtf.cheeze.sbt.hud.bases.TextHUD;
import wtf.cheeze.sbt.hud.components.SingleHudLine;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.skyblock.SkyblockData;


import java.awt.*;

public class ManaHUD extends TextHUD {

    public ManaHUD() {
        INFO = new HudInformation(
                () -> SBTConfig.huds().mana.x,
                () -> SBTConfig.huds().mana.y,
                () -> SBTConfig.huds().mana.scale,
                () -> SBTConfig.huds().mana.anchor,
                x -> SBTConfig.huds().mana.x = x,
                y -> SBTConfig.huds().mana.y = y,
                scale -> SBTConfig.huds().mana.scale = scale,
                anchor -> SBTConfig.huds().mana.anchor = anchor
        );
        line = new SingleHudLine(
                () -> SBTConfig.huds().mana.color,
                () -> SBTConfig.huds().mana.outlineColor,
                () -> SBTConfig.huds().mana.mode,
                () -> Text.literal(NumberUtils.formatNumber((int) SkyblockData.Stats.mana, SBTConfig.huds().mana.separator) + "/" + NumberUtils.formatNumber((int) SkyblockData.Stats.maxMana, SBTConfig.huds().mana.separator) + (SBTConfig.huds().mana.icon ? "✎" : ""))
                //,
//                () -> Icons.SKILL_ICONS.get(Skill.MINING),
//                () -> true
        );
    }
    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        return (SkyblockData.inSB && SBTConfig.huds().mana.enabled) || fromHudScreen;
    }
//    @Override
//    public String getText() {
//        return TextUtils.formatNumber((int) SkyBlockTweaks.DATA.mana, SBTConfig.huds().mana.separator) + "/" + TextUtils.formatNumber((int) SkyBlockTweaks.DATA.maxMana, SBTConfig.huds().mana.separator) + (SBTConfig.huds().mana.icon ? "✎" : "");
//    }

    @Override
    public Text getName() {
        return TextUtils.withColor("Mana Hud", Colors.BLUE);
    }

    public static class Config {

        @SerialEntry
        public boolean enabled = false;

//        @SerialEntry
//        public boolean shadow = true;

        @SerialEntry
        public DrawMode mode = DrawMode.SHADOW;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.30f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public int color = Colors.BLUE;

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
                    .name(key("mana.enabled"))
                    .description(keyD("mana.enabled"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.mana.enabled,
                            () -> config.huds.mana.enabled,
                            value -> config.huds.mana.enabled = (boolean) value
                    )
                    .build();
            var color = Option.<Color>createBuilder()
                    .name(key("mana.color"))
                    .description(keyD("mana.color"))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.mana.color),
                            () ->  new Color(config.huds.mana.color),
                            value -> config.huds.mana.color = value.getRGB()

                    )
                    .build();
            var outline = Option.<Color>createBuilder()
                    .name(key("mana.outlineColor"))
                    .description(keyD("mana.outlineColor"))
                    .controller(ColorControllerBuilder::create)
                    .available(config.huds.mana.mode == DrawMode.OUTLINE)
                    .binding(
                            new Color(defaults.huds.mana.outlineColor),
                            () ->  new Color(config.huds.mana.outlineColor),
                            value -> config.huds.mana.outlineColor = value.getRGB()

                    )
                    .build();
            var mode = Option.<DrawMode>createBuilder()
                    .name(key("mana.mode"))
                    .description(keyD("mana.mode"))
                    .controller(SBTConfig::generateDrawModeController)
                    .binding(
                            defaults.huds.mana.mode,
                            () -> config.huds.mana.mode,
                            value -> {
                                config.huds.mana.mode = value;
                                outline.setAvailable(value == DrawMode.OUTLINE);
                            }
                    )
                    .build();
            var icon = Option.<Boolean>createBuilder()
                    .name(key("mana.icon"))
                    .description(keyD("mana.icon"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.mana.icon,
                            () -> config.huds.mana.icon,
                            value -> config.huds.mana.icon = (boolean) value
                    )
                    .build();
            var separator = Option.<String>createBuilder()
                    .name(key("mana.separator"))
                    .description(keyD("mana.separator"))
                    .controller(StringControllerBuilder::create)
                    .binding(
                            defaults.huds.mana.separator,
                            () -> config.huds.mana.separator,
                            value -> config.huds.mana.separator = value
                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(key("mana.scale"))
                    .description(keyD("mana.scale"))
                    .controller(SBTConfig::generateScaleController)
                    .binding(
                            defaults.huds.mana.scale,
                            () -> config.huds.mana.scale,
                            value -> config.huds.mana.scale = value
                    )
                    .build();
            return OptionGroup.createBuilder()
                    .name(key("mana"))
                    .description(keyD("mana"))
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
