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

import java.util.function.Predicate;

public class Predicates {

    public static final Predicate<String> ZERO_TO_ONE = string -> {
        if (string.isEmpty()) return true; // allow empty strings
        if (string.startsWith(".")) return false; // don't allow just a dot
        if (string.endsWith(".")) string = string + "0"; // allow trailing dots
        try {
            var f = Float.parseFloat(string);
            return !(f < 0) && !(f > 1);
        } catch (NumberFormatException e) {
            return false;
        }
    };


    public static final Predicate<String> INT = string -> {
        if (string.isEmpty()) return true; // allow empty strings
        if (string.equals("____")) return true; // allow this specific placeholder
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    };
}

