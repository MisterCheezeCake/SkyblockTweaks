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
package wtf.cheeze.sbt.utils.enums;

/**
 * Skill
 * Unknown is used rather than null when no skill is known to prevent NPEs
 */
public enum Skill {
    COMBAT,
    MINING,
    FARMING,
    FORAGING,
    FISHING,
    ENCHANTING,
    ALCHEMY,
    TAMING,
    CARPENTRY,
    RUNECRAFTING,
    SOCIAL,
    UNKNOWN
}
