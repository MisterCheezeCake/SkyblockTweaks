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
package wtf.cheeze.sbt.utils;

public class SkyblockUtils {

    public static SkyblockConstants.Rarity castStringToRarity(String input) {
        input = input.toLowerCase();
        switch (input) {
            case "common", "com", "c": return SkyblockConstants.Rarity.COMMON;
            case "uncommon", "unc", "u" : return SkyblockConstants.Rarity.UNCOMMON;
            case "rare", "rar", "r" : return SkyblockConstants.Rarity.RARE;
            case "epic", "ep", "e" : return SkyblockConstants.Rarity.EPIC;
            case "legendary", "leg", "l" : return SkyblockConstants.Rarity.LEGENDARY;
            case "mythic", "myth", "myt", "m" : return SkyblockConstants.Rarity.MYTHIC;

            default:
                return null;
        }
    }
    public static SkyblockConstants.Slayers castStringToSlayerType(String input) {
        input = input.toLowerCase();
        switch (input) {
            case "zombie", "zom", "z", "revenant", "rev": return SkyblockConstants.Slayers.ZOMBIE;
            case "spider", "spi", "s", "tarantula", "tara" : return SkyblockConstants.Slayers.SPIDER;
            case "wolf", "wol", "w" , "sven": return SkyblockConstants.Slayers.WOLF;
            case "enderman", "ender", "e", "eman",  "void", "voidgloom" : return SkyblockConstants.Slayers.ENDERMAN;
            case "blaze", "bla", "b" , "inferno", "inf" : return SkyblockConstants.Slayers.BLAZE;
            case "vampire", "vamp", "v", "rift",  "riftstalker", "blood" : return SkyblockConstants.Slayers.VAMPIRE;

            default:
                return null;
        }
    }
}
