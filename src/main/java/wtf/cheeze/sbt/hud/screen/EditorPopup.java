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

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import wtf.cheeze.sbt.utils.CheezePair;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.render.Popup;
import wtf.cheeze.sbt.utils.render.RenderUtils;

import java.util.List;

public record EditorPopup(Screen screen, int x, int y, Component title, List<CheezePair<String, ? extends AbstractWidget>> children) implements Renderable, Popup {
    @Override
    public List<? extends AbstractWidget> childrenList() {
        return children.stream().map(CheezePair::val).toList();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        Popup.super.renderBackground(guiGraphics);
        RenderUtils.drawCenteredText(guiGraphics, title, x + 40, y + 5, Colors.WHITE, false);
        var renderY = y + 15;
        for (var entry : children) {
            entry.val().render(guiGraphics, mouseX, mouseY, delta);
            RenderUtils.drawCenteredText(guiGraphics, Component.literal(entry.key()), centerX(), renderY, Colors.WHITE, false);
            renderY+= 10;
            var widget = entry.val();
            widget.setX(centerX() - 35);
            widget.setY(renderY);
            widget.setWidth(70);
            widget.setHeight(15);
            renderY += 18;
        }
    }

    public EditorPopup {
        //screen.drawables.add(this);
        for (var entry : children) {
           // screen.addDrawableChild(entry.val());
            screen.addWidget(entry.val());
        }
    }
}
