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
package wtf.cheeze.sbt.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtils {

    private static final Pattern TIME_PATTERN = Pattern.compile("(?:(?<hours>\\d+)h)?(?:(?<minutes>\\d+)m)?(?:(?<seconds>\\d+)s)?");

    public static int parseDuration(String string) {
        Matcher matcher = TIME_PATTERN.matcher(string);
        int time = 0;
        if (!matcher.matches()) {
            return time;
        }
        if (matcher.group("hours") != null) {
            time += Integer.parseInt(matcher.group("hours")) * 3600;
        }
        if (matcher.group("minutes") != null) {
            time += Integer.parseInt(matcher.group("minutes")) * 60;
        }
        if (matcher.group("seconds") != null) {
            time += Integer.parseInt(matcher.group("seconds"));
        }
        return time;
    }

    public static String toDuration(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        if (hours < 1) {
            if (minutes < 1) {
                return String.format("%ds", secs);
            } else {
                return String.format("%dm%ds", minutes, secs);
            }
        } else {
            return String.format("%dh%dm%ds", hours, minutes, secs);
        }
    }

    public static String formatTime(int seconds, boolean hours) {
        int hoursInt = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        if (hours) {
            return String.format("%02d:%02d:%02d", hoursInt, minutes, secs);
        } else {
            return String.format("%02d:%02d", minutes, secs);
        }
    }

    public static String epochToDate(long epoch) {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(epoch));
    }
}
