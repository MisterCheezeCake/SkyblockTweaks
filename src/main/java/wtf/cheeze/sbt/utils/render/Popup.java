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
package wtf.cheeze.sbt.utils.render;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import wtf.cheeze.sbt.hud.bounds.Bounds;
import wtf.cheeze.sbt.utils.text.TextUtils;

import java.util.List;

public interface Popup extends Renderable {
    Identifier BACKGROUND = Identifier.fromNamespaceAndPath("skyblocktweaks", "gui/panel.png");
    Component SBT_FOOTER = TextUtils.withColor("SBT", Colors.SBT_GREEN);

    int WIDTH = 80;
    int HEIGHT = 130;


    int x();
    int y();

    List<? extends AbstractWidget> childrenList();
    Screen screen();

    default void renderBackground(GuiGraphics guiGraphics) {
        RenderUtils.drawTexture(guiGraphics, BACKGROUND, x(), y(), WIDTH, HEIGHT, WIDTH, HEIGHT);
    }

    default void drawSBTFooter(GuiGraphics guiGraphics, boolean shadow) {
        RenderUtils.drawText(guiGraphics, SBT_FOOTER, x() + WIDTH - 3 - RenderUtils.getStringWidth(SBT_FOOTER.getString()), y() + HEIGHT - 12, Colors.WHITE, shadow);
    }

    default void remove() {
        screen().renderables.remove(this);
        for (var child : childrenList()) {
            screen().removeWidget(child);
        }
    }
    default int centerX() {
        return x() + (WIDTH / 2);
    }
    default Bounds getBounds() {
         return new Bounds(x(), y(), WIDTH, HEIGHT, 1.0f);
    }


    default boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }
    default boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }





}

