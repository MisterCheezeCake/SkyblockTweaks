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
package wtf.cheeze.sbt.utils;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import java.util.function.Supplier;

public class CommandUtils {

    public static LiteralArgumentBuilder<FabricClientCommandSource> getScreenOpeningCommand(String name, Supplier<Screen> screenFactory) {
        return ClientCommandManager.literal(name).executes(context -> {
            MinecraftClient mc = context.getSource().getClient();
            mc.send(() -> mc.setScreen(screenFactory.get()));
            return 1;
        });
    }

    public static SuggestionProvider<FabricClientCommandSource> getArrayAsSuggestions(String[] args) {
        return (context, builder) -> {
            for (String arg : args) {
                builder.suggest(arg);
            }
            return builder.buildFuture();
        };
    }
}
