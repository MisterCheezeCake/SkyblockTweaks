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

import com.mojang.authlib.GameProfile;

public class SkyblockUtils {

    public static SkyblockConstants.Rarity castStringToRarity(String input) {
        input = input.toLowerCase();
        return switch (input) {
            case "common", "com", "c" -> SkyblockConstants.Rarity.COMMON;
            case "uncommon", "unc", "u" -> SkyblockConstants.Rarity.UNCOMMON;
            case "rare", "rar", "r" -> SkyblockConstants.Rarity.RARE;
            case "epic", "ep", "e" -> SkyblockConstants.Rarity.EPIC;
            case "legendary", "leg", "l" -> SkyblockConstants.Rarity.LEGENDARY;
            case "mythic", "myth", "myt", "m" -> SkyblockConstants.Rarity.MYTHIC;
            default -> null;
        };
    }
    public static SkyblockConstants.Slayers castStringToSlayerType(String input) {
        input = input.toLowerCase();
        return switch (input) {
            case "zombie", "zom", "z", "revenant", "rev" -> SkyblockConstants.Slayers.ZOMBIE;
            case "spider", "spi", "s", "tarantula", "tara" -> SkyblockConstants.Slayers.SPIDER;
            case "wolf", "wol", "w", "sven" -> SkyblockConstants.Slayers.WOLF;
            case "enderman", "ender", "e", "eman", "void", "voidgloom" -> SkyblockConstants.Slayers.ENDERMAN;
            case "blaze", "bla", "b", "inferno", "inf" -> SkyblockConstants.Slayers.BLAZE;
            case "vampire", "vamp", "v", "rift", "riftstalker", "blood" -> SkyblockConstants.Slayers.VAMPIRE;
            default -> null;
        };
    }
}
