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

    public Supplier getX;
    public Supplier getY;
    public Supplier getScale;
    public Supplier getColor;
    @Nullable
    public Supplier getShadow;
    @Nullable
    public Supplier getMaxNum;
    @Nullable
    public Supplier getFillNum;

    public Consumer setX;
    public Consumer setY;
    public Consumer setScale;

    public HudInformation(Supplier xSupplier, Supplier ySupplier, Supplier scaleSupplier, Supplier shadowSupplier, Supplier colorSupplier, Consumer xConsumer, Consumer yConsumer, Consumer scaleConsumer) {
        this.getX = xSupplier;
        this.getY = ySupplier;
        this.getScale = scaleSupplier;
        this.getShadow = shadowSupplier;
        this.getColor = colorSupplier;

        this.setX = xConsumer;
        this.setY = yConsumer;
        this.setScale = scaleConsumer;
    }
    public HudInformation(Supplier xSupplier, Supplier ySupplier, Supplier scaleSupplier, Supplier colorSupplier, Supplier maxNumSupplier, Supplier fillNumSupplier, Consumer xConsumer, Consumer yConsumer, Consumer scaleConsumer, /*This just exists to provide a more obvious difference in the constructors*/boolean barHud) {
        this.getX = xSupplier;
        this.getY = ySupplier;
        this.getScale = scaleSupplier;
        this.getColor = colorSupplier;
        this.getMaxNum = maxNumSupplier;
        this.getFillNum = fillNumSupplier;

        this.setX = xConsumer;
        this.setY = yConsumer;
        this.setScale = scaleConsumer;
    }


}
