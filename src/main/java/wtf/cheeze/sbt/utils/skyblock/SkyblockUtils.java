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

import net.minecraft.client.MinecraftClient;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.utils.skyblock.SkyblockConstants.*;

public class SkyblockUtils {

    public static Rarity castStringToRarity(String input) {
        input = input.toLowerCase();
        return switch (input) {
            case "common", "com", "c" -> Rarity.COMMON;
            case "uncommon", "unc", "u" -> Rarity.UNCOMMON;
            case "rare", "rar", "r" -> Rarity.RARE;
            case "epic", "ep", "e" -> Rarity.EPIC;
            case "legendary", "leg", "l" -> Rarity.LEGENDARY;
            case "mythic", "myth", "myt", "m" -> Rarity.MYTHIC;
            default -> null;
        };
    }

    public static Slayers castStringToSlayerType(String input) {
        input = input.toLowerCase();
        return switch (input) {
            case "zombie", "zom", "z", "revenant", "rev" -> Slayers.ZOMBIE;
            case "spider", "spi", "s", "tarantula", "tara" -> Slayers.SPIDER;
            case "wolf", "wol", "w", "sven" -> Slayers.WOLF;
            case "enderman", "ender", "e", "eman", "void", "voidgloom" -> Slayers.ENDERMAN;
            case "blaze", "bla", "b", "inferno", "inf" -> Slayers.BLAZE;
            case "vampire", "vamp", "v", "rift", "riftstalker", "blood" -> Slayers.VAMPIRE;
            default -> null;
        };
    }

    public static Crops castStringToCrop(String input) {
        input = input.toLowerCase();
        return switch (input) {
            case "wheat", "whe", "w" -> Crops.WHEAT;
            case "carrot", "car", "c" -> Crops.CARROT;
            case "potato", "pot" -> Crops.POTATO;
            case "pumpkin", "pum" -> Crops.PUMPKIN;
            case "melon", "mel" -> Crops.MELON;
            case "cane", "sugarcane", "sugar" -> Crops.SUGAR_CANE;
            case "cocoa", "coc", "beans", "cocoabeans" -> Crops.COCOA_BEANS;
            case "cactus", "cac" -> Crops.CACTUS;
            case "wart", "netherwart", "nether" -> Crops.NETHER_WART;
            case "mushroom", "mush" -> Crops.MUSHROOM;
            default -> null;
        };
    }

    public static Skills strictCastStringToSkill(String skill) {
        skill = skill.toLowerCase();
        return switch (skill) {
            case "farming" -> Skills.FARMING;
            case "mining" -> Skills.MINING;
            case "combat" -> Skills.COMBAT;
            case "foraging" -> Skills.FORAGING;
            case "fishing" -> Skills.FISHING;
            case "enchanting" -> Skills.ENCHANTING;
            case "alchemy" -> Skills.ALCHEMY;
            case "taming" -> Skills.TAMING;
            case "carpentry" -> Skills.CARPENTRY;
            case "runecrafting" -> Skills.RUNECRAFTING;
            case "social" -> Skills.SOCIAL;
            default -> Skills.UNKNOWN;
        };
    }

    public static Location getLocationFromMode(String mode) {
        return switch (mode) {
            case "dynamic" -> Location.PRIVATE_ISLAND;
            case "garden" -> Location.GARDEN;
            case "hub" -> Location.HUB;
            case "farming_1" -> Location.FARMING;
            case "foraging_1" -> Location.PARK;
            case "combat_1" -> Location.SPIDERS_DEN;
            case "combat_3" -> Location.END;
            case "crimson_isle" -> Location.CRIMSON_ISLE;
            case "mining_1" -> Location.GOLD_MINE;
            case "mining_2" -> Location.DEEP_CAVERNS;
            case "mining_3" -> Location.DWARVEN_MINES;
            case "crystal_hollows" -> Location.CRYSTAL_HOLLOWS;
            case "mineshaft" -> Location.GLACITE_MINESHAFT;
            case "dungeon_hub" -> Location.DUNGEON_HUB;
            case "winter" -> Location.JERRYS_WORKSHOP;
            case "rift" -> Location.RIFT;
            case "dark_auction" -> Location.DARK_AUCTION;
            case "dungeon" -> Location.DUNGEON;
            case "kuudra" -> Location.KUUDRA;
            default -> Location.UNKNOWN;
        };
    }

    public static boolean isThePlayerHoldingADrill() {
        return MinecraftClient.getInstance().player.getMainHandStack().getName().getString().contains("Drill");
    }

    public static boolean quiverActive() {
        return SkyblockTweaks.mc.player.getInventory().getStack(8).getName().getString().startsWith("Quiver");
    }

    /**
     * Please qualify calls to this with a call to quiverActive
     */
    public static QuiverData getQuiverData() {
        return new QuiverData(SkyblockTweaks.mc.player.getInventory().getStack(8));
    }


}
