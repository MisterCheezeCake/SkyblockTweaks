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
import wtf.cheeze.sbt.utils.enums.*;

public class SkyblockUtils {

    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final float SPRINT_MULT = 1.3f;

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

    public static Slayer castStringToSlayerType(String input) {
        input = input.toLowerCase();
        return switch (input) {
            case "zombie", "zom", "z", "revenant", "rev" -> Slayer.ZOMBIE;
            case "spider", "spi", "s", "tarantula", "tara" -> Slayer.SPIDER;
            case "wolf", "wol", "w", "sven" -> Slayer.WOLF;
            case "enderman", "ender", "e", "eman", "void", "voidgloom" -> Slayer.ENDERMAN;
            case "blaze", "bla", "b", "inferno", "inf" -> Slayer.BLAZE;
            case "vampire", "vamp", "v", "rift", "riftstalker", "blood" -> Slayer.VAMPIRE;
            default -> null;
        };
    }

    public static Crop castStringToCrop(String input) {
        input = input.toLowerCase();
        return switch (input) {
            case "wheat", "whe", "w" -> Crop.WHEAT;
            case "carrot", "car", "c" -> Crop.CARROT;
            case "potato", "pot" -> Crop.POTATO;
            case "pumpkin", "pum" -> Crop.PUMPKIN;
            case "melon", "mel" -> Crop.MELON;
            case "cane", "sugarcane", "sugar" -> Crop.SUGAR_CANE;
            case "cocoa", "coc", "beans", "cocoabeans" -> Crop.COCOA_BEANS;
            case "cactus", "cac" -> Crop.CACTUS;
            case "wart", "netherwart", "nether" -> Crop.NETHER_WART;
            case "mushroom", "mush" -> Crop.MUSHROOM;
            default -> null;
        };
    }

    public static Skill strictCastStringToSkill(String skill) {
        skill = skill.toLowerCase();
        return switch (skill) {
            case "farming" -> Skill.FARMING;
            case "mining" -> Skill.MINING;
            case "combat" -> Skill.COMBAT;
            case "foraging" -> Skill.FORAGING;
            case "fishing" -> Skill.FISHING;
            case "enchanting" -> Skill.ENCHANTING;
            case "alchemy" -> Skill.ALCHEMY;
            case "taming" -> Skill.TAMING;
            case "carpentry" -> Skill.CARPENTRY;
            case "runecrafting" -> Skill.RUNECRAFTING;
            case "social" -> Skill.SOCIAL;
            case "hunting" -> Skill.HUNTING;
            default -> Skill.UNKNOWN;
        };
    }

    public static boolean isThePlayerHoldingADrill() {
        return MinecraftClient.getInstance().player.getMainHandStack().getName().getString().contains("Drill");
    }

    public static boolean quiverActive() {
        return client.player.getInventory().getStack(8).getName().getString().startsWith("Quiver");
    }


    /**
     * Please qualify calls to this with a call to quiverActive
     */
    public static QuiverData getQuiverData() {
        try {
            if (quiverActive()) return new QuiverData(client.player.getInventory().getStack(8));
            return QuiverData.DEFAULT;
        } catch (Exception e) {
            return QuiverData.DEFAULT;
        }
    }


    public static float getSpeed() {
        MinecraftClient mc = MinecraftClient.getInstance();
        // sprint = 1.3 x base speed
        return mc.player.isSprinting() ? (mc.player.getMovementSpeed() / SPRINT_MULT) * 1000 : mc.player.getMovementSpeed() * 1000;
    }

    public static boolean inMiningIsland() {
        return SkyblockData.location == Location.DWARVEN_MINES || SkyblockData.location == Location.CRYSTAL_HOLLOWS|| SkyblockData.location == Location.GLACITE_MINESHAFT;
    }
}
