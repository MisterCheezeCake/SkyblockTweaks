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
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.utils.NumberUtils;
import wtf.cheeze.sbt.utils.hud.HudLine;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.utils.hud.HudInformation;
import wtf.cheeze.sbt.utils.hud.TextHUD;

import java.awt.*;

public class OverflowManaHUD extends TextHUD {

    public OverflowManaHUD() {
        INFO = new HudInformation(
                () -> SBTConfig.huds().overflowMana.x,
                () -> SBTConfig.huds().overflowMana.y,
                () -> SBTConfig.huds().overflowMana.scale,
                () -> SBTConfig.huds().overflowMana.anchor,
                x -> SBTConfig.huds().overflowMana.x = (float) x,
                y -> SBTConfig.huds().overflowMana.y = (float) y,
                scale -> SBTConfig.huds().overflowMana.scale = (float) scale,
                anchor -> SBTConfig.huds().overflowMana.anchor = anchor
        );
        line = new HudLine(
                () -> SBTConfig.huds().overflowMana.color,
                () -> SBTConfig.huds().overflowMana.outlineColor,
                () -> SBTConfig.huds().overflowMana.mode,
                () -> Text.literal(NumberUtils.formatNumber((int) SkyblockTweaks.DATA.overflowMana, SBTConfig.huds().overflowMana.separator) + (SBTConfig.huds().overflowMana.icon ? "ʬ" : ""))
        );

    }
    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        if ((SkyblockTweaks.DATA.inSB && SBTConfig.huds().overflowMana.enabled && (!SBTConfig.huds().overflowMana.hideWhenZero || SkyblockTweaks.DATA.overflowMana != 0)) || fromHudScreen) return true;
        return false;
    }

    @Override
    public String getName() {
        return TextUtils.SECTION +  "3Oveflow Mana HUD";
    }

    public static class Config {

        @SerialEntry
        public boolean enabled = false;

        @SerialEntry
        public boolean hideWhenZero = true;

        @SerialEntry
        public HudLine.DrawMode mode = HudLine.DrawMode.SHADOW;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.40f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public int color = 43690;

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
                    .name(Text.literal("Enable Overflow Mana HUD"))
                    .description(OptionDescription.of(Text.literal("Enables the Overflow Mana HUD")))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.overflowMana.enabled,
                            () -> config.huds.overflowMana.enabled,
                            value -> config.huds.overflowMana.enabled = (Boolean) value
                    )
                    .build();
            var hideWhenZero = Option.<Boolean>createBuilder()
                    .name(Text.literal("Hide Overflow Mana HUD when Zero"))
                    .description(OptionDescription.of(Text.literal("Hides the Overflow Mana HUD when the value is zero")))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.overflowMana.hideWhenZero,
                            () -> config.huds.overflowMana.hideWhenZero,
                            value -> config.huds.overflowMana.hideWhenZero = (Boolean) value
                    )
                    .build();
            var color = Option.<Color>createBuilder()
                    .name(Text.literal("Overflow Mana HUD Color"))
                    .description(OptionDescription.of(Text.literal("The color of the Overflow Mana HUD")))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.overflowMana.color),
                            () ->  new Color(config.huds.overflowMana.color),
                            value -> config.huds.overflowMana.color = value.getRGB()

                    )
                    .build();
            var outline = Option.<Color>createBuilder()
                    .name(Text.literal("Overflow Mana HUD Outline Color"))
                    .description(OptionDescription.of(Text.literal("The outline color of the Overflow Mana HUD")))
                    .controller(ColorControllerBuilder::create)
                    .available(config.huds.overflowMana.mode == HudLine.DrawMode.OUTLINE)
                    .binding(
                            new Color(defaults.huds.overflowMana.outlineColor),
                            () ->  new Color(config.huds.overflowMana.outlineColor),
                            value -> config.huds.overflowMana.outlineColor = value.getRGB()

                    )
                    .build();
            var mode = Option.<HudLine.DrawMode>createBuilder()
                    .name(Text.literal("Overflow Mana HUD Mode"))
                    .description(OptionDescription.of(Text.literal("The draw mode of the Overflow Mana HUD. Pure will render without shadow, Shadow will render with a shadow, and Outline will render with an outline\n§4Warning: §cOutline mode is still a work in progress and can cause annoying visual bugs in menus.")))
                    .controller(SBTConfig::generateDrawModeController)
                    .binding(
                            defaults.huds.overflowMana.mode,
                            () -> config.huds.overflowMana.mode,
                            value -> {
                                config.huds.overflowMana.mode = value;
                                if (value == HudLine.DrawMode.OUTLINE) outline.setAvailable(true);
                                else outline.setAvailable(false);
                            }
                    )
                    .build();
            var icon = Option.<Boolean>createBuilder()
                    .name(Text.literal("Overflow Mana HUD Icon"))
                    .description(OptionDescription.of(Text.literal("Enables the icon (ʬ) in the Overflow Mana HUD")))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.overflowMana.icon,
                            () -> config.huds.overflowMana.icon,
                            value -> config.huds.overflowMana.icon = (Boolean) value
                    )
                    .build();
            var separator = Option.<String>createBuilder()
                    .name(Text.literal("Overflow Mana HUD Separator"))
                    .description(OptionDescription.of(Text.literal("The separator for the Overflow Mana HUD")))
                    .controller(StringControllerBuilder::create)
                    .binding(
                            defaults.huds.overflowMana.separator,
                            () -> config.huds.overflowMana.separator,
                            value -> config.huds.overflowMana.separator = value
                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(Text.literal("Overflow Mana HUD Scale"))
                    .description(OptionDescription.of(Text.literal("The scale of the Overflow Mana HUD")))
                    .controller(SBTConfig::generateScaleController)
                    .binding(
                            defaults.huds.overflowMana.scale,
                            () -> config.huds.overflowMana.scale,
                            value -> config.huds.overflowMana.scale = value
                    )
                    .build();
            return OptionGroup.createBuilder()
                    .name(Text.literal("Overflow Mana HUD"))
                    .description(OptionDescription.of(Text.literal("Settings for the Overflow Mana HUD")))
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
