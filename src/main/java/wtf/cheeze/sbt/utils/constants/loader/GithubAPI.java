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

import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.utils.HTTPUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class GithubAPI {
    public static final String BASE_URL = "https://api.github.com/";
    public static final String USERCONTENT_URL = "https://raw.githubusercontent.com/";


    public static Branch[] getBranches(String user, String repo) throws URISyntaxException, IOException {
        String url = BASE_URL + "repos/" + user + "/" + repo + "/branches";
        var result = HTTPUtils.get(url);
        if (result.statusCode() != 200) {
            throw new IOException("Failed to get branches, status code: " + result.statusCode());
        }
        return SkyblockTweaks.GSON.fromJson(result.body(), Branch[].class);
    }

    public static void downloadRepoZip(String user, String repo, String branch, File destination) throws URISyntaxException, IOException {
        String url = BASE_URL + "repos/" + user + "/" + repo + "/zipball/" + branch;
        HTTPUtils.downloadFile(url, destination);
    }

    public static RemoteManifest checkRemoteManifest(String user, String repo, String branch) throws IOException, URISyntaxException {
        String url = USERCONTENT_URL + user + "/" + repo + "/refs/heads/" + branch + "/manifest.json";
        var result = HTTPUtils.get(url);
        if (result.statusCode() == 404) {
            throw new IOException("Remote manifest not found, status code: " + result.statusCode());
        } else if (result.statusCode() != 200) {
            throw new IOException("Failed to get remote manifest, status code: " + result.statusCode());
        }
        return SkyblockTweaks.GSON.fromJson(result.body(), RemoteManifest.class);
    }

    public record Branch(String name, Commit commit) {}
    public record Commit(String sha, String url) {}
}
