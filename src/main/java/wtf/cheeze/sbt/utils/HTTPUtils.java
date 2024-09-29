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

import java.io.IOException;
import java.net.URL;

public class HTTPUtils {

    public static String get(String uri){
        try {
            URL url = new URL(uri);
            var connection = url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 SkyblockTweaks");
            var reader = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream()));
            var response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
