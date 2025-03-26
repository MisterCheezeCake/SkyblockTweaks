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
import org.jetbrains.annotations.NotNull;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.hud.bases.TextHud;
import wtf.cheeze.sbt.hud.utils.AnchorPoint;
import wtf.cheeze.sbt.hud.utils.DrawMode;
import wtf.cheeze.sbt.hud.components.SingleHudLine;
import wtf.cheeze.sbt.hud.utils.HudName;
import wtf.cheeze.sbt.utils.NumberUtils;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.hud.utils.HudInformation;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.skyblock.SkyblockData;

import java.awt.Color;

public class DamageReductionHud extends TextHud {

    public static final DamageReductionHud INSTANCE = new DamageReductionHud();

    private DamageReductionHud() {
        INFO = new HudInformation(
                () -> SBTConfig.huds().dr.x,
                () -> SBTConfig.huds().dr.y,
                () -> SBTConfig.huds().dr.scale,
                () -> SBTConfig.huds().dr.anchor,
                x -> SBTConfig.huds().dr.x = x,
                y -> SBTConfig.huds().dr.y = y,
                scale -> SBTConfig.huds().dr.scale = scale,
                anchor -> SBTConfig.huds().dr.anchor = anchor
        );
        line = new SingleHudLine(
                () -> SBTConfig.huds().dr.color,
                () -> SBTConfig.huds().dr.outlineColor,
                () -> SBTConfig.huds().dr.mode,
                () -> Text.literal(NumberUtils.round(SkyblockData.Stats.damageReduction(), 1) + "%")
        );

    }
    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        return (SkyblockData.inSB && SBTConfig.huds().dr.enabled) || fromHudScreen;
    }

    @Override
    public @NotNull HudName getName() {
        return new HudName("Damage Reduction Percentage HUD", "DR % HUD",Colors.LIME);
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
        public float y = 0;

        @SerialEntry
        public AnchorPoint anchor = AnchorPoint.LEFT;

        @SerialEntry
        public float scale = 1.0f;



        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(key("dr.enabled"))
                    .description(keyD("dr.enabled"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.dr.enabled,
                            () -> config.huds.dr.enabled,
                            value -> config.huds.dr.enabled = (boolean) value
                    )
                    .build();
            var color = Option.<Color>createBuilder()
                    .name(key("dr.color"))
                    .description(keyD("dr.color"))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.dr.color),
                            () ->  new Color(config.huds.dr.color),
                            value -> config.huds.dr.color = value.getRGB()

                    )
                    .build();
            var outline = Option.<Color>createBuilder()
                    .name(key("dr.outlineColor"))
                    .description(keyD("dr.outlineColor"))
                    .controller(ColorControllerBuilder::create)
                    .available(config.huds.dr.mode == DrawMode.OUTLINE)
                    .binding(
                            new Color(defaults.huds.dr.outlineColor),
                            () ->  new Color(config.huds.dr.outlineColor),
                            value -> config.huds.dr.outlineColor = value.getRGB()

                    )
                    .build();
            var mode = Option.<DrawMode>createBuilder()
                    .name(key("dr.mode"))
                    .description(keyD("dr.mode"))
                    .controller(SBTConfig::generateDrawModeController)
                    .binding(
                            defaults.huds.dr.mode,
                            () -> config.huds.dr.mode,
                            value -> {
                                config.huds.dr.mode = value;
                                outline.setAvailable(value == DrawMode.OUTLINE);
                            }
                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(key("dr.scale"))
                    .description(keyD("dr.scale"))
                    .controller(SBTConfig::generateScaleController)
                    .binding(
                            defaults.huds.dr.scale,
                            () -> config.huds.dr.scale,
                            value -> config.huds.dr.scale = value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(key("dr"))
                    .description(keyD("dr"))
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
