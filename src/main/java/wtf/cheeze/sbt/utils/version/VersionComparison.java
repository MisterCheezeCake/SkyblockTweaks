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

public enum VersionComparison {
    EQUAL,
    GREATER,
    LESS,
    FAILURE;

    public static VersionComparison compare(Version a, Version b) {
        try {
            if (a.STREAM == VersionType.UNSTABLE || b.STREAM == VersionType.UNSTABLE) {
                return FAILURE;
            }

            if (a.MAJOR > b.MAJOR) {
                return GREATER;
            } else if (a.MAJOR < b.MAJOR) {
                return LESS;
            }
            if (a.MINOR > b.MINOR) {
                return GREATER;
            } else if (a.MINOR < b.MINOR) {
                return LESS;
            }
            if (a.PATCH > b.PATCH) {
                return GREATER;
            } else if (a.PATCH < b.PATCH) {
                return LESS;
            }
            // If we get here, the semvers are equal
            if (a.STREAM == VersionType.ALPHA && b.STREAM == VersionType.BETA) {
                return LESS;
            } else if (a.STREAM == VersionType.BETA && b.STREAM == VersionType.ALPHA) {
                return GREATER;
            }
            if (a.BUILD > b.BUILD) {
                return GREATER;
            } else if (a.BUILD < b.BUILD) {
                return LESS;
            }
            return EQUAL;
        } catch (Exception e) {
            return FAILURE;
        }
    }
}
