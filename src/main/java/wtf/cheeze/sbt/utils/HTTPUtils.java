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

import wtf.cheeze.sbt.SkyblockTweaks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;

public class HTTPUtils {

    public static HTTPResponse get(String uri) throws URISyntaxException, IOException {
        URL url = new URI(uri).toURL();
        var connection = url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 SkyblockTweaks " + SkyblockTweaks.VERSION.getVersionString());
        var reader = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream()));

        var response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        return new HTTPResponse(((HttpURLConnection) connection).getResponseCode(), response.toString());
    }

    public static void downloadFile(String uri, File destination) throws URISyntaxException, IOException {
        URL url = new URI(uri).toURL();
        var connection = url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 SkyblockTweaks " + SkyblockTweaks.VERSION.getVersionString());
        var channel = Channels.newChannel(connection.getInputStream());
        try (FileOutputStream fileOutputStream = new FileOutputStream(destination)) {
            FileChannel fileChannel = fileOutputStream.getChannel();
            fileChannel.transferFrom(channel, 0, Long.MAX_VALUE);
        }
    }

    public record HTTPResponse(
            int statusCode,
            String body
    ) {}
}
