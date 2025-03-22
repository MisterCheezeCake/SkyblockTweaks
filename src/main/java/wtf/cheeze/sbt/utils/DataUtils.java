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

import net.minecraft.client.util.math.MatrixStack;
import wtf.cheeze.sbt.hud.utils.AnchorPoint;

import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * Contains some commonly used data methods and allows us to more easily track where things like
 * the {@link #alwaysFalse} and {@link #alwaysTrue} methods are used.
 */
public class DataUtils {
    public static final Supplier<Boolean> alwaysFalse = () -> false;
    public static final Supplier<Boolean> alwaysTrue = () -> true;
    public static final Supplier<Integer> alwaysZero = () -> 0;
    public static final Supplier<AnchorPoint> alwaysLeft = () -> AnchorPoint.LEFT;
    @SuppressWarnings("rawtypes") public static final Consumer doNothing = (o) -> {};
}
