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
package wtf.cheeze.sbt.utils.version;

import java.util.regex.Pattern;

public class Version {
    public static Pattern PATTERN_RELEASE = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)$");
    public static Pattern PATTERN_ALPHA_BETA = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)-(Alpha|Beta)\\.(\\d+)$");

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
        STREAM = type; // Good lord, I am an idiot, this was missing
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
    public Version (String versionString) {
        if (versionString.equals("Unstable")) {
            STREAM = VersionType.UNSTABLE;
            return;
        }
        var releaseMatcher = PATTERN_RELEASE.matcher(versionString);
        var alphaBetaMatcher = PATTERN_ALPHA_BETA.matcher(versionString);
        if (releaseMatcher.matches()) {
            STREAM = VersionType.RELEASE;
            MAJOR = Integer.parseInt(releaseMatcher.group(1));
            MINOR = Integer.parseInt(releaseMatcher.group(2));
            PATCH = Integer.parseInt(releaseMatcher.group(3));
        } else if (alphaBetaMatcher.matches()) {
            MAJOR = Integer.parseInt(alphaBetaMatcher.group(1));
            MINOR = Integer.parseInt(alphaBetaMatcher.group(2));
            PATCH = Integer.parseInt(alphaBetaMatcher.group(3));
            STREAM = alphaBetaMatcher.group(4).equals("Alpha") ? VersionType.ALPHA : VersionType.BETA;
            BUILD = Integer.parseInt(alphaBetaMatcher.group(5));
        } else {
            throw new IllegalArgumentException("Invalid version string: " + versionString);
        }
    }

    public static String getBuildMCVersion() {
        //? if =1.21.4 {
        return "1.21.4";
         //?} else if =1.21.5 {
        /*return "1.21.5";
        *///?} else if >1.21.5 {
         /*return "1.21.6";
         *///?}


    }

    public String getVersionString() {
        if (STREAM == VersionType.UNSTABLE) {
            return "Unstable";
        } else if (STREAM != VersionType.RELEASE) {
            return String.format("%d.%d.%d-%s.%d", MAJOR, MINOR, PATCH, STREAM == VersionType.ALPHA ? "Alpha" : "Beta", BUILD);
        } else {
            return String.format("%d.%d.%d", MAJOR, MINOR, PATCH);
        }
    }

    public String getVersionStringWithMc() {
        return getVersionString() + "+mc" + getBuildMCVersion();
    }


    public static String getModrinthLink(String name) {
        return "https://modrinth.com/mod/sbt/version/" + name;
    }

}