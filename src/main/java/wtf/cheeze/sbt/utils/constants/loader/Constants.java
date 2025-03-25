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

import wtf.cheeze.sbt.utils.constants.*;

public class Constants {
    public static Garden garden() {
        return ConstantLoader.garden;
    }
    public static Skills skills() { return ConstantLoader.skills;}
    public static Slayers slayers() {
        return ConstantLoader.slayers;
    }
    public static Pets pets() {
        return ConstantLoader.pets;
    }
    public static Hotm hotm() { return ConstantLoader.hotm;}
    public static DisabledFeatures disabledFeatures() { return ConstantLoader.disabledFeatures;}
    public static Minions minions() { return ConstantLoader.minions;}
}
