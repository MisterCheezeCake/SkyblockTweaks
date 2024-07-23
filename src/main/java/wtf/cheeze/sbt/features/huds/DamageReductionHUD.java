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
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.SkyBlockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SkyBlockTweaksConfig;
import wtf.cheeze.sbt.utils.HudLine;
import wtf.cheeze.sbt.utils.NumberUtils;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.utils.hud.HudInformation;
import wtf.cheeze.sbt.utils.hud.TextHUD;

import java.awt.Color;

public class DamageReductionHUD extends TextHUD {

    public DamageReductionHUD() {
        INFO = new HudInformation(
                () -> SkyBlockTweaks.CONFIG.config.huds.dr.x,
                () -> SkyBlockTweaks.CONFIG.config.huds.dr.y,
                () -> SkyBlockTweaks.CONFIG.config.huds.dr.scale,
                x -> SkyBlockTweaks.CONFIG.config.huds.dr.x = (float) x,
                y -> SkyBlockTweaks.CONFIG.config.huds.dr.y = (float) y,
                scale -> SkyBlockTweaks.CONFIG.config.huds.dr.scale = (float) scale
        );
        line = new HudLine(
                () -> SkyBlockTweaks.CONFIG.config.huds.dr.color,
                () -> SkyBlockTweaks.CONFIG.config.huds.dr.outlineColor,
                () -> SkyBlockTweaks.CONFIG.config.huds.dr.mode,
                () -> NumberUtils.round(SkyBlockTweaks.DATA.damageReduction(), 1) + "%"
        );

    }
    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        if ((SkyBlockTweaks.DATA.inSB && SkyBlockTweaks.CONFIG.config.huds.dr.enabled) || fromHudScreen) return true;
        return false;
    }

    @Override
    public String getName() {
        return TextUtils.SECTION + "aDamage Reduction Percentage HUD";
    }

    public static class Config {

        @SerialEntry
        public boolean enabled = false;

        @SerialEntry
        public HudLine.DrawMode mode = HudLine.DrawMode.SHADOW;

        @SerialEntry
        public int color = 5635925;

        @SerialEntry
        public int outlineColor = 0x000000;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0;

        @SerialEntry
        public float scale = 1.0f;



        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(Text.literal("Enable Damage Reduction HUD"))
                    .description(OptionDescription.of(Text.literal("Enables the Damage Reduction HUD")))
                    .controller(SkyBlockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.dr.enabled,
                            () -> config.huds.dr.enabled,
                            value -> config.huds.dr.enabled = (Boolean) value
                    )
                    .build();
            var color = Option.<Color>createBuilder()
                    .name(Text.literal("Damage Reduction HUD Color"))
                    .description(OptionDescription.of(Text.literal("The color of the Damage Reduction HUD")))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.dr.color),
                            () ->  new Color(config.huds.dr.color),
                            value -> config.huds.dr.color = value.getRGB()

                    )
                    .build();
            var outline = Option.<Color>createBuilder()
                    .name(Text.literal("Damage Reduction HUD Outline Color"))
                    .description(OptionDescription.of(Text.literal("The outline color of the Damage Reduction HUD")))
                    .controller(ColorControllerBuilder::create)
                    .available(config.huds.dr.mode == HudLine.DrawMode.OUTLINE)
                    .binding(
                            new Color(defaults.huds.dr.outlineColor),
                            () ->  new Color(config.huds.dr.outlineColor),
                            value -> config.huds.dr.outlineColor = value.getRGB()

                    )
                    .build();
            var mode = Option.<HudLine.DrawMode>createBuilder()
                    .name(Text.literal("Damage Reduction HUD Mode"))
                    .description(OptionDescription.of(Text.literal("The draw mode of the Damage Reduction HUD. Pure will render without shadow, Shadow will render with a shadow, and Outline will render with an outline\n§4Warning: §cOutline mode is still a work in progress and can cause annoying visual bugs in menus.")))
                    .controller(SkyBlockTweaksConfig::generateDrawModeController)
                    .binding(
                            defaults.huds.dr.mode,
                            () -> config.huds.dr.mode,
                            value -> {
                                config.huds.dr.mode = value;
                                if (value == HudLine.DrawMode.OUTLINE) outline.setAvailable(true);
                                else outline.setAvailable(false);
                            }
                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(Text.literal("Damage Reduction HUD Scale"))
                    .description(OptionDescription.of(Text.literal("The scale of the Damage Reduction HUD")))
                    .controller(SkyBlockTweaksConfig::generateScaleController)
                    .binding(
                            defaults.huds.dr.scale,
                            () -> config.huds.dr.scale,
                            value -> config.huds.dr.scale = value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(Text.literal("Damage Reduction HUD"))
                    .description(OptionDescription.of(Text.literal("Settings for the Damage Reduction HUD")))
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
