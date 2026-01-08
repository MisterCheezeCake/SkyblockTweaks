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
package wtf.cheeze.sbt.events;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;
import net.minecraft.resources.Identifier;


/**
 * Fabric's Hud Render callback is not that good, so we use this instead
 * This class is copied from Skyblocker, massive credit to Aaron for writing this
 * TODO: Migrate to Fabric API's new events, at least post 1.21.1
 * @author AzureAaron
 */
public class HudRenderEvents {
    /**
     * Called after the hotbar, status bars, and experience bar have been rendered.
     */
    public static final Event<HudRenderStage> AFTER_MAIN_HUD = createEventForStage();

    /**
     * Called before the {@link net.minecraft.client.gui.components.ChatComponent} is rendered.
     */
    public static final Event<HudRenderStage> BEFORE_CHAT = createEventForStage();

    /**
     * Called after the entire HUD is rendered.
     */
    public static final Event<HudRenderStage> LAST = createEventForStage();

    private static Event<HudRenderStage> createEventForStage() {
        return EventFactory.createArrayBacked(HudRenderStage.class, listeners -> (guiGraphics, tickDelta) -> {
            for (HudRenderStage listener : listeners) {
                listener.onRender(guiGraphics, tickDelta);
            }
        });
    }

    public static void registerEvents() {
        // TODO: skyblocker doesnt have this issue of rendering breaking if the vanilla hud element is missing
        HudElementRegistry.attachElementBefore(VanillaHudElements.HOTBAR, Identifier.fromNamespaceAndPath("skyblocktweaks", "after_main_hud"), AFTER_MAIN_HUD.invoker()::onRender);
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.fromNamespaceAndPath("skyblocktweaks", "before_chat"), BEFORE_CHAT.invoker()::onRender);
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("skyblocktweaks", "last"), LAST.invoker()::onRender);
    }

    /**
     * @implNote Similar to Fabric's {@link net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback}
     */
    @FunctionalInterface
    public interface HudRenderStage {
        /**
         * Called sometime during a specific HUD render stage.
         *
         * @param guiGraphics The {@link GuiGraphics} instance
         * @param tickCounter The {@link DeltaTracker} instance
         */
        void onRender(GuiGraphics guiGraphics, DeltaTracker tickCounter);
    }
}
