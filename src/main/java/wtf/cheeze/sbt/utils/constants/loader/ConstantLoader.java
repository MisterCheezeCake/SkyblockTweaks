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
package wtf.cheeze.sbt.utils.constants.loader;

import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.events.EventUtils;
import wtf.cheeze.sbt.utils.MessageManager;
import wtf.cheeze.sbt.utils.NotificationHandler;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.utils.constants.*;
import wtf.cheeze.sbt.utils.errors.ErrorHandler;
import wtf.cheeze.sbt.utils.errors.ErrorLevel;
import wtf.cheeze.sbt.utils.render.Colors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

public class ConstantLoader {

    private static final int SPEC_VERSION = 1;

    public static final Event<Runnable> RELOAD = EventUtils.getRunnableBackedEvent();

    private static final Logger LOGGER = LoggerFactory.getLogger("SkyblockTweaks Constant Loader");
    public static final Path REPO_FOLDER = FabricLoader.getInstance().getConfigDir().resolve("skyblocktweaks/repo");
    public static final Map<String, Class<?>> EXPECTED_CONSTANTS = Map.of(
            "skills", Skills.class,
            "garden", Garden.class,
            "slayers", Slayers.class,
            "pets", Pets.class,
            "hotm", Hotm.class,
            "disabledFeatures", DisabledFeatures.class
    );

    static Garden garden = Garden.empty();
    static Skills skills = Skills.empty();
    static Slayers slayers = Slayers.empty();
    static Pets pets = Pets.empty();
    static Hotm hotm = Hotm.empty();
    static DisabledFeatures disabledFeatures = DisabledFeatures.empty();

    public static void registerEvents() {
      if (!REPO_FOLDER.toFile().exists()) {
          if(!REPO_FOLDER.toFile().mkdirs()) failureNotification("Failed to create repo folder");
      }
      load();
    }




    public static void load() {
        try {
            if (!REPO_FOLDER.resolve("manifest.json").toFile().exists()) {
                LOGGER.info("No local manifest file, attempting to download files");
                Files.writeString(REPO_FOLDER.resolve("manifest.json"), SkyblockTweaks.GSON.toJson(new LocalManifest()));
            } else {
                loadFromFiles();
            }
            var commitHash = checkForUpdate();
            if (commitHash != null) {
                new Thread(() -> {
                    try {
                        updateRepo(commitHash);
                        loadFromFiles();
                    } catch (IOException | URISyntaxException e) {
                        ErrorHandler.handleError(e, "Failed to update constants", ErrorLevel.CRITICAL);
                    }
                }, "SBT-ConstantLoader").start();

            }
        } catch (Exception e) {
            ErrorHandler.handleError(e, "Failed to load constants", ErrorLevel.CRITICAL);
            tryLoadAgain();
        }
    }

    private static void tryLoadAgain() {
        try {
            loadFromFiles();
        } catch (Exception e) {
            ErrorHandler.handleError(e, "Failed to load constants on second attempt: reverting to fallback", ErrorLevel.CRITICAL);
            try {
                updateFromFallback();
                loadFromFiles();
            } catch (Exception ex) {
                ErrorHandler.handleError(ex, "Failed to load fallback contants. This should not be happening, please contact support in the discord.", ErrorLevel.CRITICAL);
            }
        }
    }




    private static void updateRepo(String commitHash) throws IOException, URISyntaxException {
        var remoteManifest = GithubAPI.checkRemoteManifest(SBTConfig.get().constantLoader.user, SBTConfig.get().constantLoader.repo, SBTConfig.get().constantLoader.branch);
        if (remoteManifest == null) {
            //ErrorHandler.handleError(new Exception("Failed to get remote manifest"), "Failed to get remote manifest", ErrorLevel.CRITICAL);
            throw new IOException("Remote manifest not found");
        }
        if (remoteManifest.specVersion != SPEC_VERSION) {
            NotificationHandler.pushChat(TextUtils.join(
                    MessageManager.PREFIX,
                    TextUtils.SPACE,
                    TextUtils.withColor("Failed to update constants, the remote manifest spec version is " + remoteManifest.specVersion + " and your current version is " + SPEC_VERSION + ". Please update your mod or contact the author if this message appears on the latest version" ,Colors.RED)
            ));
            return;
        }
        LOGGER.info("Updating constants from " + SBTConfig.get().constantLoader.user + "/" + SBTConfig.get().constantLoader.repo + "#" + SBTConfig.get().constantLoader.branch);
        var fileName = "repo-" + commitHash+ ".zip";
        //LOGGER.info("Downloading repo zip from " + SBTConfig.get().constantLoader.user + "/" + SBTConfig.get().constantLoader.repo + "#" + SBTConfig.get().constantLoader.branch);
        GithubAPI.downloadRepoZip(SBTConfig.get().constantLoader.user, SBTConfig.get().constantLoader.repo, SBTConfig.get().constantLoader.branch, REPO_FOLDER.resolve(fileName).toFile());
        LOGGER.info("Unzipping repo zip");
        var tempFolder = REPO_FOLDER.resolve("tmp");
        ZipUtils.unzip(REPO_FOLDER.resolve(fileName).toString(), tempFolder.toFile());
        LOGGER.info("Deleting old repo zip");
        Files.delete(REPO_FOLDER.resolve(fileName));

        LOGGER.info("Copying new repo files");
        try (var dirList = Files.list(tempFolder)) {
            var genFolder = dirList.findFirst().orElseThrow();
            FileUtils.copyDirectory(genFolder.toFile(), REPO_FOLDER.toFile());
            LOGGER.info("Deleting temp files");
            FileUtils.deleteDirectory(tempFolder.toFile());
            LOGGER.info("Refreshing manifest");
            RemoteManifest manifest = SkyblockTweaks.GSON.fromJson(
                    Files.readString(REPO_FOLDER.resolve("manifest.json")),
                    RemoteManifest.class
            );
            var localManifest = manifest.toLocalManifest(commitHash, SBTConfig.get().constantLoader.user + "/" + SBTConfig.get().constantLoader.repo + "#" + SBTConfig.get().constantLoader.branch);
            Files.writeString(REPO_FOLDER.resolve("manifest.json"), SkyblockTweaks.GSON.toJson(localManifest));
        }

    }

