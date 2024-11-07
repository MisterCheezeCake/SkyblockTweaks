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
package wtf.cheeze.sbt.hud.utils;

import org.jetbrains.annotations.Nullable;
import wtf.cheeze.sbt.utils.DataUtils;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class HudInformation {

    public Supplier<Float> getX;
    public Supplier<Float> getY;
    public Supplier<Float> getScale;
    public Supplier<AnchorPoint> getAnchorPoint;

    @Nullable
    public Supplier<Float> getFill;

    @Nullable
    public Supplier<Integer> getColor;

    public Consumer<Float> setX;
    public Consumer<Float> setY;
    public Consumer<Float> setScale;
    public Consumer<AnchorPoint> setAnchorPoint;

    /**
     * This constructor is used for Single Line Text HUDs and the Ticker HUD
     */
    public HudInformation(Supplier<Float> xSupplier, Supplier<Float> ySupplier, Supplier<Float> scaleSupplier, Supplier<AnchorPoint> anchorPointSupplier, Consumer<Float> xConsumer, Consumer<Float> yConsumer, Consumer<Float> scaleConsumer, Consumer<AnchorPoint> anchorPointConsumer) {
        this.getX = xSupplier;
        this.getY = ySupplier;
        this.getScale = scaleSupplier;
        this.getAnchorPoint = anchorPointSupplier;

        this.setX = xConsumer;
        this.setY = yConsumer;
        this.setScale = scaleConsumer;
        this.setAnchorPoint = anchorPointConsumer;
    }

    /**
     * This constructor is used for Bar HUDs
     */
    public HudInformation(Supplier<Float> xSupplier, Supplier<Float> ySupplier, Supplier<Float> scaleSupplier, Supplier<AnchorPoint> anchorPointSupplier, Supplier<Integer> colorSupplier, Supplier<Float> fillSupplier, Consumer<Float> xConsumer, Consumer<Float> yConsumer, Consumer<Float> scaleConsumer, Consumer<AnchorPoint> anchorPointConsumer) {
        this.getX = xSupplier;
        this.getY = ySupplier;
        this.getScale = scaleSupplier;
        this.getAnchorPoint = anchorPointSupplier;

        this.getFill = fillSupplier;
        this.getColor = colorSupplier;

        this.setX = xConsumer;
        this.setY = yConsumer;
        this.setScale = scaleConsumer;
        this.setAnchorPoint = anchorPointConsumer;
    }

    /**
     * This constructor is used for Multi Line Text HUDs
     */
    public HudInformation(Supplier<Float> xSupplier, Supplier<Float> ySupplier, Supplier<Float> scaleSupplier, Consumer<Float> xConsumer, Consumer<Float> yConsumer, Consumer<Float> scaleConsumer) {
        this.getX = xSupplier;
        this.getY = ySupplier;
        this.getScale = scaleSupplier;
        this.getAnchorPoint = DataUtils.alwaysLeft;

        this.setX = xConsumer;
        this.setY = yConsumer;
        this.setScale = scaleConsumer;
        this.setAnchorPoint = DataUtils.doNothing;
    }
}
