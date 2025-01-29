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
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.hud.bases.TextHUD;
import wtf.cheeze.sbt.hud.components.SingleHudLine;
import wtf.cheeze.sbt.hud.utils.AnchorPoint;
import wtf.cheeze.sbt.hud.utils.DrawMode;
import wtf.cheeze.sbt.hud.utils.HudInformation;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.skyblock.IconDict;
import wtf.cheeze.sbt.utils.skyblock.SkyblockUtils;

import java.awt.*;

public class QuiverHUD extends TextHUD {



    public QuiverHUD() {
        INFO = new HudInformation(
                () -> SBTConfig.huds().quiver.x,
                () -> SBTConfig.huds().quiver.y,
                () -> SBTConfig.huds().quiver.scale,
                () -> SBTConfig.huds().quiver.anchor,
                x -> SBTConfig.huds().quiver.x = x,
                y -> SBTConfig.huds().quiver.y = y,
                scale -> SBTConfig.huds().quiver.scale = scale,
                anchor -> SBTConfig.huds().quiver.anchor = anchor
        );
        line = new SingleHudLine(
                () -> Colors.WHITE,
                () -> SBTConfig.huds().quiver.outlineColor,
                () -> SBTConfig.huds().quiver.mode,
                () -> {
                    var quiver = SkyblockUtils.getQuiverData();
                    return TextUtils.join(
                            quiver.arrowName,
                            TextUtils.SPACE,
                            TextUtils.withColor("x" + quiver.arrowCount, Colors.GRAY)
                    );
                },
                () -> IconDict.ARROW,
                () -> SBTConfig.huds().quiver.icon
        );

    }

    @Override
    public Text getName() {
        return Text.literal("Quiver Hud");
    }

    @Override
    public boolean shouldRender(boolean fromHudScreen) {
        if (!super.shouldRender(fromHudScreen)) return false;
        return (SkyblockTweaks.DATA.inSB && SBTConfig.huds().quiver.enabled && SkyblockUtils.quiverActive()) || fromHudScreen;
    }


    public static class Config {
        @SerialEntry
        public boolean enabled = false;

        @SerialEntry
        public DrawMode mode = DrawMode.SHADOW;

        @SerialEntry // Not handled by YACL Gui
        public float x = 0;

        @SerialEntry // Not handled by YACL Gui
        public float y = 0.9f;

        @SerialEntry
        public float scale = 1.0f;

        @SerialEntry
        public int outlineColor = Colors.BLACK;

        @SerialEntry
        public AnchorPoint anchor = AnchorPoint.LEFT;

        @SerialEntry
        public boolean icon = true;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(key("quiver.enabled"))
                    .description(keyD("quiver.enabled"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.quiver.enabled,
                            () -> config.huds.quiver.enabled,
                            value -> config.huds.quiver.enabled = (boolean) value
                    )
                    .build();
            var icon = Option.<Boolean>createBuilder()
                    .name(key("quiver.icon"))
                    .description(keyD("quiver.icon"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.huds.quiver.icon,
                            () -> config.huds.quiver.icon,
                            value -> config.huds.quiver.icon = (boolean) value
                    )
                    .build();



            var outline = Option.<Color>createBuilder()
                    .name(key("quiver.outlineColor"))
                    .description(keyD("quiver.outlineColor"))
                    .controller(ColorControllerBuilder::create)
                    .available(config.huds.quiver.mode == DrawMode.OUTLINE)
                    .binding(
                            new Color(defaults.huds.quiver.outlineColor),
                            () ->  new Color(config.huds.quiver.outlineColor),
                            value -> config.huds.quiver.outlineColor = value.getRGB()

                    )
                    .build();

            var mode = Option.<DrawMode>createBuilder()
                    .name(key("quiver.mode"))
                    .description(keyD("quiver.mode"))
                    .controller(SBTConfig::generateDrawModeController)
                    .binding(
                            defaults.huds.quiver.mode,
                            () -> config.huds.quiver.mode,
                            value -> {
                                config.huds.quiver.mode = value;
                                outline.setAvailable(value == DrawMode.OUTLINE);
                            }
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(key("quiver"))
                    .description(keyD("quiver"))
                    .option(enabled)
                    .option(icon)
                    .option(outline)
                    .option(mode)
                    .collapsed(true)
                    .build();
        }
    }

}
