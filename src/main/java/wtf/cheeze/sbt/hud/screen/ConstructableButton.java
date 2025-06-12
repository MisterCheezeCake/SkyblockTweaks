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

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.function.Consumer;

/**
 * This used to be a MatrixConsumingButton
 */
public class ConstructableButton extends ButtonWidget {

    public ConstructableButton(Text message, PressAction onPress, int x, int y, int width, int height) {
        super(x, y, width, height, message, onPress, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
    }

    public ConstructableButton(Text message, PressAction onPress, int x, int y) {
        this(message, onPress, x, y, 80, 20);
    }

    public ConstructableButton(Text message, PressAction onPress) {
        this(message, onPress, 0, 0);
    }
//
//    @Override
//    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
//        context.getMatrices().push();
//        matrixConsumer.accept(context.getMatrices());
//        super.renderWidget(context, mouseX, mouseY, delta);
//        context.getMatrices().pop();
//    }
}
