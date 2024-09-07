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
import wtf.cheeze.sbt.utils.NumberUtils;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.utils.hud.HudInformation;
import wtf.cheeze.sbt.utils.hud.TextHUD;
import wtf.cheeze.sbt.utils.hud.HudLine;

import java.awt.Color;

public class DefenseHUD extends TextHUD {

    public DefenseHUD() {
        INFO = new HudInformation(
                () -> SkyblockTweaks.CONFIG.config.huds.defense.x,
                () -> SkyblockTweaks.CONFIG.config.huds.defense.y,
                () -> SkyblockTweaks.CONFIG.config.huds.defense.scale,
                () -> SkyblockTweaks.CONFIG.config.huds.defense.anchor,
                x -> SkyblockTweaks.CONFIG.config.huds.defense.x = (float) x,
                y -> SkyblockTweaks.CONFIG.config.huds.defense.y = (float) y,
                scale -> SkyblockTweaks.CONFIG.config.huds.defense.scale = (float) scale,
                anchor -> SkyblockTweaks.CONFIG.config.huds.defense.anchor = anchor
        );

        line = new HudLine(
                () -> SkyblockTweaks.CONFIG.config.huds.defense.color,
                () -> SkyblockTweaks.CONFIG.config.huds.defense.outlineColor,
                () -> SkyblockTweaks.CONFIG.config.huds.defense.mode,
                () -> Text.literal(NumberUtils.formatNumber(SkyblockTweaks.DATA.defense, SkyblockTweaks.CONFIG.config.huds.defense.separator)+ (SkyblockTweaks.CONFIG.config.huds.defense.icon ? "❈" : ""))
        );
    }
    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        if ((SkyblockTweaks.DATA.inSB && SkyblockTweaks.CONFIG.config.huds.defense.enabled) || fromHudScreen) return true;
        return false;
    }



    @Override
    public String getName() {
        return TextUtils.SECTION + "aDefense HUD";
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
                    .name(Text.literal("Enable Defense HUD"))
                    .description(OptionDescription.of(Text.literal("Enables the Defense HUD")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.defense.enabled,
                            () -> config.huds.defense.enabled,
                            value -> config.huds.defense.enabled = (Boolean) value
                    )
                    .build();
            var color = Option.<Color>createBuilder()
                    .name(Text.literal("Defense HUD Color"))
                    .description(OptionDescription.of(Text.literal("The color of the Defense HUD")))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.defense.color),
                            () ->  new Color(config.huds.defense.color),
                            value -> config.huds.defense.color = value.getRGB()

                    )
                    .build();
            var outline = Option.<Color>createBuilder()
                    .name(Text.literal("Defense HUD Outline Color"))
                    .description(OptionDescription.of(Text.literal("The outline color of the Defense HUD")))
                    .controller(ColorControllerBuilder::create)
                    .available(config.huds.defense.mode == HudLine.DrawMode.OUTLINE)
                    .binding(
                            new Color(defaults.huds.defense.outlineColor),
                            () ->  new Color(config.huds.defense.outlineColor),
                            value -> config.huds.defense.outlineColor = value.getRGB()



                    )
                    .build();
            var mode = Option.<HudLine.DrawMode>createBuilder()
                    .name(Text.literal("Defense HUD Mode"))
                    .description(OptionDescription.of(Text.literal("The draw mode of the Defense HUD. Pure will render without shadow, Shadow will render with a shadow, and Outline will render with an outline\n§4Warning: §cOutline mode is still a work in progress and can cause annoying visual bugs in menus.")))
                    .controller(SkyblockTweaksConfig::generateDrawModeController)
                    .binding(
                            defaults.huds.defense.mode,
                            () -> config.huds.defense.mode,
                            value -> {
                                config.huds.defense.mode = value;
                                if (value == HudLine.DrawMode.OUTLINE) outline.setAvailable(true);
                                else outline.setAvailable(false);
                            }
                    )
                    .build();
            var icon = Option.<Boolean>createBuilder()
                    .name(Text.literal("Defense HUD Icon"))
                    .description(OptionDescription.of(Text.literal("Enables the icon (❈) in the Defense HUD")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.defense.icon,
                            () -> config.huds.defense.icon,
                            value -> config.huds.defense.icon = (Boolean) value
                    )
                    .build();
            var separator = Option.<String>createBuilder()
                    .name(Text.literal("Defense HUD Separator"))
                    .description(OptionDescription.of(Text.literal("The separator for the Defense HUD")))
                    .controller(StringControllerBuilder::create)
                    .binding(
                            defaults.huds.defense.separator,
                            () -> config.huds.defense.separator,
                            value -> config.huds.defense.separator = value
                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(Text.literal("Defense HUD Scale"))
                    .description(OptionDescription.of(Text.literal("The scale of the Defense HUD")))
                    .controller(SkyblockTweaksConfig::generateScaleController)
                    .binding(
                            defaults.huds.defense.scale,
                            () -> config.huds.defense.scale,
                            value -> config.huds.defense.scale = value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(Text.literal("Defense HUD"))
                    .description(OptionDescription.of(Text.literal("Settings for the Defense HUD")))
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


