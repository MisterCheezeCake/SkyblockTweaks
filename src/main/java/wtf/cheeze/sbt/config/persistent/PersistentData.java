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
package wtf.cheeze.sbt.config.persistent;

import com.google.gson.FieldNamingPolicy;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.resources.Identifier;
import wtf.cheeze.sbt.utils.skyblock.SkyblockData;

import java.nio.file.Path;
import java.util.HashMap;

/**
 * This class stores non user-configurable data that needs to be saved between sessions. Almost everything is stored by profile id, which are v4 UUIDs.
 * We use these instead of the cute names so that profiles, even across accounts, will never inherit the persistent data of another
 */
public class PersistentData {
    @SerialEntry
    public HashMap<String, ProfileData> profiles = new HashMap<>();

    public ProfileData currentProfile() {
        return profiles.getOrDefault(SkyblockData.getCurrentProfileUnique(), new ProfileData());
    }


    private static final Path pdPath = FabricLoader.getInstance().getConfigDir().resolve("skyblocktweaks-persistent.json");
    private static final ConfigClassHandler<PersistentData> HANDLER = ConfigClassHandler.createBuilder(PersistentData.class)
            .id(Identifier.fromNamespaceAndPath("skyblocktweaks", "persistent"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config).appendGsonBuilder(builder -> builder.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY))
                    .setPath(pdPath)
                    .build())
            .build();

    public static PersistentData get() {
        return HANDLER.instance();
    }

    private boolean needsSave = false;

    public void requestSave() {
        needsSave = true;
    }
    private static void save() {
        HANDLER.instance().needsSave = false; // Reset the flag
        HANDLER.save();
    }


    public static void registerEvents() {
        HANDLER.load();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!get().needsSave) return;
            if (client.level != null) {
                if (client.screen != null && !(client.screen instanceof ChatScreen)) return; // Don't save while in a screen, but allow the ChatScreen
                save();
            } else {
                save();
            }
        });
    }
}
