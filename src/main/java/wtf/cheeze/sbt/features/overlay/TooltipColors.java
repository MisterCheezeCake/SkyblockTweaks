/*
 * Copyright (C) 2026 MisterCheezeCake
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
package wtf.cheeze.sbt.features.overlay;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.config.categories.Overlays;
import wtf.cheeze.sbt.utils.enums.Rarity;
import wtf.cheeze.sbt.utils.skyblock.ItemUtils;

public class TooltipColors {


    public static Identifier getReplacementFrame(ItemStack item) {
        Rarity rarity = ItemUtils.getRarity(item);
        if (rarity == null) return null;
        return Identifier.fromNamespaceAndPath("skyblocktweaks", rarity.tooltipId);
    }


    public static class Config {
        @SerialEntry
        public boolean enabled = false;

        public static OptionGroup getGroup(ConfigImpl defaults, ConfigImpl config) {
            var enabled = Option.<Boolean>createBuilder()
                    .name(Overlays.key("tooltipColors.enabled"))
                    .description(Overlays.keyD("tooltipColors.enabled"))
                    .controller(SBTConfig::generateBooleanController)
                    .binding(
                            defaults.tooltipColors.enabled,
                            () -> config.tooltipColors.enabled,
                            value -> config.tooltipColors.enabled = value
                    )
                    .build();

            return OptionGroup.createBuilder()
                    .name(Overlays.key("tooltipColors"))
                    .description(Overlays.keyD("tooltipColors"))
                    .option(enabled)
                    .build();


        }
    }

}
