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
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.hud.utils.AnchorPoint;
import wtf.cheeze.sbt.utils.NumberUtils;
import wtf.cheeze.sbt.hud.utils.DrawMode;
import wtf.cheeze.sbt.hud.components.SingleHudLine;
import wtf.cheeze.sbt.hud.utils.HudInformation;
import wtf.cheeze.sbt.hud.bases.TextHud;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.skyblock.SkyblockData;

import java.awt.Color;

public class CoordinatesHud extends TextHud {

    public CoordinatesHud() {
        INFO = new HudInformation(
                () -> SBTConfig.huds().coordinates.x,
                () -> SBTConfig.huds().coordinates.y,
                () -> SBTConfig.huds().coordinates.scale,
                () -> SBTConfig.huds().coordinates.anchor,
                x -> SBTConfig.huds().coordinates.x = x,
                y -> SBTConfig.huds().coordinates.y = y,
                scale -> SBTConfig.huds().coordinates.scale = scale,
                anchor -> SBTConfig.huds().coordinates.anchor = anchor
        );
        line = new SingleHudLine(
                () -> SBTConfig.huds().coordinates.color,
                () -> SBTConfig.huds().coordinates.outlineColor,
                () -> SBTConfig.huds().coordinates.mode,
                () -> Text.literal(String.format("X: %s Y: %s Z: %s" , NumberUtils.formattedRound(client.player.getX(), SBTConfig.huds().coordinates.decimalPlaces), NumberUtils.formattedRound(client.player.getY(), SBTConfig.huds().coordinates.decimalPlaces), NumberUtils.formattedRound(client.player.getZ(), SBTConfig.huds().coordinates.decimalPlaces)))
        );
    }
    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        return ((SkyblockData.inSB || SBTConfig.huds().coordinates.showOutside) && SBTConfig.huds().coordinates.enabled) || fromHudScreen;
    }

    @Override
    public Text getName() {
        return Text.literal("Coordinates HUD");
    }

    public static class Config {
        @SerialEntry
        public boolean enabled = false;

        @SerialEntry
        public boolean showOutside = false;

        @SerialEntry
        public int decimalPlaces = 0;

        @SerialEntry
        public DrawMode mode = DrawMode.SHADOW;

        @SerialEntry
        public int color = Colors.WHITE;

        @SerialEntry
        public int outlineColor = Colors.BLACK;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.60f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public AnchorPoint anchor = AnchorPoint.LEFT;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(key("coordinates.enabled"))
                    .description(keyD("coordinates.enabled"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.coordinates.enabled,
                            () -> config.huds.coordinates.enabled,
                            value -> config.huds.coordinates.enabled = (boolean) value
                    )
                    .build();

            var outside = Option.<Boolean>createBuilder()
                    .name(key("coordinates.showOutside"))
                    .description(keyD("coordinates.showOutside"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.coordinates.showOutside,
                            () -> config.huds.coordinates.showOutside,
                            value -> config.huds.coordinates.showOutside = (boolean) value
                    )
                    .build();
            var decimalPlaces = Option.<Integer>createBuilder()
                    .name(key("coordinates.decimalPlaces"))
                    .description(keyD("coordinates.decimalPlaces"))
                    .controller(
                            opt -> IntegerSliderControllerBuilder.create(opt)
                                    .step(1)
                                    .range(0, 4)

                    )
                    .binding(
                            defaults.huds.coordinates.decimalPlaces,
                            () -> config.huds.coordinates.decimalPlaces,
                            value -> config.huds.coordinates.decimalPlaces = value
                    )
                    .build();

            var color = Option.<Color>createBuilder()
                    .name(key("coordinates.color"))
                    .description(keyD("coordinates.color"))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.coordinates.color),
                            () ->  new Color(config.huds.coordinates.color),
                            value -> config.huds.coordinates.color = value.getRGB()

                    )
                    .build();
            var outline = Option.<Color>createBuilder()
                    .name(key("coordinates.outlineColor"))
                    .description(keyD("coordinates.outlineColor"))
                    .controller(ColorControllerBuilder::create)
                    .available(config.huds.coordinates.mode == DrawMode.OUTLINE)
                    .binding(
                            new Color(defaults.huds.coordinates.outlineColor),
                            () ->  new Color(config.huds.coordinates.outlineColor),
                            value -> config.huds.coordinates.outlineColor = value.getRGB()

                    )
                    .build();
            var mode = Option.<DrawMode>createBuilder()
                    .name(key("coordinates.mode"))
                    .description(keyD("coordinates.mode"))
                    .controller(SBTConfig::generateDrawModeController)
                    .binding(
                            defaults.huds.coordinates.mode,
                            () -> config.huds.coordinates.mode,
                            value -> {
                                config.huds.coordinates.mode = value;
                                outline.setAvailable(value == DrawMode.OUTLINE);
                            }
                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(key("coordinates.scale"))
                    .description(keyD("coordinates.scale"))
                    .controller(SBTConfig::generateScaleController)
                    .binding(
                            defaults.huds.coordinates.scale,
                            () -> config.huds.coordinates.scale,
                            value -> config.huds.coordinates.scale = value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(key("coordinates"))
                    .description(keyD("coordinates"))
                    .option(enabled)
                    .option(outside)
                    .option(decimalPlaces)
                    .option(mode)
                    .option(color)
                    .option(outline)
                    .option(scale)
                    .collapsed(true)
                    .build();
        }
    }
}
