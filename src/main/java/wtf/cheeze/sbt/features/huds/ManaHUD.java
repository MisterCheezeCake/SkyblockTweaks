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


import java.awt.*;

public class ManaHUD extends TextHUD {

    public ManaHUD() {
        INFO = new HudInformation(
                () -> SkyblockTweaks.CONFIG.config.huds.mana.x,
                () -> SkyblockTweaks.CONFIG.config.huds.mana.y,
                () -> SkyblockTweaks.CONFIG.config.huds.mana.scale,
                () -> SkyblockTweaks.CONFIG.config.huds.mana.anchor,
                x -> SkyblockTweaks.CONFIG.config.huds.mana.x = (float) x,
                y -> SkyblockTweaks.CONFIG.config.huds.mana.y = (float) y,
                scale -> SkyblockTweaks.CONFIG.config.huds.mana.scale = (float) scale,
                anchor -> SkyblockTweaks.CONFIG.config.huds.mana.anchor = anchor
        );
        line = new HudLine(
                () -> SkyblockTweaks.CONFIG.config.huds.mana.color,
                () -> SkyblockTweaks.CONFIG.config.huds.mana.outlineColor,
                () -> SkyblockTweaks.CONFIG.config.huds.mana.mode,
                () -> Text.literal(NumberUtils.formatNumber((int) SkyblockTweaks.DATA.mana, SkyblockTweaks.CONFIG.config.huds.mana.separator) + "/" + NumberUtils.formatNumber((int) SkyblockTweaks.DATA.maxMana, SkyblockTweaks.CONFIG.config.huds.mana.separator) + (SkyblockTweaks.CONFIG.config.huds.mana.icon ? "✎" : ""))
        );
    }
    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        if ((SkyblockTweaks.DATA.inSB && SkyblockTweaks.CONFIG.config.huds.mana.enabled) || fromHudScreen) return true;
        return false;
    }
//    @Override
//    public String getText() {
//        return TextUtils.formatNumber((int) SkyBlockTweaks.DATA.mana, SkyBlockTweaks.CONFIG.config.huds.mana.separator) + "/" + TextUtils.formatNumber((int) SkyBlockTweaks.DATA.maxMana, SkyBlockTweaks.CONFIG.config.huds.mana.separator) + (SkyBlockTweaks.CONFIG.config.huds.mana.icon ? "✎" : "");
//    }

    @Override
    public String getName() {
        return TextUtils.SECTION +  "9Mana HUD";
    }

    public static class Config {

        @SerialEntry
        public boolean enabled = false;

//        @SerialEntry
//        public boolean shadow = true;

        @SerialEntry
        public HudLine.DrawMode mode = HudLine.DrawMode.SHADOW;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.30f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public int color = 5592575;

        @SerialEntry
        public int outlineColor = 0x000000;

        @SerialEntry
        public boolean icon = true;

        @SerialEntry
        public String separator = ",";

        @SerialEntry
        public AnchorPoint anchor = AnchorPoint.LEFT;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(Text.literal("Enable Mana HUD"))
                    .description(OptionDescription.of(Text.literal("Enables the Mana HUD")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.mana.enabled,
                            () -> config.huds.mana.enabled,
                            value -> config.huds.mana.enabled = (Boolean) value
                    )
                    .build();
            var color = Option.<Color>createBuilder()
                    .name(Text.literal("Mana HUD Color"))
                    .description(OptionDescription.of(Text.literal("The color of the Mana HUD")))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.mana.color),
                            () ->  new Color(config.huds.mana.color),
                            value -> config.huds.mana.color = value.getRGB()

                    )
                    .build();
            var outline = Option.<Color>createBuilder()
                    .name(Text.literal("Mana HUD Outline Color"))
                    .description(OptionDescription.of(Text.literal("The outline color of the Mana HUD")))
                    .controller(ColorControllerBuilder::create)
                    .available(config.huds.mana.mode == HudLine.DrawMode.OUTLINE)
                    .binding(
                            new Color(defaults.huds.mana.outlineColor),
                            () ->  new Color(config.huds.mana.outlineColor),
                            value -> config.huds.mana.outlineColor = value.getRGB()

                    )
                    .build();
            var mode = Option.<HudLine.DrawMode>createBuilder()
                    .name(Text.literal("Mana HUD Mode"))
                    .description(OptionDescription.of(Text.literal("The draw mode of the Mana HUD. Pure will render without shadow, Shadow will render with a shadow, and Outline will render with an outline\n§4Warning: §cOutline mode is still a work in progress and can cause annoying visual bugs in menus.")))
                    .controller(SkyblockTweaksConfig::generateDrawModeController)
                    .binding(
                            defaults.huds.mana.mode,
                            () -> config.huds.mana.mode,
                            value -> {
                                config.huds.mana.mode = value;
                                if (value == HudLine.DrawMode.OUTLINE) outline.setAvailable(true);
                                else outline.setAvailable(false);
                            }
                    )
                    .build();
            var icon = Option.<Boolean>createBuilder()
                    .name(Text.literal("Mana HUD Icon"))
                    .description(OptionDescription.of(Text.literal("Enables the icon (✎) in the Mana HUD")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.mana.icon,
                            () -> config.huds.mana.icon,
                            value -> config.huds.mana.icon = (Boolean) value
                    )
                    .build();
            var separator = Option.<String>createBuilder()
                    .name(Text.literal("Mana HUD Separator"))
                    .description(OptionDescription.of(Text.literal("The separator for the Mana HUD")))
                    .controller(StringControllerBuilder::create)
                    .binding(
                            defaults.huds.mana.separator,
                            () -> config.huds.mana.separator,
                            value -> config.huds.mana.separator = value
                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(Text.literal("Mana HUD Scale"))
                    .description(OptionDescription.of(Text.literal("The scale of the Mana HUD")))
                    .controller(SkyblockTweaksConfig::generateScaleController)
                    .binding(
                            defaults.huds.mana.scale,
                            () -> config.huds.mana.scale,
                            value -> config.huds.mana.scale = value
                    )
                    .build();
            return OptionGroup.createBuilder()
                    .name(Text.literal("Mana HUD"))
                    .description(OptionDescription.of(Text.literal("Settings for the Mana HUD")))
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
