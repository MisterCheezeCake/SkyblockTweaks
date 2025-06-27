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

    private static final int MILLION = 1_000_000;
    private static final int THOUSAND = 1_000;

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

    public static String formatPercent(float decimal) {
        return formattedRound(decimal * 100, 2) + "%";
    }

    public static int parseIntWithKorM(String text) {
        text = text.toLowerCase();
        text = text.replaceAll(",", "");
        var i = Integer.parseInt(text.substring(0, text.length() - 1));
        if (text.endsWith("k")) {
            return i * 1000;
        } else if (text.endsWith("m")) {
            return i * 1_000_000;
        } else {
            return Integer.parseInt(text);
        }
    }

    public static float parsePercentage(String text) {
        text = text.toLowerCase();
        if (text.equals("done")) return 1;
        if (text.endsWith("%")) {
            return Float.parseFloat(text.substring(0, text.length() - 1)) / 100;
        } else {
            return Float.parseFloat(text);
        }
    }
    @SuppressWarnings("DuplicatedCode")
    public static float parseFloatWithKorM(String text) {
        text = text.toLowerCase();
        text = text.replaceAll(",", "");

        return switch (text.charAt(text.length() - 1)) {
            case 'k' -> Float.parseFloat(text.substring(0, text.length() - 1)) * THOUSAND;
            case 'm' -> Float.parseFloat(text.substring(0, text.length() - 1)) * MILLION;
            default -> Float.parseFloat(text);
        };

    }
    public static String formatNumber(int number, String separator) {
        String str = Integer.toString(number);
        return formatNumber(str, separator);
    }

    public static String formatNumber(long number, String separator) {
        String str = Long.toString(number);
        return formatNumber(str, separator);
    }


    private static String formatNumber(String numString, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numString.length(); i++) {
            sb.append(numString.charAt(i));
            if ((numString.length() - i - 1) % 3 == 0 && i != numString.length() - 1) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }


    public static String addKOrM(int number, String separator) {
        if (number >= MILLION) {
            return formatNumber(number / MILLION, separator) + "M";
        } else if (number >= THOUSAND) {
            return formatNumber(number / THOUSAND, separator) + "K";
        } else {
            return formatNumber(number, separator);
        }
    }


}
