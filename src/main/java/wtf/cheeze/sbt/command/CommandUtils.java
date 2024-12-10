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
package wtf.cheeze.sbt.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.utils.enums.Crops;
import wtf.cheeze.sbt.utils.enums.Rarity;
import wtf.cheeze.sbt.utils.enums.Slayers;
import wtf.cheeze.sbt.utils.skyblock.SkyblockConstants;

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

    static int[] getCalcPetTable(Rarity rarity) {
        return switch (rarity) {
            case COMMON -> SkyblockConstants.PET_LEVELS_COMMON;
            case UNCOMMON -> SkyblockConstants.PET_LEVELS_UNCOMMON;
            case RARE -> SkyblockConstants.PET_LEVELS_RARE;
            case EPIC -> SkyblockConstants.PET_LEVELS_EPIC;
            case LEGENDARY, MYTHIC -> SkyblockConstants.PET_LEVELS_LEGENDARY;
            default -> null;
        };
    }

    static int[] getCalcSlayerTable(Slayers slayer) {
        return switch (slayer) {
            case ZOMBIE -> SkyblockConstants.SLAYER_LEVELS_ZOMBIE;
            case SPIDER -> SkyblockConstants.SLAYER_LEVELS_SPIDER;
            case WOLF, ENDERMAN, BLAZE -> SkyblockConstants.SLAYER_LEVELS_WEB;
            case VAMPIRE -> SkyblockConstants.SLAYER_LEVELS_VAMPIRE;
        };
    }

    static int[] getCalcCropTable(Crops crop) {
        return switch (crop) {
            case WHEAT, PUMPKIN, MUSHROOM -> SkyblockConstants.CROP_LEVELS_WPMS;
            case CARROT, POTATO -> SkyblockConstants.CROP_LEVELS_CP;
            case MELON -> SkyblockConstants.CROP_LEVELS_MELON;
            case SUGAR_CANE, CACTUS -> SkyblockConstants.CROP_LEVELS_SCC;
            case COCOA_BEANS, NETHER_WART -> SkyblockConstants.CROP_LEVELS_CBNW;
        };
    }

    static Text getDebugText(String name, boolean value) {
        return Text.of("§3" + name + ": §" + (value ? "a" : "c") + value);
    }

    static Text getDebugText(String name, String value) {
        return Text.of("§3" + name + ": §e" + value);
    }
}
