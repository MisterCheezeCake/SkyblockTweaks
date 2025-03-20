/*
 * Copyright (C) 2025 MisterCheezeCake
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
package wtf.cheeze.sbt.utils.version;

public enum VersionType {
    // Development versions, ideally, a user should never be using these
    UNSTABLE,
    // Alpha versions, these are the least stable jars that will go out to users, and may have bugs
    ALPHA,
    // Beta versions, more stable than alpha, but still may have bugs and not ready for full release
    BETA,
    // Full releases, stable and ideally not released with any known bugs
    RELEASE
}
