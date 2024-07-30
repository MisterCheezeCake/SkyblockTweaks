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
