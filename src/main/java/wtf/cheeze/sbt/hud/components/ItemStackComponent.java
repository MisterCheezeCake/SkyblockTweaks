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
package wtf.cheeze.sbt.hud.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import wtf.cheeze.sbt.hud.cache.Cache;
import wtf.cheeze.sbt.hud.cache.UpdateTiming;

import java.util.function.Supplier;

public class ItemStackComponent implements HudComponent {

    private static final int BASE_WIDTH = 16;
    private static final int BASE_HEIGHT = 16;


    private final Cache<ItemStack> cache;
    private final Supplier<ItemStack> itemStack;
    private static final ItemStack ITEM_ERROR =  new ItemStack(Items.BARRIER);


    public ItemStackComponent(UpdateTiming updateTiming, Supplier<ItemStack> itemStack) {
        this.cache = new Cache<>(updateTiming, itemStack, ITEM_ERROR);
        this.itemStack = itemStack;
    }

    public ItemStackComponent(Supplier<ItemStack> itemStack) {
        this( UpdateTiming.FRAME, itemStack);
    }


    @Override
    public int render(DrawContext context, int x, int y, float scale) {
        if (cache.timing == UpdateTiming.FRAME || cache.isDueForUpdate()) {
            cache.update();
        }

            context.drawItem(cache.get(),  (int) (x / scale), (int) (y / scale));
            //? if =1.21.1 {
            /*context.drawItemInSlot(MinecraftClient.getInstance().textRenderer, cache.get(), (int) (x / scale), (int) (y / scale));
            *///?} else {
            
            context.drawStackOverlay(MinecraftClient.getInstance().textRenderer, cache.get(), (int) (x / scale), (int) (y / scale));
            //?}


        return 1;
    }

    @Override
    public int getWidth() {
       return BASE_WIDTH;
    }

    @Override
    public int getHeight() {
        return BASE_HEIGHT;
    }

    @Override
    public int getlines() {
        return 1;
    }
}
