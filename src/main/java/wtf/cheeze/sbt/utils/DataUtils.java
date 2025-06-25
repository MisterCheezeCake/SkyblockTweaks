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

import wtf.cheeze.sbt.hud.utils.AnchorPoint;
import wtf.cheeze.sbt.utils.render.Colors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * Contains some commonly used data methods and allows us to more easily track where things like
 * the {@link #ALWAYS_FALSE} and {@link #ALWAYS_TRUE} methods are used.
 */
public class DataUtils {
    public static final Supplier<Boolean> ALWAYS_FALSE = () -> false;
    public static final Supplier<Boolean> ALWAYS_TRUE = () -> true;
    public static final Supplier<Integer> ALWAYS_WHITE = () -> Colors.WHITE;
    public static final Supplier<AnchorPoint> ALWAYS_LEFT = () -> AnchorPoint.LEFT;
    @SuppressWarnings("rawtypes") public static final Consumer DO_NOTHING = (o) -> {};

    @SafeVarargs
    public static<T> ArrayList<T> arrayListOf(T... items) {
        ArrayList<T> list = new ArrayList<>();
        Collections.addAll(list, items);
        return list;
    }
}
