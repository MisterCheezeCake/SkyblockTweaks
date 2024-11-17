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

import net.minecraft.MinecraftVersion;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.utils.Version.NotificationStream;

public class UpdateChecker {
    public static final String REMOTE_VERSION_FILE = "https://raw.githubusercontent.com/MisterCheezeCake/RemoteData/refs/heads/main/SBT/updateNew.json";

    public static void checkForUpdates() {
        if (SBTConfig.get().notificationStream != NotificationStream.NONE && SkyblockTweaks.VERSION.STREAM != Version.VersionType.UNSTABLE) {
            try {
                SkyblockTweaks.LOGGER.info("Checking for updates");
                var result = HTTPUtils.get(REMOTE_VERSION_FILE);
                Version.RemoteVersionFile remote = SkyblockTweaks.GSON.fromJson(result, Version.RemoteVersionFile.class);
                if (remote == null) {
                    SkyblockTweaks.LOGGER.error("Failed to parse remote version file");
                } else {
                    if (remote.enabled) {
                        switch (SBTConfig.get().notificationStream) {
                            case NotificationStream.ALPHA -> {
                                if (remote.latestAlpha == null) {
                                    break;
                                }

                                internalRun(remote.latestAlpha.get(MinecraftVersion.CURRENT.getName()));
                            }
                            case NotificationStream.BETA -> {
                                if (remote.latestBeta == null) {
                                    break;
                                }
                                internalRun(remote.latestBeta.get(MinecraftVersion.CURRENT.getName()));
                            }
                            case NotificationStream.RELEASE -> {
                                if (remote.latestRelease == null) {
                                    break;
                                }
                                internalRun(remote.latestRelease.get(MinecraftVersion.CURRENT.getName()));
                            }
                        }
                    }
                }
            } catch (Exception e) {
               SkyblockTweaks.LOGGER.error("Failed to check for updates", e);
            }
        }
    }

    private static void internalRun(Version.RemoteVersion remoteVersion) {
        if (remoteVersion == null) {
            SkyblockTweaks.LOGGER.warn("Null RemoteVersion");
            return;
        };
        var version = new Version(remoteVersion.versionString);
        var comparison = Version.compareVersions(version, SkyblockTweaks.VERSION);
        if (comparison == Version.VersionComparison.GREATER) {
            var link = Version.getModrinthLink(remoteVersion.modrinthName);
            var message = TextUtils.getTextThatLinksToURL("§7[§aSkyblockTweaks§f§7] §3Update §e" + remoteVersion.versionString + " §3is available! §2[Download]", "§3Click to open Modrinth in your browser", link);
            NotificationHandler.NOTIFICATION_QUEUE.add(message);
        }
    }


}
