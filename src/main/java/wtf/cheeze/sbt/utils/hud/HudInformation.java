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
    public Supplier<HUD.AnchorPoint> getAnchorPoint;

    @Nullable
    public Supplier<Float> getMaxNum;
    @Nullable
    public Supplier<Float> getFillNum;

    @Nullable
    public Supplier<Integer> getColor;

    public Consumer<Float> setX;
    public Consumer<Float> setY;
    public Consumer<Float> setScale;
    public Consumer<HUD.AnchorPoint> setAnchorPoint;

    public HudInformation(Supplier<Float> xSupplier, Supplier<Float> ySupplier, Supplier<Float> scaleSupplier, Supplier<HUD.AnchorPoint> anchorPointSupplier, Consumer<Float> xConsumer, Consumer<Float> yConsumer, Consumer<Float> scaleConsumer, Consumer<HUD.AnchorPoint> anchorPointConsumer) {
        this.getX = xSupplier;
        this.getY = ySupplier;
        this.getScale = scaleSupplier;
        this.getAnchorPoint = anchorPointSupplier;

        this.setX = xConsumer;
        this.setY = yConsumer;
        this.setScale = scaleConsumer;
        this.setAnchorPoint = anchorPointConsumer;
    }
    public HudInformation(Supplier<Float> xSupplier, Supplier<Float> ySupplier, Supplier<Float> scaleSupplier, Supplier<HUD.AnchorPoint> anchorPointSupplier, Supplier<Integer> colorSupplier, Supplier<Float> maxNumSupplier, Supplier<Float> fillNumSupplier, Consumer<Float> xConsumer, Consumer<Float> yConsumer, Consumer<Float> scaleConsumer, Consumer<HUD.AnchorPoint> anchorPointConsumer) {
        this.getX = xSupplier;
        this.getY = ySupplier;
        this.getScale = scaleSupplier;
        this.getAnchorPoint = anchorPointSupplier;

        this.getMaxNum = maxNumSupplier;
        this.getFillNum = fillNumSupplier;
        this.getColor = colorSupplier;

        this.setX = xConsumer;
        this.setY = yConsumer;
        this.setScale = scaleConsumer;
        this.setAnchorPoint = anchorPointConsumer;
    }
}
