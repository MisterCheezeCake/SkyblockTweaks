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
package wtf.cheeze.sbt.utils.skyblock;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;

import java.util.regex.Pattern;

public class QuiverData {
    private static final Pattern QUIVER_PATTERN = Pattern.compile("Active Arrow: (.+) \\((\\d+)\\)");

    public final Component arrowName;
    public final int arrowCount;

    public static QuiverData DEFAULT = new QuiverData(Component.nullToEmpty("Placeholder Arrow"), 0);

   QuiverData (ItemStack stack) {
        var lines = stack.getOrDefault(DataComponents.LORE, ItemLore.EMPTY).lines();
        if (lines.size() < 5) {
            arrowCount = 0;
            arrowName = Component.nullToEmpty("Placeholder Arrow");
            return;
        }
        var line = lines.get(4);
        var matcher = QUIVER_PATTERN.matcher(line.getString());
        if (matcher.matches()) {
            arrowCount = Integer.parseInt(matcher.group(2));
            var type = line.getSiblings().get(1);
            arrowName = Component.literal(type.getString().trim()).setStyle(type.getStyle());
        } else {
            arrowCount = 0;
            arrowName = Component.nullToEmpty("Placeholder Arrow");
        }
    }

    private QuiverData(Component arrowName, int arrowCount) {
        this.arrowName = arrowName;
        this.arrowCount = arrowCount;
    }
}
