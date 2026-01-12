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

package wtf.cheeze.sbt.utils.skyblock;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.fabric.event.HypixelModAPICallback;
import net.hypixel.modapi.fabric.event.HypixelModAPIErrorCallback;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;
import net.hypixel.modapi.packet.impl.serverbound.ServerboundPartyInfoPacket;
import net.minecraft.network.chat.Component;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;

public class ModAPI {
    public static void registerEvents() {
        HypixelModAPI.getInstance().subscribeToEventPacket(ClientboundLocationPacket.class);
        HypixelModAPICallback.EVENT.register(SkyblockData::handlePacket);
        HypixelModAPIErrorCallback.EVENT.register(SkyblockData::handleModApiError);

    }

    public static void requestPartyInfo() {

        HypixelModAPI.getInstance().sendPacket(new ServerboundPartyInfoPacket());
    }

    public static Option<Boolean> getShowErrors(ConfigImpl defaults, ConfigImpl config) {
        return Option.<Boolean>createBuilder()
                .name(Component.translatable("sbt.config.general.errors.modApi"))
                .description(OptionDescription.of(Component.translatable("sbt.config.general.errors.modApi.desc")))
                .controller(SBTConfig::generateBooleanController)
                .binding(
                        defaults.chatModApiErrors,
                        () -> config.chatModApiErrors,
                        value -> config.chatModApiErrors = value
                )
                .build();
    }
}
