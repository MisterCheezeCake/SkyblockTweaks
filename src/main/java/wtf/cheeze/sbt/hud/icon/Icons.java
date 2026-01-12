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
package wtf.cheeze.sbt.hud.icon;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;

import wtf.cheeze.sbt.utils.enums.Skill;
import wtf.cheeze.sbt.utils.skyblock.ItemUtils;

import static java.util.Map.entry;

public class Icons {
    public static final HudIcon ARROW = itemID("arrow");
    public static final HudIcon CHEST_MINECART = itemID("chest_minecart");
    public static final HudIcon CHEST = itemStack("chest");
    public static final HudIcon FIREWORK = itemID("firework_rocket");
    public static final HudIcon IRON_SWORD = itemID("iron_sword");
    public static final HudIcon DIAMOND_PICKAXE = itemID("diamond_pickaxe");
    public static final HudIcon NETHER_STAR = itemID("nether_star");
    public static final HudIcon GOLDEN_HELMET = itemID("golden_helmet");
    public static final HudIcon COMPASS = itemStack("compass");
    public static final HudIcon WATER_BUCKET = itemID("water_bucket");
    public static final HudIcon DEFAULT_HEAD = itemStack("player_head");

    public static final HudIcon DEFAULT_ICON = HudIcon.of(ResourceLocation.fromNamespaceAndPath("skyblocktweaks", "missing.png"));
    public static final Map<Skill, HudIcon> SKILL_ICONS = Map.ofEntries(
            entry(Skill.COMBAT, IRON_SWORD),
            entry(Skill.FARMING, itemID("golden_hoe")),
            entry(Skill.MINING, DIAMOND_PICKAXE),
            entry(Skill.FORAGING, blockID("oak_sapling")),
            entry(Skill.FISHING, itemID("fishing_rod")),
            entry(Skill.ENCHANTING, itemStack("enchanting_table")),
            entry(Skill.ALCHEMY, itemID("brewing_stand")),
            entry(Skill.CARPENTRY, itemStack("crafting_table")),
            entry(Skill.RUNECRAFTING, itemID("magma_cream")),
            entry(Skill.SOCIAL , itemID("emerald")),
            entry(Skill.HUNTING, itemID("lead"))
    );

    public static final Map<String, HudIcon> MINING_ICONS = Map.ofEntries(
            entry("MITHRIL_POWDER", sb("lime_dye", "MITHRIL_POWDER")),
            entry("GEMSTONE_POWDER", sb("magenta_dye", "GEMSTONE_POWDER")),
            entry("GLACITE_POWDER", sb("light_blue_dye", "GLACITE_POWDER")),
            entry("MITHRIL", sb("prismarine_crystals", "MITHRIL_ORE")),
            entry("HARD_STONE", sb("stone", "HARD_STONE")),
            entry("RAFFLE", sb("name_tag", "MINING_RAFFLE_TICKET")),
            entry("GLACITE", sb("packed_ice", "GLACITE")),
            entry("TITANIUM", sbHead("TITANIUM_ORE", "TITANIUM")),
            entry("RUBY", sbHead("ROUGH_RUBY_GEM", "RUBY_ROUGH")),
            entry("AMETHYST", sbHead("ROUGH_AMETHYST_GEM", "AMETHYST_ROUGH")),
            entry("AMBER", sbHead("ROUGH_AMBER_GEM", "AMBER_ROUGH")),
            entry("JADE", sbHead("ROUGH_JADE_GEM", "JADE_ROUGH")),
            entry("SAPPHIRE", sbHead("ROUGH_SAPPHIRE_GEM", "SAPPHIRE_ROUGH")),
            entry("TOPAZ", sbHead("ROUGH_TOPAZ_GEM", "TOPAZ_ROUGH")),
            entry("JASPER", sbHead("ROUGH_JASPER_GEM", "JASPER_ROUGH")),
            entry("AQUAMARINE", sbHead("ROUGH_AQUAMARINE_GEM", "AQUAMARINE_ROUGH")),
            entry("ONYX", sbHead("ROUGH_ONYX_GEM", "ONYX_ROUGH")),
            entry("PERIDOT", sbHead("ROUGH_PERIDOT_GEM", "PERIDOT_ROUGH")),
            entry("CITRINE",  sbHead("ROUGH_CITRINE_GEM", "CITRINE_ROUGH")),
            entry("AMETHYST_CRYSTAL",  sbHead("AMETHYST_CRYSTAL")),
            entry("JADE_CRYSTAL",  sbHead("JADE_CRYSTAL")),
            entry("SAPPHIRE_CRYSTAL",  sbHead(("SAPPHIRE_CRYSTAL"))),
            entry("TOPAZ_CRYSTAL", sbHead("TOPAZ_CRYSTAL")),
            entry("AMBER_CRYSTAL", sbHead("AMBER_CRYSTAL")),
            entry("TUNGSTEN", sbHead("TUNGSTEN")),
            entry("UMBER", sbHead("UMBER")),
            entry("SUSPICIOUS_SCRAP", sbHead("SUSPICIOUS_SCRAP")),
            entry("GOBLIN", head("GOBLIN")),
            entry("TEAM_TREASURITE", head("WENDY")),
            entry("CORLEONE", head("CORLEONE")),
            entry("TREASURE_HOARDER", head("HOARDER")),
            entry("LOOTER", sbHead("MINERAL_HELMET")),
            entry("THYST", head("ENDERMITE")),
            entry("AUTOMATON", head("GOLEM")),
            entry("YOG", head("MAGMA")),
            entry("SLUDGE", head("SLIME")),
            entry("GOURMAND", sb("cyan_dye", "MITHRIL_GOURMAND"))
    );





//    public static final HudIcon MITHRIL = new HudIcon(getSkyblockItem("prismarine_crystals", "MITHRIL_ORE"));
//    // The powders are not technically real items, but this allows resource packs to change the icon
//    public static final HudIcon MITHRIL_POWDER = new HudIcon(getSkyblockItem("lime_dye", "MITHRIL_POWDER"));
//    public static final HudIcon TITANIUM = new HudIcon(getHeadItem("TITANIUM_ORE", "TITANIUM"));






    private static HudIcon itemID(String name) {
        return HudIcon.of(ResourceLocation.withDefaultNamespace("textures/item/" + name + ".png"));
    }

    private static HudIcon blockID(String name) {
        return HudIcon.of(ResourceLocation.withDefaultNamespace("textures/block/" + name + ".png"));
    }

    private static HudIcon itemStack(String name) {
        return HudIcon.of(ItemUtils.getVanilla(name));
    }

    private static HudIcon sb(String minecraftID, String skyblockID) {
        return HudIcon.of(ItemUtils.getSkyblock(minecraftID, skyblockID));
    }

    private static HudIcon sbHead(String skyblockID, String skullName) {
        return HudIcon.of(ItemUtils.getHead(skullName, skyblockID));
    }
    private static HudIcon sbHead(String both) {
        return sbHead(both, both);
    }

    private static HudIcon head(String skullName) {
        return HudIcon.of(ItemUtils.justHead(skullName));
    }
}