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
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SkyblockTweaksConfig;
import wtf.cheeze.sbt.utils.hud.HudLine;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.utils.hud.HudInformation;
import wtf.cheeze.sbt.utils.hud.TextHUD;

import java.awt.Color;

public class HealthHUD extends TextHUD {

    public HealthHUD() {
        INFO = new HudInformation(
                () -> SkyblockTweaks.CONFIG.config.huds.health.x,
                () -> SkyblockTweaks.CONFIG.config.huds.health.y,
                () -> SkyblockTweaks.CONFIG.config.huds.health.scale,
                x -> SkyblockTweaks.CONFIG.config.huds.health.x = (float) x,
                y -> SkyblockTweaks.CONFIG.config.huds.health.y = (float) y,
                scale -> SkyblockTweaks.CONFIG.config.huds.health.scale = (float) scale
        );
        line = new HudLine(
                () -> SkyblockTweaks.DATA.health > SkyblockTweaks.DATA.maxHealth ? SkyblockTweaks.CONFIG.config.huds.health.colorAbsorption : SkyblockTweaks.CONFIG.config.huds.health.color,
                () -> SkyblockTweaks.CONFIG.config.huds.health.outlineColor,
                () -> SkyblockTweaks.CONFIG.config.huds.health.mode,
                () -> TextUtils.formatNumber((int) SkyblockTweaks.DATA.health, SkyblockTweaks.CONFIG.config.huds.health.separator) + "/" + TextUtils.formatNumber((int) SkyblockTweaks.DATA.maxHealth, SkyblockTweaks.CONFIG.config.huds.health.separator) + (SkyblockTweaks.CONFIG.config.huds.health.icon ? "❤" : "")
        );
    }
    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        if ((SkyblockTweaks.DATA.inSB && SkyblockTweaks.CONFIG.config.huds.health.enabled) || fromHudScreen) return true;
        return false;
    }

    @Override
    public String getName() {
        return TextUtils.SECTION +  "cHealth HUD";
    }

    public static class Config {
        @SerialEntry
        public boolean enabled = false;

        @SerialEntry
        public HudLine.DrawMode mode = HudLine.DrawMode.SHADOW;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.20f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public int color = 16733525;

        @SerialEntry
        public int colorAbsorption = 16755200;

        @SerialEntry
        public int outlineColor = 0x000000;

        @SerialEntry
        public boolean icon = true;

        @SerialEntry
        public String separator = ",";

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(Text.literal("Enable Health HUD"))
                    .description(OptionDescription.of(Text.literal("Enables the Health HUD")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.health.enabled,
                            () -> config.huds.health.enabled,
                            value -> config.huds.health.enabled = (Boolean) value
                    )
                    .build();
            var color = Option.<Color>createBuilder()
                    .name(Text.literal("Health HUD Color"))
                    .description(OptionDescription.of(Text.literal("The color of the Health HUD")))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.health.color),
                            () ->  new Color(config.huds.health.color),
                            value -> config.huds.health.color = value.getRGB()

                    )
                    .build();
            var colorAbsorption = Option.<Color>createBuilder()
                    .name(Text.literal("Health HUD Absorption Color"))
                    .description(OptionDescription.of(Text.literal("The color of the Health HUD when you have absorption")))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.health.colorAbsorption),
                            () ->  new Color(config.huds.health.colorAbsorption),
                            value -> config.huds.health.colorAbsorption = value.getRGB()

                    )
                    .build();
            var outline = Option.<Color>createBuilder()
                    .name(Text.literal("Health HUD Outline Color"))
                    .description(OptionDescription.of(Text.literal("The outline color of the Health HUD")))
                    .controller(ColorControllerBuilder::create)
                    .available(config.huds.health.mode == HudLine.DrawMode.OUTLINE)
                    .binding(
                            new Color(defaults.huds.health.outlineColor),
                            () ->  new Color(config.huds.health.outlineColor),
                            value -> config.huds.health.outlineColor = value.getRGB()

                    )
                    .build();
            var mode = Option.<HudLine.DrawMode>createBuilder()
                    .name(Text.literal("Health HUD Mode"))
                    .description(OptionDescription.of(Text.literal("The draw mode of the Health HUD. Pure will render without shadow, Shadow will render with a shadow, and Outline will render with an outline\n§4Warning: §cOutline mode is still a work in progress and can cause annoying visual bugs in menus.")))
                    .controller(SkyblockTweaksConfig::generateDrawModeController)
                    .binding(
                            defaults.huds.health.mode,
                            () -> config.huds.health.mode,
                            value -> {
                                config.huds.health.mode = value;
                                if (value == HudLine.DrawMode.OUTLINE) outline.setAvailable(true);
                                else outline.setAvailable(false);
                            }
                    )
                    .build();
            var icon = Option.<Boolean>createBuilder()
                    .name(Text.literal("Health HUD Icon"))
                    .description(OptionDescription.of(Text.literal("Enables the icon (❤) in the Health HUD")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.health.icon,
                            () -> config.huds.health.icon,
                            value -> config.huds.health.icon = (Boolean) value
                    )
                    .build();
            var separator = Option.<String>createBuilder()
                    .name(Text.literal("Health HUD Separator"))
                    .description(OptionDescription.of(Text.literal("The separator for the Health HUD")))
                    .controller(StringControllerBuilder::create)
                    .binding(
                            defaults.huds.health.separator,
                            () -> config.huds.health.separator,
                            value -> config.huds.health.separator = value
                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(Text.literal("Health HUD Scale"))
                    .description(OptionDescription.of(Text.literal("The scale of the Health HUD")))
                    .controller(SkyblockTweaksConfig::generateScaleController)
                    .binding(
                            defaults.huds.health.scale,
                            () -> config.huds.health.scale,
                            value -> config.huds.health.scale = value
                    )
                    .build();
            return OptionGroup.createBuilder()
                    .name(Text.literal("Health HUD"))
                    .description(OptionDescription.of(Text.literal("Settings for the Health HUD")))
                    .option(enabled)
                    .option(mode)
                    .option(color)
                    .option(colorAbsorption)
                    .option(outline)
                    .option(icon)
                    .option(separator)
                    .option(scale)
                    .collapsed(true)
                    .build();
        }
    }
}
