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

import java.io.*;
import java.util.zip.*;

/**
 * Utils for Zipping and Unzipping files.
 * Adapted from  <a href="https://github.com/ChatTriggers/ctjs/blob/3921da9991ac09b872db63a1f4fd5e5cc928c032/src/main/kotlin/com/chattriggers/ctjs/api/client/FileLib.kt#L228">ChatTriggers FileLib</a>
 */
public class ZipUtils {


    private static final int BUFFER_SIZE = 4096;

    public static void unzip(String zipFilePath, File destDirectory) throws IOException {
        try (FileInputStream fis = new FileInputStream(zipFilePath)) {
            unzip(fis, destDirectory);
        }
    }
    public static void unzip(InputStream source, File destDirectory) throws IOException {
        if (!destDirectory.exists()) destDirectory.mkdirs();
        try (ZipInputStream zipIn = new ZipInputStream(source)) {
            ZipEntry entry = zipIn.getNextEntry();
            // iterates over entries in the zip file
            while (entry != null) {
                String filePath = destDirectory + File.separator + entry.getName();
                if (!entry.isDirectory()) {
                    // if the entry is a file, extracts it
                    extractFile(zipIn, filePath);
                } else {
                    // if the entry is a directory, make the directory
                    File dir = new File(filePath);
                    dir.mkdir();
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
    }

    // helper method for unzipping
    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        File toWrite = new File(filePath);
        toWrite.getParentFile().mkdirs();
        toWrite.createNewFile();

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
            byte[] bytesIn = new byte[BUFFER_SIZE];
            int read;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
    }
}
