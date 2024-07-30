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
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SkyblockTweaksConfig;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.utils.hud.BarHUD;
import wtf.cheeze.sbt.utils.hud.HudInformation;

import java.awt.Color;

public class ManaBar extends BarHUD {

    public ManaBar() {
        INFO = new HudInformation(
                () -> SkyblockTweaks.CONFIG.config.huds.manaBar.x,
                () -> SkyblockTweaks.CONFIG.config.huds.manaBar.y,
                () -> SkyblockTweaks.CONFIG.config.huds.manaBar.scale,
                () -> SkyblockTweaks.CONFIG.config.huds.manaBar.color,
                () -> SkyblockTweaks.DATA.maxMana,
                () -> SkyblockTweaks.DATA.mana,
                x -> SkyblockTweaks.CONFIG.config.huds.manaBar.x = (float) x,
                y -> SkyblockTweaks.CONFIG.config.huds.manaBar.y = (float) y,
                scale -> SkyblockTweaks.CONFIG.config.huds.manaBar.scale = (float) scale

        );
    }

    @Override
    public String getName() {
        return TextUtils.SECTION +  "9Mana Bar";
    }

    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        if ((SkyblockTweaks.DATA.inSB && SkyblockTweaks.CONFIG.config.huds.manaBar.enabled) || fromHudScreen) return true;
        return false;
    }

    public static class Config {
        @SerialEntry
        public boolean enabled = false;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.25f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public int color = 5592575;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(Text.literal("Enable Mana Bar"))
                    .description(OptionDescription.of(Text.literal("Enables the Mana Bar")))
                    .controller(SkyblockTweaksConfig::generateBooleanController)
                    .binding(
                            defaults.huds.manaBar.enabled,
                            () -> config.huds.manaBar.enabled,
                            value -> config.huds.manaBar.enabled = (Boolean) value
                    )
                    .build();

            var color = Option.<Color>createBuilder()
                    .name(Text.literal("Mana Bar Color"))
                    .description(OptionDescription.of(Text.literal("The color of the Mana Bar")))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.manaBar.color),
                            () ->  new Color(config.huds.manaBar.color),
                            value -> config.huds.manaBar.color = value.getRGB()

                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(Text.literal("Mana Bar Scale"))
                    .description(OptionDescription.of(Text.literal("The scale of the Mana Bar")))
                    .controller(SkyblockTweaksConfig::generateScaleController)
                    .binding(
                            defaults.huds.manaBar.scale,
                            () -> config.huds.manaBar.scale,
                            value -> config.huds.manaBar.scale = value
                    )
                    .build();
            return OptionGroup.createBuilder()
                    .name(Text.literal("Mana Bar"))
                    .description(OptionDescription.of(Text.literal("Settings for the Mana Bar")))
                    .option(enabled)
                    .option(color)
                    .option(scale)
                    .collapsed(true)
                    .build();
        }
    }
}
