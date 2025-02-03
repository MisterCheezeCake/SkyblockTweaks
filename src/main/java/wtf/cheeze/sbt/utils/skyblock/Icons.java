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

import static java.util.Map.entry;
import static wtf.cheeze.sbt.utils.skyblock.ItemStackUtils.*;

public class Icons {

    public static final HudIcon ARROW = new HudIcon(ofItem("arrow"));
    public static final HudIcon CHEST_MINECART = new HudIcon(ofItem("chest_minecart"));
    public static final HudIcon CHEST = new HudIcon(ItemStackUtils.getVanillaItem("chest"));
    public static final HudIcon FIREWORK = new HudIcon(ofItem("firework_rocket"));
    public static final HudIcon IRON_SWORD = new HudIcon(ofItem("iron_sword"));
    public static final HudIcon DIAMOND_PICKAXE = new HudIcon(ofItem("diamond_pickaxe"));
    public static final HudIcon NETHER_STAR = new HudIcon(ofItem("nether_star"));

    public static final HudIcon DEFAULT_ICON = new HudIcon(Identifier.of("skyblocktweaks", "missing.png"));
    public static final Map<Skills, HudIcon> SKILL_ICONS = Map.ofEntries(
            entry(Skills.COMBAT, IRON_SWORD),
            entry(Skills.FARMING, new HudIcon(ofItem("golden_hoe"))),
            entry(Skills.MINING, DIAMOND_PICKAXE),
            entry(Skills.FORAGING, new HudIcon(ofBlock("oak_sapling"))),
            entry(Skills.FISHING, new HudIcon(ofItem("fishing_rod"))),
            entry(Skills.ENCHANTING, new HudIcon(ofBlock("enchanting_table_top"))),
            entry(Skills.ALCHEMY, new HudIcon(ofItem("brewing_stand"))),
            entry(Skills.CARPENTRY, new HudIcon(ofBlock("crafting_table_top"))),
            entry(Skills.RUNECRAFTING, new HudIcon(ofItem("magma_cream"))),
            entry(Skills.SOCIAL, new HudIcon(ofItem("emerald"))
    ));

    public static final Map<String, HudIcon> MINING_ICONS = Map.ofEntries(
            entry("MITHRIL_POWDER", new HudIcon(getSkyblockItem("lime_dye", "MITHRIL_POWDER"))),
            entry("GEMSTONE_POWDER", new HudIcon(getSkyblockItem("magenta_dye", "GEMSTONE_POWDER"))),
            entry("GLACITE_POWDER", new HudIcon(getSkyblockItem("light_blue_dye", "GLACITE_POWDER"))),
            entry("MITHRIL", new HudIcon(getSkyblockItem("prismarine_crystals", "MITHRIL_ORE"))),
            entry("HARD_STONE", new HudIcon(getSkyblockItem("stone", "HARD_STONE"))),
            entry("RAFFLE", new HudIcon(getSkyblockItem("name_tag", "MINING_RAFFLE_TICKET"))),
            entry("GLACITE", new HudIcon(getSkyblockItem("packed_ice", "GLACITE"))),
            entry("TITANIUM", new HudIcon(getHeadItem("TITANIUM_ORE", "TITANIUM"))),
            entry("RUBY", new HudIcon(getHeadItem("ROUGH_RUBY_GEM", "RUBY_ROUGH"))),
            entry("AMETHYST", new HudIcon(getHeadItem("ROUGH_AMETHYST_GEM", "AMETHYST_ROUGH"))),
            entry("AMBER", new HudIcon(getHeadItem("ROUGH_AMBER_GEM", "AMBER_ROUGH"))),
            entry("JADE", new HudIcon(getHeadItem("ROUGH_JADE_GEM", "JADE_ROUGH"))),
            entry("SAPPHIRE", new HudIcon(getHeadItem("ROUGH_SAPPHIRE_GEM", "SAPPHIRE_ROUGH"))),
            entry("TOPAZ", new HudIcon(getHeadItem("ROUGH_TOPAZ_GEM", "TOPAZ_ROUGH"))),
            entry("JASPER", new HudIcon(getHeadItem("ROUGH_JASPER_GEM", "JASPER_ROUGH"))),
            entry("AQUAMARINE", new HudIcon(getHeadItem("ROUGH_AQUAMARINE_GEM", "AQUAMARINE_ROUGH"))),
            entry("ONYX", new HudIcon(getHeadItem("ROUGH_ONYX_GEM", "ONYX_ROUGH"))),
            entry("PERIDOT", new HudIcon(getHeadItem("ROUGH_PERIDOT_GEM", "PERIDOT_ROUGH"))),
            entry("CITRINE", new HudIcon(getHeadItem("ROUGH_CITRINE_GEM", "CITRINE_ROUGH"))),
            entry("AMETHYST_CRYSTAL", new HudIcon(getHeadItem("AMETHYST_CRYSTAL"))),
            entry("JADE_CRYSTAL", new HudIcon(getHeadItem("JADE_CRYSTAL"))),
            entry("SAPPHIRE_CRYSTAL", new HudIcon(getHeadItem("SAPPHIRE_CRYSTAL"))),
            entry("TOPAZ_CRYSTAL", new HudIcon(getHeadItem("TOPAZ_CRYSTAL"))),
            entry("AMBER_CRYSTAL", new HudIcon(getHeadItem("AMBER_CRYSTAL"))),
            entry("TUNGSTEN", new HudIcon(getHeadItem("TUNGSTEN"))),
            entry("UMBER", new HudIcon(getHeadItem("UMBER"))),
            entry("SUSPICIOUS_SCRAP", new HudIcon(getHeadItem("SUSPICIOUS_SCRAP"))),
            entry("GOBLIN", new HudIcon(justHead("GOBLIN"))),
            entry("TEAM_TREASURITE", new HudIcon(justHead("WENDY"))),
            entry("CORLEONE", new HudIcon(justHead("CORLEONE"))),
            entry("TREASURE_HOARDER", new HudIcon(justHead("HOARDER"))),
            entry("LOOTER", new HudIcon(getHeadItem("MINERAL_HELMET"))),
            entry("THYST", new HudIcon(justHead("ENDERMITE"))),
            entry("AUTOMATON", new HudIcon(justHead("GOLEM"))),
            entry("YOG", new HudIcon(justHead("MAGMA")))
    );





//    public static final HudIcon MITHRIL = new HudIcon(getSkyblockItem("prismarine_crystals", "MITHRIL_ORE"));
//    // The powders are not technically real items, but this allows resource packs to change the icon
//    public static final HudIcon MITHRIL_POWDER = new HudIcon(getSkyblockItem("lime_dye", "MITHRIL_POWDER"));
//    public static final HudIcon TITANIUM = new HudIcon(getHeadItem("TITANIUM_ORE", "TITANIUM"));






    private static Identifier ofItem(String name) {
        return Identifier.ofVanilla("textures/item/" + name + ".png");
    }

    private static Identifier ofBlock(String name) {
        return Identifier.ofVanilla("textures/block/" + name + ".png");
    }
}