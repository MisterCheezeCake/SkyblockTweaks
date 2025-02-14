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
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.utils.MessageManager;
import wtf.cheeze.sbt.utils.NumberUtils;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.utils.enums.Crops;
import wtf.cheeze.sbt.utils.enums.Rarity;
import wtf.cheeze.sbt.utils.enums.Slayers;
import wtf.cheeze.sbt.utils.render.Colors;
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

    public static SuggestionProvider<FabricClientCommandSource> getArrayAsSuggestions(String... args) {
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
        return getDebugText(name, String.valueOf(value));
    }
    static Text getDebugText(String name, int value) {
        return getDebugText(name, String.valueOf(value));
    }
    static Text getDebugText(String name, float value) {
        return getDebugText(name, String.valueOf(value));
    }

    static Text getDebugText(String name, String value) {
        return TextUtils.join(
                TextUtils.withColor(name + ": ", Colors.CYAN),
                TextUtils.withColor(value, Colors.YELLOW)
        );
    }

    public static void send(CommandContext<FabricClientCommandSource> context, Text text) {
        context.getSource().sendFeedback(TextUtils.join(MessageManager.PREFIX, TextUtils.SPACE, text));
    }
    public static void send(CommandContext<FabricClientCommandSource> context, String text) {
        context.getSource().sendFeedback(TextUtils.join(MessageManager.PREFIX, TextUtils.SPACE, Text.of(text)));
    }
    public static void sendRaw(CommandContext<FabricClientCommandSource> context, Text text) {
        context.getSource().sendFeedback(text);
    }
    public static void sendRaw(CommandContext<FabricClientCommandSource> context, String text) {
        context.getSource().sendFeedback(Text.of(text));
    }

    public static void calcSend(CommandContext<FabricClientCommandSource> context, String type, int number) {
        //context.getSource().sendFeedback(Text.of(PREFIX + " §3Total Garden XP Required: §e" + NumberUtils.formatNumber(total, ",")));
        send(context, TextUtils.join(
                TextUtils.withColor("Total " + type + " XP Required: ", Colors.CYAN),
                TextUtils.withColor(NumberUtils.formatNumber(number, ","), Colors.YELLOW)
        ));
    }
}
