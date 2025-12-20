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
package wtf.cheeze.sbt.hud.icon;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import wtf.cheeze.sbt.utils.render.RenderUtils;

public class ItemStackIcon implements HudIcon {
    private final ItemStack iconStack;

    public ItemStackIcon(@NotNull ItemStack stack) {
        this.iconStack = stack;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int x, int y, float scale) {
        RenderUtils.beginScale(guiGraphics, 0.5f);
        guiGraphics.renderItem(iconStack, (int) (x / (0.5f *scale)), (int) (y / (0.5f *scale)));
        RenderUtils.popMatrix(guiGraphics);
    }
}