    /**
     * The mod includes a fallback repo zip, which is the most recent repo commit at the time the JAR was built.
     */
    private static void updateFromFallback() throws IOException {
        var tempFolder = REPO_FOLDER.resolve("tmp");
        LOGGER.info("Updating from fallback");

        var fallbackManifest = SkyblockTweaks.GSON.fromJson(
                new BufferedReader(new InputStreamReader(SkyblockTweaks.class.getResourceAsStream("/fallback/manifest.json"))),
                FallbackManifest.class
        );
        ZipUtils.unzip(SkyblockTweaks.class.getResourceAsStream("/fallback/repo.zip"), tempFolder.toFile());
        try (var dirList = Files.list(tempFolder)) {
            var genFolder = dirList.findFirst().orElseThrow();
            FileUtils.copyDirectory(genFolder.toFile(), REPO_FOLDER.toFile());
            LOGGER.info("Deleting temp files");
            FileUtils.deleteDirectory(tempFolder.toFile());
            LOGGER.info("Refreshing manifest");
            RemoteManifest manifest = SkyblockTweaks.GSON.fromJson(
                    Files.readString(REPO_FOLDER.resolve("manifest.json")),
                    RemoteManifest.class
            );
            var localManifest = manifest.toLocalManifest(fallbackManifest.commit, SBTConfig.get().constantLoader.user + "/" + SBTConfig.get().constantLoader.repo + "#" + SBTConfig.get().constantLoader.branch);
            Files.writeString(REPO_FOLDER.resolve("manifest.json"), SkyblockTweaks.GSON.toJson(localManifest));
        }


    }

    /**
     * Returns the commit hash to be updated from if there is an update or null if no update is due
     */
    private static String checkForUpdate() throws IOException, URISyntaxException {

        LocalManifest manifest = SkyblockTweaks.GSON.fromJson(
                Files.readString(REPO_FOLDER.resolve("manifest.json")),
                LocalManifest.class
        );
        var branches = GithubAPI.getBranches(SBTConfig.get().constantLoader.user, SBTConfig.get().constantLoader.repo);
        var desiredBranch = Arrays.stream(branches).filter(branch -> branch.name().equals(SBTConfig.get().constantLoader.branch)).findFirst();
        if (desiredBranch.isEmpty()) {
            throw new IOException("Did not find branch " + SBTConfig.get().constantLoader.branch + " in " + SBTConfig.get().constantLoader.user + "/" + SBTConfig.get().constantLoader.repo);
        }
        var branch = desiredBranch.get();
        if(!branch.commit().sha().equals(manifest.commitHash)) {
            return branch.commit().sha();
        }
        return null;
    }


    public static void loadFromFiles() throws IOException {
        LocalManifest manifest = SkyblockTweaks.GSON.fromJson(
                Files.readString(REPO_FOLDER.resolve("manifest.json")),
                LocalManifest.class
        );
        var constantFolder = REPO_FOLDER.resolve(manifest.constantsDirectory);
        for (var expected : EXPECTED_CONSTANTS.entrySet()) {
            String name = expected.getKey();
            Class<?> clazz = expected.getValue();
            if (manifest.providedFiles.contains(name + ".json")) {
                Path path = constantFolder.resolve(name + ".json");
                if (!path.toFile().exists()) {
                    failureNotification("Failed to load " + name + " constants, file not found");
                }
                var contents = SkyblockTweaks.GSON.fromJson(Files.readString(path), clazz);
                switch (name) {
                    case "skills" -> skills = (Skills) contents;
                    case "garden" -> garden = (Garden) contents;
                    case "slayers" -> slayers = (Slayers) contents;
                    case "pets" -> pets = (Pets) contents;
                    case "hotm" -> hotm = (Hotm) contents;
                    case "disabledFeatures" -> disabledFeatures = (DisabledFeatures) contents;
                }
            }
        }
        RELOAD.invoker().run();
    }

    public static LocalManifest getLocalManifestSafe() {
        try {
            return SkyblockTweaks.GSON.fromJson(
                    Files.readString(REPO_FOLDER.resolve("manifest.json")),
                    LocalManifest.class
            );
        } catch (IOException e) {
            ErrorHandler.handleError(e, "Failed to load local manifest", ErrorLevel.SILENT, false);
            return null;
        }
    }

    public static void failureNotification(String message) {
        LOGGER.error(message);
        NotificationHandler.pushChat(TextUtils.join(
                MessageManager.PREFIX,
                TextUtils.SPACE,
                TextUtils.withColor(message, Colors.RED)
        ));
    }

    public static class Config {
        @SerialEntry
        public String user = "SkyblockTweaks";

        @SerialEntry
        public String repo = "data";

        @SerialEntry
        public String branch = "main";
    }


}
