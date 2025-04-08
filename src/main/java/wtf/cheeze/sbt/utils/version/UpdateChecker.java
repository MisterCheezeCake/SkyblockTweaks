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

import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import net.minecraft.MinecraftVersion;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.config.categories.General;
import wtf.cheeze.sbt.utils.HTTPUtils;
import wtf.cheeze.sbt.utils.MessageManager;
import wtf.cheeze.sbt.utils.NotificationHandler;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.utils.errors.ErrorHandler;
import wtf.cheeze.sbt.utils.errors.ErrorLevel;
import wtf.cheeze.sbt.utils.render.Colors;

import java.util.Map;

public class UpdateChecker {
    public static final String REMOTE_VERSION_FILE = "https://raw.githubusercontent.com/MisterCheezeCake/RemoteData/refs/heads/main/SBT/updateNew.json";
    private static final int CODE_SUCCESS = 200;


    public static void checkForUpdates() {
        if (SBTConfig.get().notificationStream != NotificationStream.NONE && SkyblockTweaks.VERSION.STREAM != VersionType.UNSTABLE) {
            try {
                SkyblockTweaks.LOGGER.info("Checking for updates");
                var result = HTTPUtils.get(REMOTE_VERSION_FILE);
                    if (result.statusCode() != CODE_SUCCESS) {
                    SkyblockTweaks.LOGGER.error("Failed to check for updates, status code: {}", result.statusCode());
                    return;
                }
                RemoteVersionFile remote = SkyblockTweaks.GSON.fromJson(result.body(), RemoteVersionFile.class);
                if (remote == null) {
                    SkyblockTweaks.LOGGER.error("Failed to parse remote version file");
                } else {
                    if (remote.enabled) {
                        switch (SBTConfig.get().notificationStream) {
                            case NotificationStream.ALPHA -> {
                                if (remote.latestAlpha == null) {
                                    break;
                                }
                                internalRun(remote.latestAlpha);
                            }
                            case NotificationStream.BETA -> {
                                if (remote.latestBeta == null) {
                                    break;
                                }
                                internalRun(remote.latestBeta);
                            }
                            case NotificationStream.RELEASE -> {
                                if (remote.latestRelease == null) {
                                    break;
                                }
                                internalRun(remote.latestRelease);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                ErrorHandler.handleError(e, "Failed to check for updates", ErrorLevel.WARNING);
            }
        }
    }

    private static void internalRun(Map<String, RemoteVersion> map) {
        var remoteVersion = map.get(MinecraftVersion.CURRENT.getName());
        if (remoteVersion == null) {
            SkyblockTweaks.LOGGER.warn("Null RemoteVersion");
            return;
        }
        var version = new Version(remoteVersion.versionString);
        var comparison = VersionComparison.compare(version, SkyblockTweaks.VERSION);
        if (comparison == VersionComparison.GREATER) {
            var link = Version.getModrinthLink(remoteVersion.modrinthName);

            var message = TextUtils.getTextThatLinksToURL(
                    TextUtils.join(
                            MessageManager.PREFIX,
                            TextUtils.SPACE,
                            TextUtils.withColor("Update ", Colors.CYAN),
                            TextUtils.withColor(remoteVersion.versionString, Colors.YELLOW),
                            TextUtils.withColor(" is available! ", Colors.CYAN),
                            TextUtils.withColor("[Download]", Colors.GREEN)

                    ),
                    //"§7[§aSkyblockTweaks§f§7] §3Update §e" + remoteVersion.versionString + " §3is available! §2[Download]",
                    TextUtils.withColor("Click to open Modrinth in your browser", Colors.CYAN),
                    link);
            NotificationHandler.pushChat(message);
        } else if (comparison == VersionComparison.EQUAL && remoteVersion.lastSupported) {
                var upgradeToVersion = new Version(map.get(remoteVersion.upgradeTo).versionString);
                var comp = VersionComparison.compare(upgradeToVersion, SkyblockTweaks.VERSION);
                if (comp == VersionComparison.GREATER) {
                            var message = TextUtils.getTextThatLinksToURL(
                                    TextUtils.join(
                                        MessageManager.PREFIX,
                                        TextUtils.SPACE,
                                        TextUtils.withColor("An update is available, but not for your Minecraft version. It is suggested that you upgrade your game version to ", Colors.CYAN),
                                        TextUtils.withColor(remoteVersion.upgradeTo, Colors.YELLOW),
                                        TextUtils.withColor(" to get the latest SBT features and bug fixes.", Colors.CYAN),
                                        TextUtils.withColor(" [Click to learn more]", Colors.GREEN)
                                    ),
                                    TextUtils.withColor("Click to open the SBT wiki in your browser", Colors.CYAN),
                                    "https://github.com/MisterCheezeCake/SkyblockTweaks/wiki/Version-Support#version-end-of-life"

                            );
                            NotificationHandler.pushChat(message);
                }



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
}
