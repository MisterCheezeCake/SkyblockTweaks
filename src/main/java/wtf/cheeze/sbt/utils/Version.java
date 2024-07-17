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

public class Version {

    public static enum VersionType {
        // Development versions, ideally, a user should never be using these
        UNSTABLE,
        // Alpha versions, these are the least stable jars that will go out to users, and may have bugs
        ALPHA,
        // Beta versions, more stable than alpha, but still may have bugs and not ready for full release
        BETA,
        // Full releases, stable and ideally not released with any known bugs
        RELEASE
    }

    public VersionType STREAM;
    public int MAJOR;
    public int MINOR;
    public int PATCH;
    // Build number, ie the 3 in 1.0.0-Alpha.3
    public int BUILD;
    public Version(VersionType type) {
        if (type == VersionType.UNSTABLE) {
            STREAM = type;
        } else {
            throw new IllegalArgumentException("Stable versions must have a semantic version");
        }
    }
    public Version(VersionType type, int major, int minor, int patch, int build) {
        if (type == VersionType.RELEASE || type == VersionType.UNSTABLE) {
            throw new IllegalArgumentException("Release and Unstable versions must NOT have a build number");
        }
        MAJOR = major;
        MINOR = minor;
        PATCH = patch;
        BUILD = build;
    }
    public Version(VersionType type, int major, int minor, int patch) {
        if (type == VersionType.ALPHA || type == VersionType.BETA) {
            throw new IllegalArgumentException("Alpha and Beta versions must have a build number");
        }
        MAJOR = major;
        MINOR = minor;
        PATCH = patch;
    }
    public String getVersionString() {
        if (STREAM == VersionType.UNSTABLE) {
            return "Unstable";
        } else if (STREAM != VersionType.RELEASE) {
            return String.format("%d.%d.%d-%s.%d", MAJOR, MINOR, PATCH, STREAM == VersionType.ALPHA ? "Beta" : "Alpha", BUILD);
        } else {
            return String.format("%d.%d.%d", MAJOR, MINOR, PATCH);
        }
    }
}
