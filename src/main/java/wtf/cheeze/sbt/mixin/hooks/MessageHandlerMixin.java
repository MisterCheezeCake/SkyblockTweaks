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
package wtf.cheeze.sbt.mixin.hooks;


import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wtf.cheeze.sbt.events.ChatEvents;

@Mixin(MessageHandler.class)
public abstract class MessageHandlerMixin {
    /**
     * Powers {@link ChatEvents}
     */
    @Inject(order = 500, method = "onGameMessage", at = @At("HEAD"))
    private void sbt$onGameMessage(Text message, boolean overlay, CallbackInfo ci) {
        if (overlay) {
            ChatEvents.ON_ACTION_BAR.invoker().onMessage(message);
        } else {
            ChatEvents.ON_GAME.invoker().onMessage(message);
        }
    }
}
