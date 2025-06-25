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
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.utils.CheezePair;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.render.Popup;
import wtf.cheeze.sbt.utils.render.RenderUtils;

import java.util.List;

public record EditorPopup(Screen screen, int x, int y, Text title, List<CheezePair<String, ? extends ClickableWidget>> children) implements Drawable, Popup {

    public static final int POPUP_Z = 300;



    @Override
    public List<? extends ClickableWidget> childrenList() {
        return children.stream().map(CheezePair::val).toList();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderUtils.drawWithZ(context, 10 , () -> {
            Popup.super.renderBackground(context);
            RenderUtils.drawCenteredText(context, title, x + 40, y + 5, Colors.WHITE, false);
            var renderY = y + 15;
            for (var entry : children) {
                entry.val().render(context, mouseX, mouseY, delta);
                RenderUtils.drawCenteredText(context, Text.literal(entry.key()), centerX(), renderY, Colors.WHITE, false);
                renderY+= 10;
                var widget = entry.val();
                widget.setX(centerX() - 35);
                widget.setY(renderY);
                widget.setWidth(70);
                widget.setHeight(15);
                renderY += 18;
            }
        });
    }

     public EditorPopup {
        //screen.drawables.add(this);
        for (var entry : children) {
           // screen.addDrawableChild(entry.val());
            screen.addSelectableChild(entry.val());
        }
    }


}
