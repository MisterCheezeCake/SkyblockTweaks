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
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.hud.utils.AnchorPoint;
import wtf.cheeze.sbt.hud.utils.DrawMode;
import wtf.cheeze.sbt.hud.components.SingleHudLine;
import wtf.cheeze.sbt.hud.utils.HudInformation;
import wtf.cheeze.sbt.hud.bases.TextHUD;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.utils.render.Colors;

import java.awt.Color;

public class ArmorStackHUD extends TextHUD {

    public ArmorStackHUD() {
        INFO = new HudInformation(
                () -> SBTConfig.huds().armorStack.x,
                () -> SBTConfig.huds().armorStack.y,
                () -> SBTConfig.huds().armorStack.scale,
                () -> SBTConfig.huds().armorStack.anchor,
                x -> SBTConfig.huds().armorStack.x = x,
                y -> SBTConfig.huds().armorStack.y = y,
                scale -> SBTConfig.huds().armorStack.scale = scale,
                anchor -> SBTConfig.huds().armorStack.anchor = anchor
        );
        line = new SingleHudLine(
                () -> SBTConfig.huds().armorStack.color,
                () -> SBTConfig.huds().armorStack.outlineColor,
                () -> SBTConfig.huds().armorStack.mode,
                () -> SkyblockTweaks.DATA.stackString != null ? Text.literal(SkyblockTweaks.DATA.armorStack + SkyblockTweaks.DATA.stackString) : Text.literal("0ᝐ")
        );
    }
    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        if (SkyblockTweaks.DATA.stackString == null && !fromHudScreen) return false;
        return (SkyblockTweaks.DATA.inSB && SBTConfig.huds().armorStack.enabled) || fromHudScreen;
    }

    @Override
    public Text getName() {
        return TextUtils.withColor("Armor Stack HUD", Colors.ORANGE);
    }

    public static class Config {
        @SerialEntry
        public boolean enabled = false;

        @SerialEntry
        public DrawMode mode = DrawMode.SHADOW;

        @SerialEntry
        public int color = Colors.ORANGE;

        @SerialEntry
        public int outlineColor = Colors.BLACK;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.95f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public AnchorPoint anchor = AnchorPoint.LEFT;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(key("armorStack.enabled"))
                    .description(keyD("armorStack.enabled"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.armorStack.enabled,
                            () -> config.huds.armorStack.enabled,
                            value -> config.huds.armorStack.enabled = (boolean) value
                    )
                    .build();

            var color = Option.<Color>createBuilder()
                    .name(key("armorStack.color"))
                    .description(keyD("armorStack.color"))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.armorStack.color),
                            () ->  new Color(config.huds.armorStack.color),
                            value -> config.huds.armorStack.color = value.getRGB()

                    )
                    .build();
            var outline = Option.<Color>createBuilder()
                    .name(key("armorStack.outlineColor"))
                    .description(keyD("armorStack.outlineColor"))
                    .controller(ColorControllerBuilder::create)
                    .available(config.huds.armorStack.mode == DrawMode.OUTLINE)
                    .binding(
                            new Color(defaults.huds.armorStack.outlineColor),
                            () ->  new Color(config.huds.armorStack.outlineColor),
                            value -> config.huds.armorStack.outlineColor = value.getRGB()

                    )
                    .build();
            var mode = Option.<DrawMode>createBuilder()
                    .name(key("armorStack.mode"))
                    .description(keyD("armorStack.mode"))
                    .controller(SBTConfig::generateDrawModeController)
                    .binding(
                            defaults.huds.armorStack.mode,
                            () -> config.huds.armorStack.mode,
                            value -> {
                                config.huds.armorStack.mode = value;
                                outline.setAvailable(value == DrawMode.OUTLINE);
                            }
                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(key("armorStack.scale"))
                    .description(keyD("armorStack.scale"))
                    .controller(SBTConfig::generateScaleController)
                    .binding(
                            defaults.huds.armorStack.scale,
                            () -> config.huds.armorStack.scale,
                            value -> config.huds.armorStack.scale = value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(key("armorStack"))
                    .description(keyD("armorStack"))
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

