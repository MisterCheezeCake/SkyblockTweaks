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
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.hud.utils.AnchorPoint;
import wtf.cheeze.sbt.hud.utils.DrawMode;
import wtf.cheeze.sbt.hud.components.SingleHudLine;
import wtf.cheeze.sbt.hud.utils.HudInformation;
import wtf.cheeze.sbt.hud.bases.TextHud;
import wtf.cheeze.sbt.hud.utils.HudName;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.skyblock.SkyblockData;

import java.awt.Color;

public class ArmorStackHud extends TextHud {
    public static final ArmorStackHud INSTANCE = new ArmorStackHud();

    private ArmorStackHud() {
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
                () -> SkyblockData.Stats.stackString != null ? Component.literal(SkyblockData.Stats.armorStack + SkyblockData.Stats.stackString) : Component.literal("0ᝐ")
        );
    }

    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        if (SkyblockData.Stats.stackString == null && !fromHudScreen) return false;
        return (SkyblockData.inSB && SBTConfig.huds().armorStack.enabled) || fromHudScreen;
    }

    @Override
    public @NotNull HudName getName() {
        return new HudName("Armor Stack HUD", "Stack HUD", Colors.ORANGE);
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
                            value -> config.huds.armorStack.enabled = value
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
                    .available(config.huds.armorStack.mode.outline)
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
                                outline.setAvailable(value.outline);
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
