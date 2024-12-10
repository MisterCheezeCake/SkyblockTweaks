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

import net.minecraft.util.Identifier;
import wtf.cheeze.sbt.hud.HudIcon;

import java.util.Map;

import wtf.cheeze.sbt.utils.enums.Skills;

public class IconDict {

    public static final HudIcon DEFAULT_ICON = new HudIcon(Identifier.of("skyblocktweaks", "missing.png"));
    public static final Map<Skills, HudIcon> SKILL_ICONS = Map.ofEntries(
            Map.entry(Skills.COMBAT, new HudIcon(ofItem("iron_sword"))),
            Map.entry(Skills.FARMING, new HudIcon(ofItem("golden_hoe"))),
            Map.entry(Skills.MINING, new HudIcon(ofItem("diamond_pickaxe"))),
            Map.entry(Skills.FORAGING, new HudIcon(ofBlock("oak_sapling"))),
            Map.entry(Skills.FISHING, new HudIcon(ofItem("fishing_rod"))),
            Map.entry(Skills.ENCHANTING, new HudIcon(ofBlock("enchanting_table_top"))),
            Map.entry(Skills.ALCHEMY, new HudIcon(ofItem("brewing_stand"))),
            Map.entry(Skills.CARPENTRY, new HudIcon(ofBlock("crafting_table_top"))),
            Map.entry(Skills.RUNECRAFTING, new HudIcon(ofItem("magma_cream"))),
            Map.entry(Skills.SOCIAL, new HudIcon(ofItem("emerald"))
            ));

    public static final HudIcon ARROW = new HudIcon(ofItem("arrow"));

    private static Identifier ofItem(String name) {
        return Identifier.ofVanilla("textures/item/" + name + ".png");
    }

    private static Identifier ofBlock(String name) {
        return Identifier.ofVanilla("textures/block/" + name + ".png");
    }
}