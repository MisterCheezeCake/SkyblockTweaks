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

import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import net.minecraft.MinecraftVersion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.categories.General;

import java.util.Map;
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

    public String getVersionString() {
        if (STREAM == VersionType.UNSTABLE) {
            return "Unstable";
        } else if (STREAM != VersionType.RELEASE) {
            return String.format("%d.%d.%d-%s.%d", MAJOR, MINOR, PATCH, STREAM == VersionType.ALPHA ? "Alpha" : "Beta", BUILD);
        } else {
            return String.format("%d.%d.%d", MAJOR, MINOR, PATCH);
        }
    }

    public String getVersionStrringWithMc() {
        return getVersionString() + "+mc" + MinecraftVersion.CURRENT.getName();
    }

    public static VersionComparison compareVersions(Version a, Version b) {
        try {
            if (a.STREAM == VersionType.UNSTABLE || b.STREAM == VersionType.UNSTABLE) {
                return VersionComparison.FAILURE;
            }

            if (a.MAJOR > b.MAJOR) {
                return VersionComparison.GREATER;
            } else if (a.MAJOR < b.MAJOR) {
                return VersionComparison.LESS;
            }
            if (a.MINOR > b.MINOR) {
                return VersionComparison.GREATER;
            } else if (a.MINOR < b.MINOR) {
                return VersionComparison.LESS;
            }
            if (a.PATCH > b.PATCH) {
                return VersionComparison.GREATER;
            } else if (a.PATCH < b.PATCH) {
                return VersionComparison.LESS;
            }
            // If we get here, the semvers are equal
            if (a.STREAM == VersionType.ALPHA && b.STREAM == VersionType.BETA) {
                return VersionComparison.LESS;
            } else if (a.STREAM == VersionType.BETA && b.STREAM == VersionType.ALPHA) {
                return VersionComparison.GREATER;
            }
            if (a.BUILD > b.BUILD) {
                return VersionComparison.GREATER;
            } else if (a.BUILD < b.BUILD) {
                return VersionComparison.LESS;
            }
            return VersionComparison.EQUAL;
        } catch (Exception e) {
            return VersionComparison.FAILURE;
        }
    }



    public static Option<NotificationStream> getStreamOption(ConfigImpl defaults, ConfigImpl config) {
        return Option.<NotificationStream>createBuilder()
                .name(General.key("notificationStream"))
                .description(General.keyD("notificationStream"))
                .controller(opt -> EnumControllerBuilder.create(opt).enumClass(NotificationStream.class))
                .binding(
                        defaults.notificationStream,
                        () -> config.notificationStream,
                        value -> config.notificationStream = value
                )
                .build();
    }
    public static String getModrinthLink(String name) {
        return "https://modrinth.com/mod/sbt/version/" + name;
    }

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
    public enum NotificationStream implements NameableEnum {
        ALPHA,
        BETA,
        RELEASE,
        NONE;

        @Override
        public Text getDisplayName() {
            return Text.literal(name());
        }
    }

    public enum VersionComparison {
        EQUAL,
        GREATER,
        LESS,
        FAILURE
    }

    public static class RemoteVersionFile {
        public boolean enabled;
        public Map<String, RemoteVersion> latestAlpha;
        public Map<String, RemoteVersion> latestBeta;
        public Map<String, RemoteVersion> latestRelease;
    }

    public static class RemoteVersion {
        public String versionString;
        public String modrinthName;
    }
}