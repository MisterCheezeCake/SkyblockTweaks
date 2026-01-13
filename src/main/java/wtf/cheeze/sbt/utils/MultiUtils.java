/*
 * Copyright (C) 2026 MisterCheezeCake
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

//?if >1.21.8
import net.minecraft.client.Minecraft;
//?if <=1.21.8
/*import net.minecraft.client.gui.screens.Screen;*/

/**
 * Misc/Multiversion Utils
 */
public class MultiUtils {

    public static boolean controlDown() {
        //?if >1.21.8 {
        return Minecraft.getInstance().hasControlDown();
        //?} else {
        /*return Screen.hasControlDown();
        *///?}
    }

    public static boolean altDown() {
        //?if >1.21.8 {
        return Minecraft.getInstance().hasAltDown(); 
        //?} else {
        /*return Screen.hasAltDown();
        *///?}
    }

    public static boolean shiftDown() {
        //?if >1.21.8 {
        return Minecraft.getInstance().hasShiftDown(); 
        //?} else {
        /*return Screen.hasShiftDown();
        *///?}
    }


}
