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
package wtf.cheeze.sbt.utils.hud;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class HudInformation {

    public Supplier<Float> getX;
    public Supplier<Float> getY;
    public Supplier<Float> getScale;

    @Nullable
    public Supplier<Float> getMaxNum;
    @Nullable
    public Supplier<Float> getFillNum;

    @Nullable
    public Supplier<Integer> getColor;

    public Consumer<Float> setX;
    public Consumer<Float> setY;
    public Consumer<Float> setScale;

    public HudInformation(Supplier<Float> xSupplier, Supplier<Float> ySupplier, Supplier<Float> scaleSupplier,  Consumer<Float> xConsumer, Consumer<Float> yConsumer, Consumer<Float> scaleConsumer) {
        this.getX = xSupplier;
        this.getY = ySupplier;
        this.getScale = scaleSupplier;


        this.setX = xConsumer;
        this.setY = yConsumer;
        this.setScale = scaleConsumer;
    }
    public HudInformation(Supplier<Float> xSupplier, Supplier<Float> ySupplier, Supplier<Float> scaleSupplier, Supplier<Integer> colorSupplier, Supplier<Float> maxNumSupplier, Supplier<Float> fillNumSupplier, Consumer<Float> xConsumer, Consumer<Float> yConsumer, Consumer<Float> scaleConsumer) {
        this.getX = xSupplier;
        this.getY = ySupplier;
        this.getScale = scaleSupplier;

        this.getMaxNum = maxNumSupplier;
        this.getFillNum = fillNumSupplier;
        this.getColor = colorSupplier;

        this.setX = xConsumer;
        this.setY = yConsumer;
        this.setScale = scaleConsumer;
    }





}
