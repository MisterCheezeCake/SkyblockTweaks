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

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.SkyblockTweaksScreenMain;
import wtf.cheeze.sbt.utils.skyblock.SkyblockConstants;
import wtf.cheeze.sbt.utils.skyblock.SkyblockUtils;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.utils.hud.HudScreen;

import java.util.Arrays;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;


public class SBTCommand {

    public static String PREFIX = "§7[§aSkyblockTweaks§f§7]";

    public static void registerEvents() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                literal("sbt")
                        .then(CommandUtils.getScreenOpeningCommand("config", () -> SkyblockTweaks.CONFIG.getScreen(null)))
                        .then(CommandUtils.getScreenOpeningCommand("hud", () -> new HudScreen(Text.literal("SkyBlockTweaks"), SkyblockTweaks.HUDS, null)))
                        .then(literal("calc")
                                .then(literal("skill")
                                        .then(argument("level-start", IntegerArgumentType.integer())
                                                .then(argument("level-end", IntegerArgumentType.integer())
                                                        .executes(context -> {
                                                                    var levelStart = IntegerArgumentType.getInteger(context, "level-start");
                                                                    var levelEnd = IntegerArgumentType.getInteger(context, "level-end");

                                                                    if (levelStart < 0 || levelEnd < 0 || levelStart > 60 || levelEnd > 60 || levelStart >= levelEnd) {
                                                                        context.getSource().sendFeedback(Text.of(PREFIX + " §cInvalid arguments"));
                                                                        return 0;
                                                                    }

                                                                    var newArr = Arrays.stream(SkyblockConstants.SKILL_LEVELS).skip(levelStart).limit(levelEnd - levelStart).toArray();
                                                                    var total = Arrays.stream(newArr).sum();
                                                                    context.getSource().sendFeedback(Text.of(PREFIX + " §3Total Skill XP Required: §e" + TextUtils.formatNumber(total, ",")));
                                                                    return 1;
                                                                }
                                                        )
                                                )).executes(context -> {
                                            context.getSource().sendFeedback(Text.of(PREFIX + " §cInvalid arguments"));
                                            return 0;
                                        })
                                )
                                .then(literal("slayer")
                                        .then(argument("type", StringArgumentType.string()).suggests(CommandUtils.getArrayAsSuggestions(new String[]{"zombie", "spider", "wolf", "enderman", "blaze", "vampire"}))
                                                .then(argument("level-start", IntegerArgumentType.integer())
                                                        .then(argument("level-end", IntegerArgumentType.integer())
                                                                .executes(context -> {
                                                                    var slayer = SkyblockUtils.castStringToSlayerType(StringArgumentType.getString(context, "type"));
                                                                    if (slayer == null) {
                                                                        context.getSource().sendFeedback(Text.of(PREFIX + " §cInvalid slayer type"));
                                                                        return 0;

                                                                    }
                                                                    var levelStart = IntegerArgumentType.getInteger(context, "level-start");
                                                                    var levelEnd = IntegerArgumentType.getInteger(context, "level-end");
                                                                    var cap = slayer == SkyblockConstants.Slayers.VAMPIRE ? 5 : 9;

                                                                    if (levelStart > levelEnd || levelEnd < 0 || levelStart < 0 || levelStart > cap || levelEnd > cap) {
                                                                        context.getSource().sendFeedback(Text.of(PREFIX + " §cInvalid arguments"));
                                                                        return 0;
                                                                    }

                                                                    var table = getCalcSlayerTable(slayer);
                                                                    var needed = table[levelEnd] - table[levelStart];
                                                                    context.getSource().sendFeedback(Text.of(PREFIX + " §3Total Slayer XP Required: §e" + TextUtils.formatNumber(needed, ",")));
                                                                    return 1;
                                                                })

                                                        ))).executes(context -> {
                                            context.getSource().sendFeedback(Text.of(PREFIX + " §cInvalid arguments"));
                                            return 0;
                                        })
                                )
                                .then(literal("dungeon")
                                        .then(argument("level-start", IntegerArgumentType.integer())
                                                .then(argument("level-end", IntegerArgumentType.integer())
                                                        .executes(context -> {
                                                                    var levelStart = IntegerArgumentType.getInteger(context, "level-start");
                                                                    var levelEnd = IntegerArgumentType.getInteger(context, "level-end");

                                                                    if (levelStart < 0 || levelEnd < 0 || levelStart > 50 || levelEnd > 50 || levelStart >= levelEnd) {
                                                                        context.getSource().sendFeedback(Text.of(PREFIX + " §cInvalid arguments"));
                                                                        return 0;
                                                                    }

                                                                    var newArr = Arrays.stream(SkyblockConstants.DUNGEON_LEVELS).skip(levelStart).limit(levelEnd - levelStart).toArray();
                                                                    var total = Arrays.stream(newArr).sum();
                                                                    context.getSource().sendFeedback(Text.of(PREFIX + " §3Total Dungeons XP Required: §e" + TextUtils.formatNumber(total, ",")));
                                                                    return 1;
                                                                }
                                                        )
                                                ))
                                        .executes(context -> {
                                            context.getSource().sendFeedback(Text.of(PREFIX + " §cInvalid arguments"));
                                            return 0;
                                        })

                                )
                                .then(literal("pet")
                                        .then(argument("rarity", StringArgumentType.string()).suggests(CommandUtils.getArrayAsSuggestions(new String[]{"common", "uncommon", "rare", "epic", "legendary", "mythic"}))
                                                .then(argument("level-start", IntegerArgumentType.integer())
                                                        .then(argument("level-end", IntegerArgumentType.integer())
                                                                .executes(context -> {
                                                                    var rarity = SkyblockUtils.castStringToRarity(StringArgumentType.getString(context, "rarity"));
                                                                    if (rarity == null) {
                                                                        context.getSource().sendFeedback(Text.of(PREFIX + " §cInvalid rarity"));
                                                                        return 0;
                                                                    }
                                                                    var levelStart = IntegerArgumentType.getInteger(context, "level-start");
                                                                    var levelEnd = IntegerArgumentType.getInteger(context, "level-end");

                                                                    var cap = (rarity == SkyblockConstants.Rarity.LEGENDARY || rarity == SkyblockConstants.Rarity.MYTHIC) ? 200 : 100;


                                                                    if (levelStart < 0 || levelEnd < 0 || levelStart > cap || levelEnd > cap || levelStart >= levelEnd) {
                                                                        context.getSource().sendFeedback(Text.of(PREFIX + " §cInvalid arguments"));
                                                                        return 0;
                                                                    }

                                                                    var newArr = Arrays.stream(getCalcPetTable(rarity)).skip(levelStart).limit(levelEnd - levelStart).toArray();
                                                                    var total = Arrays.stream(newArr).sum();
                                                                    context.getSource().sendFeedback(Text.of(PREFIX + " §3Total Pet XP Required: §e" + TextUtils.formatNumber(total, ",")));
                                                                    return 1;
                                                                })
                                                        ).executes(context -> {
                                                            context.getSource().sendFeedback(Text.of(PREFIX + " §cInvalid arguments"));
                                                            return 0;
                                                        })))
                                        .executes(context -> {
                                            context.getSource().sendFeedback(Text.of(PREFIX + " §cInvalid arguments"));
                                            return 0;
                                        })
                                )
                                .then(literal("hotm")
                                        .then(argument("level-start", IntegerArgumentType.integer())
                                                .then(argument("level-end", IntegerArgumentType.integer())
                                                        .executes(context -> {
                                                                    var levelStart = IntegerArgumentType.getInteger(context, "level-start");
                                                                    var levelEnd = IntegerArgumentType.getInteger(context, "level-end");

                                                                    if (levelStart < 0 || levelEnd < 0 || levelStart > 10 || levelEnd > 10 || levelStart >= levelEnd) {
                                                                        context.getSource().sendFeedback(Text.of(PREFIX + " §cInvalid arguments"));
                                                                        return 0;
                                                                    }

                                                                    var newArr = Arrays.stream(SkyblockConstants.HOTM_LEVELS).skip(levelStart).limit(levelEnd - levelStart).toArray();
                                                                    var total = Arrays.stream(newArr).sum();
                                                                    context.getSource().sendFeedback(Text.of(PREFIX + " §3Total HOTM XP Required: §e" + TextUtils.formatNumber(total, ",")));
                                                                    return 1;
                                                                }
                                                        )


                                                ))
                                        .executes(context -> {
                                            context.getSource().sendFeedback(Text.of(PREFIX + " §cInvalid arguments"));
                                            return 0;
                                        }))


                                .executes(context -> {
                                    context.getSource().sendFeedback(Text.of(PREFIX + " §cInvalid arguments"));
                                    return 0;
                                })
                        )
                        .executes(context -> {
                                    MinecraftClient mc = context.getSource().getClient();
                                    Screen screen = new SkyblockTweaksScreenMain(null);
                                    mc.send(() -> mc.setScreen(screen));
                                    return 1;
                                }
                        )
        ));
    }

    private static int[] getCalcPetTable(SkyblockConstants.Rarity rarity) {
        switch (rarity) {
            case COMMON:
                return SkyblockConstants.PET_LEVELS_COMMON;
            case UNCOMMON:
                return SkyblockConstants.PET_LEVELS_UNCOMMON;
            case RARE:
                return SkyblockConstants.PET_LEVELS_RARE;
            case EPIC:
                return SkyblockConstants.PET_LEVELS_EPIC;
            case LEGENDARY, MYTHIC:
                return SkyblockConstants.PET_LEVELS_LEGENDARY;
            default:
                return null;
        }
    }

    private static int[] getCalcSlayerTable(SkyblockConstants.Slayers slayer) {
        switch (slayer) {
            case ZOMBIE:
                return SkyblockConstants.SLAYER_LEVELS_ZOMBIE;
            case SPIDER:
                return SkyblockConstants.SLAYER_LEVELS_SPIDER;
            case WOLF, ENDERMAN, BLAZE:
                return SkyblockConstants.SLAYER_LEVELS_WEB;
            case VAMPIRE:
                return SkyblockConstants.SLAYER_LEVELS_VAMPIRE;
            default:
                return null;
        }
    }

}