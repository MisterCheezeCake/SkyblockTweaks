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

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.azureaaron.hmapi.events.HypixelPacketEvents;
import net.azureaaron.hmapi.network.HypixelNetworking;
import net.azureaaron.hmapi.network.packet.v1.s2c.LocationUpdateS2CPacket;
import net.minecraft.util.Util;

public class ModAPI {


    private static final int LOCATION_PACKET_VERSION = 1;
    public static void registerEvents() {
        HypixelNetworking.registerToEvents(Util.make(new Object2IntOpenHashMap<>(), map -> {
            map.put(LocationUpdateS2CPacket.ID, LOCATION_PACKET_VERSION);
        }));
        HypixelPacketEvents.PARTY_INFO.register(SkyblockData::handlePacket);
        HypixelPacketEvents.HELLO.register(SkyblockData::handlePacket);
        HypixelPacketEvents.LOCATION_UPDATE.register(SkyblockData::handlePacket);
    }

    public static void requestPartyInfo() {
        HypixelNetworking.sendPartyInfoC2SPacket(2);
    }


}
