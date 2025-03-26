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

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

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
     * Called before the {@link net.minecraft.client.gui.hud.ChatHud} is rendered.
     */
    public static final Event<HudRenderStage> BEFORE_CHAT = createEventForStage();

    /**
     * Called after the entire HUD is rendered.
     */
    public static final Event<HudRenderStage> LAST = createEventForStage();

    private static Event<HudRenderStage> createEventForStage() {
        return EventFactory.createArrayBacked(HudRenderStage.class, listeners -> (context, tickDelta) -> {
            for (HudRenderStage listener : listeners) {
                listener.onRender(context, tickDelta);
            }
        });
    }

    /**
     * @implNote Similar to Fabric's {@link net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback}
     */
    @FunctionalInterface
    public interface HudRenderStage {
        /**
         * Called sometime during a specific HUD render stage.
         *
         * @param context The {@link DrawContext} instance
         * @param tickCounter The {@link RenderTickCounter} instance
         */
        void onRender(DrawContext context, RenderTickCounter tickCounter);
    }
}