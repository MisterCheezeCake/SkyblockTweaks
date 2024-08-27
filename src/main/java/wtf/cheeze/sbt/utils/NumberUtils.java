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

public class NumberUtils {
    public static double round(float number, int decimalPlaces) {
        return Math.round(number * Math.pow(10, decimalPlaces)) / Math.pow(10, decimalPlaces);
    }
    public static double round(double number, int decimalPlaces) {
        return Math.round(number * Math.pow(10, decimalPlaces)) / Math.pow(10, decimalPlaces);
    }

    public static String formattedRound(double number, int decimalPlaces) {
        return  decimalPlaces == 0 ? (round(number, decimalPlaces) + "").split("\\.")[0] : round(number, decimalPlaces) + "";
    }

    public static String formatPercent(float progress, float total) {
        return formattedRound(progress / total * 100, 2) + "%";
    }

    public static int parseIntWithKorM(String text) {
        text = text.toLowerCase();
        var i = Integer.parseInt(text.substring(0, text.length() - 1));
        if (text.endsWith("k")) {
            return i * 1000;
        } else if (text.endsWith("m")) {
            return i * 1000000;
        } else {
            return Integer.parseInt(text);
        }
    }

    public static float parseFloatWithKorM(String text) {
        text = text.toLowerCase();
        text = text.replaceAll(",", "");
        if (text.endsWith("k")) {
            var v = Float.parseFloat(text.substring(0, text.length() - 1));
            return v * 1000;
        } else if (text.endsWith("m")) {
            var v = Float.parseFloat(text.substring(0, text.length() - 1));
            return v * 1000000;
        } else {
            return Float.parseFloat(text);
        }
    }

    public static String formatNumber(int number, String seperator) {
        String str = Integer.toString(number);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            sb.append(str.charAt(i));
            if ((str.length() - i - 1) % 3 == 0 && i != str.length() - 1) {
                sb.append(seperator);
            }
        }
        return sb.toString();
    }

    public static String addKOrM(int number, String separator) {
        if (number >= 1000000) {
            return formatNumber(number / 1000000, separator) + "M";
        } else if (number >= 1000) {
            return formatNumber(number / 1000, separator) + "K";
        } else {
            return formatNumber(number, separator);
        }
    }
}
