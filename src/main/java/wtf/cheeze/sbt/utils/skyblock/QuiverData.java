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

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.regex.Pattern;

public class QuiverData {
    private static final Pattern QUIVER_PATTERN = Pattern.compile("Active Arrow: (.+) \\((\\d+)\\)");

    public final Text arrowName;
    public final int arrowCount;


    public static QuiverData DEFAULT = new QuiverData(Text.of("Placeholder Arrow"), 0);

   QuiverData (ItemStack stack) {
        var lines = stack.getOrDefault(DataComponentTypes.LORE, LoreComponent.DEFAULT).lines();
        if (lines.size() < 5) {
            arrowCount = 0;
            arrowName = Text.of("Placeholder Arrow");
            return;
        }
        var line = lines.get(4);
        var matcher = QUIVER_PATTERN.matcher(line.getString());
        if (matcher.matches()) {
            arrowCount = Integer.parseInt(matcher.group(2));
            var type = line.getSiblings().get(1);
            arrowName = Text.literal(type.getString().trim()).setStyle(type.getStyle());
        } else {
            arrowCount = 0;
            arrowName = Text.of("Placeholder Arrow");
        }
    }


    private QuiverData(Text arrowName, int arrowCount) {
        this.arrowName = arrowName;
        this.arrowCount = arrowCount;
    }
}
