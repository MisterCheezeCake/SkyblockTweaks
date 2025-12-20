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
package wtf.cheeze.sbt.hud.screen;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class DecimalSlider extends AbstractSliderButton {
    private final double min;
    private final double max;
    private final double step;
    private final Consumer<Double> valueConsumer;

    public double actualValue() {
        return Math.round((min + (max - min) * this.value) / step) * step;
    }

    public DecimalSlider(int x, int y, int width, int height, Component text, double value, double min, double max, double step, Consumer<Double> valueConsumer) {
        super(x, y, width, height, text, value);
        this.min = min;
        this.max = max;
        this.step = step;
        this.valueConsumer = valueConsumer;
    }

    @Override
    protected void updateMessage() {
        this.setMessage(Component.literal(String.format("%.1f", actualValue())));
    }

    @Override
    protected void applyValue() {
        this.valueConsumer.accept(actualValue());
    }
}
