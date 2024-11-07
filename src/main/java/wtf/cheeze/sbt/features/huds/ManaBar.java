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
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.hud.utils.AnchorPoint;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.hud.bases.BarHUD;
import wtf.cheeze.sbt.hud.utils.HudInformation;
import wtf.cheeze.sbt.utils.render.Colors;

import java.awt.Color;

public class ManaBar extends BarHUD {

    public ManaBar() {
        INFO = new HudInformation(
                () -> SBTConfig.huds().manaBar.x,
                () -> SBTConfig.huds().manaBar.y,
                () -> SBTConfig.huds().manaBar.scale,
                () -> SBTConfig.huds().manaBar.anchor,
                () -> SBTConfig.huds().manaBar.color,
                () -> SkyblockTweaks.DATA.mana / SkyblockTweaks.DATA.maxMana,
                x -> SBTConfig.huds().manaBar.x = (float) x,
                y -> SBTConfig.huds().manaBar.y = (float) y,
                scale -> SBTConfig.huds().manaBar.scale = (float) scale,
                anchor -> SBTConfig.huds().manaBar.anchor = anchor
        );
    }

    @Override
    public String getName() {
        return TextUtils.SECTION +  "9Mana Bar";
    }

    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        if ((SkyblockTweaks.DATA.inSB && SBTConfig.huds().manaBar.enabled) || fromHudScreen) return true;
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
        public int color = Colors.BLUE;

        @SerialEntry
        public AnchorPoint anchor = AnchorPoint.LEFT;


        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(key("manaBar.enabled"))
                    .description(keyD("manaBar.enabled"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.manaBar.enabled,
                            () -> config.huds.manaBar.enabled,
                            value -> config.huds.manaBar.enabled = (Boolean) value
                    )
                    .build();

            var color = Option.<Color>createBuilder()
                    .name(key("manaBar.color"))
                    .description(keyD("manaBar.color"))
                    .controller(ColorControllerBuilder::create)
                    .binding(
                            new Color(defaults.huds.manaBar.color),
                            () ->  new Color(config.huds.manaBar.color),
                            value -> config.huds.manaBar.color = value.getRGB()

                    )
                    .build();
            var scale = Option.<Float>createBuilder()
                    .name(key("manaBar.scale"))
                    .description(keyD("manaBar.scale"))
                    .controller(SBTConfig::generateScaleController)
                    .binding(
                            defaults.huds.manaBar.scale,
                            () -> config.huds.manaBar.scale,
                            value -> config.huds.manaBar.scale = value
                    )
                    .build();
            return OptionGroup.createBuilder()
                    .name(key("manaBar"))
                    .description(keyD("manaBar"))
                    .option(enabled)
                    .option(color)
                    .option(scale)
                    .collapsed(true)
                    .build();
        }
    }
}
