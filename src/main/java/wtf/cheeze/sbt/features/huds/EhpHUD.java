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

import java.awt.Color;

public class EhpHUD extends TextHUD {

    public EhpHUD() {
        INFO = new HudInformation(
                () -> SBTConfig.huds().ehp.x,
                () -> SBTConfig.huds().ehp.y,
                () -> SBTConfig.huds().ehp.scale,
                () -> SBTConfig.huds().ehp.anchor,
                x -> SBTConfig.huds().ehp.x = (float) x,
                y -> SBTConfig.huds().ehp.y = (float) y,
                scale -> SBTConfig.huds().ehp.scale = (float) scale,
                anchor -> SBTConfig.huds().ehp.anchor = anchor
        );
        line = new HudLine(
                () -> SBTConfig.huds().ehp.color,
                () -> SBTConfig.huds().ehp.outlineColor,
                () -> SBTConfig.huds().ehp.mode,
                () -> Text.literal(NumberUtils.formatNumber((int) SkyblockTweaks.DATA.effectiveHealth(), SBTConfig.huds().ehp.separator) + (SBTConfig.huds().ehp.icon ? "❤" : ""))
        );

    }
    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        if ((SkyblockTweaks.DATA.inSB && SBTConfig.huds().ehp.enabled) || fromHudScreen) return true;
        return false;
    }

    @Override
    public String getName() {
        return TextUtils.SECTION + "2Effective Health HUD";
    }

    public static class Config {
        @SerialEntry
        public boolean enabled = false;

        @SerialEntry
        public HudLine.DrawMode mode = HudLine.DrawMode.SHADOW;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.10f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public int color = 43520;

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
                    .name(Text.literal("Enable Effective Health HUD"))
                    .description(OptionDescription.of(Text.literal("Enables the Effective Health HUD")))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.ehp.enabled,
                            () -> config.huds.ehp.enabled,
                            value -> config.huds.ehp.enabled = (Boolean) value
                    )
                    .build();
            var outline = Option.<Color>createBuilder()
                    .name(Text.literal("Effective Health HUD Outline Color"))
                    .description(OptionDescription.of(Text.literal("The outline color of the Effective Health HUD")))
                    .controller(ColorControllerBuilder::create)
                    .available(config.huds.ehp.mode == HudLine.DrawMode.OUTLINE)
                    .binding(
                            new Color(defaults.huds.ehp.outlineColor),
                            () ->  new Color(config.huds.ehp.outlineColor),
                            value -> config.huds.ehp.outlineColor = value.getRGB()



                    )
                    .build();
            var mode = Option.<HudLine.DrawMode>createBuilder()
                    .name(Text.literal("Effective Health HUD Mode"))
                    .description(OptionDescription.of(Text.literal("The draw mode of the Effective Health HUD. Pure will render without shadow, Shadow will render with a shadow, and Outline will render with an outline\n§4Warning: §cOutline mode is still a work in progress and can cause annoying visual bugs in menus.")))
                    .controller(SBTConfig::generateDrawModeController)
                    .binding(
                            defaults.huds.ehp.mode,
                            () -> config.huds.ehp.mode,
                            value -> {
                                config.huds.ehp.mode = value;
                                if (value == HudLine.DrawMode.OUTLINE) outline.setAvailable(true);
                                else outline.setAvailable(false);
                            }
                    )
                    .build();

            var color = Option.<Color>createBuilder()
                    .name(Text.literal("Effective Health HUD Color"))
                    .description(OptionDescription.of(Text.literal("The color of the Effective Health HUD")))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.ehp.color),
                            () ->  new Color(config.huds.ehp.color),
                            value -> config.huds.ehp.color = value.getRGB()

                    )
                    .build();
            var icon = Option.<Boolean>createBuilder()
                    .name(Text.literal("Effective Health HUD Icon"))
                    .description(OptionDescription.of(Text.literal("Enables the icon (❤) in the Effective Health HUD")))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.ehp.icon,
                            () -> config.huds.ehp.icon,
                            value -> config.huds.ehp.icon = (Boolean) value
                    )
                    .build();
            var separator = Option.<String>createBuilder()
                    .name(Text.literal("Effective Health HUD Separator"))
                    .description(OptionDescription.of(Text.literal("The separator for the Effective Health HUD")))
                    .controller(StringControllerBuilder::create)
                    .binding(
                            defaults.huds.ehp.separator,
                            () -> config.huds.ehp.separator,
                            value -> config.huds.ehp.separator = value
                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(Text.literal("Effective Health HUD Scale"))
                    .description(OptionDescription.of(Text.literal("The scale of the Effective Health HUD")))
                    .controller(SBTConfig::generateScaleController)
                    .binding(
                            defaults.huds.ehp.scale,
                            () -> config.huds.ehp.scale,
                            value -> config.huds.ehp.scale = value
                    )
                    .build();


            return OptionGroup.createBuilder()
                    .name(Text.literal("Effective Health HUD"))
                    .description(OptionDescription.of(Text.literal("Settings for the Effective Health HUD")))
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



