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
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.config.SkyblockTweaksScreenMain;
import wtf.cheeze.sbt.features.CalcPowder;
import wtf.cheeze.sbt.features.chat.PartyFeatures;
import wtf.cheeze.sbt.utils.NumberUtils;
import wtf.cheeze.sbt.utils.skyblock.SkyblockConstants;
import wtf.cheeze.sbt.utils.skyblock.SkyblockUtils;
import wtf.cheeze.sbt.hud.HudScreen;

import java.util.Arrays;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;


public class SBTCommand {

    public static String PREFIX = "§7[§aSkyblockTweaks§f§7]";
    private static final LiteralArgumentBuilder<FabricClientCommandSource> calc = literal("calc")
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
                                                context.getSource().sendFeedback(Text.of(PREFIX + " §3Total Skill XP Required: §e" + NumberUtils.formatNumber(total, ",")));
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
                                                context.getSource().sendFeedback(Text.of(PREFIX + " §3Total Slayer XP Required: §e" + NumberUtils.formatNumber(needed, ",")));
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
                                                context.getSource().sendFeedback(Text.of(PREFIX + " §3Total Dungeons XP Required: §e" + NumberUtils.formatNumber(total, ",")));
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
                                                context.getSource().sendFeedback(Text.of(PREFIX + " §3Total Pet XP Required: §e" + NumberUtils.formatNumber(total, ",")));
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

                                                if (levelStart < 0 || levelEnd < 0 || levelEnd > 10 || levelStart >= levelEnd) {
                                                    context.getSource().sendFeedback(Text.of(PREFIX + " §cInvalid arguments"));
                                                    return 0;
                                                }

                                                var newArr = Arrays.stream(SkyblockConstants.HOTM_LEVELS).skip(levelStart).limit(levelEnd - levelStart).toArray();
                                                var total = Arrays.stream(newArr).sum();
                                                context.getSource().sendFeedback(Text.of(PREFIX + " §3Total HOTM XP Required: §e" + NumberUtils.formatNumber(total, ",")));
                                                return 1;
                                            }
                                    )


                            ))
                    .executes(context -> {
                        context.getSource().sendFeedback(Text.of(PREFIX + " §cInvalid arguments"));
                        return 0;
                    }))
            .then(literal("garden")
                    .then(argument("level-start", IntegerArgumentType.integer())
                            .then(argument("level-end", IntegerArgumentType.integer())
                                    .executes(context -> {
                                                var levelStart = IntegerArgumentType.getInteger(context, "level-start");
                                                var levelEnd = IntegerArgumentType.getInteger(context, "level-end");

                                                if (levelStart < 0 || levelEnd < 0 || levelEnd > 15 || levelStart >= levelEnd) {
                                                    context.getSource().sendFeedback(Text.of(PREFIX + " §cInvalid arguments"));
                                                    return 0;
                                                }

                                                var newArr = Arrays.stream(SkyblockConstants.GARDEN_LEVELS).skip(levelStart).limit(levelEnd - levelStart).toArray();
                                                var total = Arrays.stream(newArr).sum();
                                                context.getSource().sendFeedback(Text.of(PREFIX + " §3Total Garden XP Required: §e" + NumberUtils.formatNumber(total, ",")));
                                                return 1;
                                            }
                                    )
                            ))
                    .executes(context -> {
                        context.getSource().sendFeedback(Text.of(PREFIX + " §cInvalid arguments"));
                        return 0;
                    })
            )
            .then(literal("crop")
                    .then(argument("crop", StringArgumentType.string()).suggests(CommandUtils.getArrayAsSuggestions(new String[]{"wheat", "pumpkin", "mushroom", "carrot", "potato", "melon", "cane", "cactus", "cocoa", "wart"}))
                            .then(argument("level-start", IntegerArgumentType.integer())
                                    .then(argument("level-end", IntegerArgumentType.integer())
                                            .executes(context -> {
                                                        var crop = SkyblockUtils.castStringToCrop(StringArgumentType.getString(context, "crop"));
                                                        if (crop == null) {
                                                            context.getSource().sendFeedback(Text.of(PREFIX + " §cInvalid crop"));
                                                            return 0;
                                                        }
                                                        var levelStart = IntegerArgumentType.getInteger(context, "level-start");
                                                        var levelEnd = IntegerArgumentType.getInteger(context, "level-end");

                                                        if (levelStart < 0 || levelEnd < 0 || levelEnd > 46 || levelStart >= levelEnd) {
                                                            context.getSource().sendFeedback(Text.of(PREFIX + " §cInvalid arguments"));
                                                            return 0;
                                                        }

                                                        var newArr = Arrays.stream(getCalcCropTable(crop)).skip(levelStart).limit(levelEnd - levelStart).toArray();
                                                        var total = Arrays.stream(newArr).sum();
                                                        context.getSource().sendFeedback(Text.of(PREFIX + " §3Total Crop XP Required: §e" + NumberUtils.formatNumber(total, ",")));
                                                        return 1;
                                                    }
                                            )
                                    ))
                    ).executes(context -> {
                        context.getSource().sendFeedback(Text.of(PREFIX + " §cInvalid arguments"));
                        return 0;
                    })
            )
            .then(literal("powder")
                    .then(argument("perk", StringArgumentType.string()).suggests(CommandUtils.getArrayAsSuggestions(CalcPowder.PERKS.keySet().toArray(new String[0])))
                            .then(argument("level-start", IntegerArgumentType.integer())
                                    .then(argument("level-end", IntegerArgumentType.integer())
                                            .executes(context -> {
                                                        var perk = CalcPowder.PERKS.get(StringArgumentType.getString(context, "perk"));
                                                        if (perk == null) {
                                                            context.getSource().sendFeedback(Text.of(PREFIX + " §cInvalid perk"));
                                                            return 0;
                                                        }
                                                        var levelStart = IntegerArgumentType.getInteger(context, "level-start");
                                                        var levelEnd = IntegerArgumentType.getInteger(context, "level-end");

                                                        if (levelStart < 0 || levelEnd < 0 || levelStart >= levelEnd) {
                                                            context.getSource().sendFeedback(Text.of(PREFIX + " §cInvalid arguments"));
                                                            return 0;
                                                        }
                                                        if (levelEnd > perk.max) {
                                                            context.getSource().sendFeedback(Text.of(PREFIX + " §cTargeted end level is higher than max (§e" + perk.max + "§c)"));
                                                            return 0;
                                                        }

                                                        var total = perk.costBetween(levelStart, levelEnd);
                                                        context.getSource().sendFeedback(Text.of(PREFIX + " §3Total " + perk.powder.getDisplayName() + " Powder Required: §e" + NumberUtils.formatNumber(total, ",")));
                                                        return 1;
                                                    }
                                            )
                                    ))
                    ).executes(context -> {
                        context.getSource().sendFeedback(Text.of(PREFIX + " §cInvalid arguments"));
                        return 0;
                    })

            );

    public static void registerEvents() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                literal("sbt")
                        .then(literal("config")
                                .then(literal("search")
                                        .executes(context -> {
                                            MinecraftClient mc = context.getSource().getClient();
                                            Screen screen = SBTConfig.getGlobalSearchScreen(null);
                                            mc.send(() -> mc.setScreen(screen));
                                            return 1;
                                        })
                                )
                        .executes(context -> {
                            MinecraftClient mc = context.getSource().getClient();
                            Screen screen = SBTConfig.getScreen(null);
                            mc.send(() -> mc.setScreen(screen));
                            return 1;
                        }))
                        .then(CommandUtils.getScreenOpeningCommand("hud", () -> new HudScreen(Text.literal("SkyBlockTweaks"), SkyblockTweaks.HUDS, null)))
                        .then(literal("debug")
                                .then(literal("forcevalue")
                                        .then(argument("key", StringArgumentType.string())
                                                .then(argument("value", StringArgumentType.string())
                                                        .executes(context -> {
                                                            var value = StringArgumentType.getString(context, "value");
                                                            if (value.equals("null")) value = null;

                                                            switch (StringArgumentType.getString(context, "key")) {
                                                                case "inSB" -> SkyblockTweaks.DATA.inSB = Boolean.parseBoolean(value);
                                                                case "alphaNetwork" -> SkyblockTweaks.DATA.alphaNetwork = Boolean.parseBoolean(value);
                                                                case "inParty" -> SkyblockTweaks.DATA.inParty = Boolean.parseBoolean(value);
                                                                case "amITheLeader" -> SkyblockTweaks.DATA.amITheLeader = Boolean.parseBoolean(value);
                                                                case "currentProfile" -> SkyblockTweaks.DATA.currentProfile = value;
                                                                case "mode" -> SkyblockTweaks.DATA.mode = value;
                                                                default -> {
                                                                    context.getSource().sendFeedback(Text.of(PREFIX + " §cInvalid key"));
                                                                    return 0;
                                                                }
                                                            }
                                                            context.getSource().sendFeedback(Text.of(PREFIX + " §3Set " + StringArgumentType.getString(context, "key") + " to " + value));
                                                            return 1;
                                                        })
                                                )

                                        )

                                )
                                .then(literal("partycommands").executes(context -> {
                                    if (PartyFeatures.verboseDebug) {
                                        PartyFeatures.verboseDebug = false;
                                        context.getSource().sendFeedback(Text.of(PREFIX + " §cDisabled party command debug"));
                                    } else {
                                        PartyFeatures.verboseDebug = true;
                                        context.getSource().sendFeedback(Text.of(PREFIX + " §aEnabled party command debug"));
                                    }
                                    return 1;
                                }))


                                .then(literal("dumpComponents")
                                        .then(literal("hand")
                                        .executes(context -> {
                                            var components =  SkyblockTweaks.mc.player.getMainHandStack().getComponents();
                                            components.forEach((component) -> {
                                                context.getSource().sendFeedback(Text.of(PREFIX + " §3" + component.toString()));
                                            });
                                            return 1;
                                        }))
                                        .then(literal("inventory")
                                                .then(argument("number" , IntegerArgumentType.integer())
                                                        .executes(context -> {
                                                            var components = SkyblockTweaks.mc.player.getInventory().getStack(IntegerArgumentType.getInteger(context, "number")).getComponents();
                                                            components.forEach((component) -> {
                                                                context.getSource().sendFeedback(Text.of(PREFIX + " §3" + component.toString()));
                                                            });
                                                            return 1;
                                                        })

                                        ))
                                        .then(literal("container")
                                                .then(argument("number" , IntegerArgumentType.integer())
                                                        .executes(context -> {
                                                            new Thread(() -> {
                                                                try {
                                                                    Thread.sleep(1500);
                                                                    var screen = (GenericContainerScreen) SkyblockTweaks.mc.currentScreen;
                                                                    var components = screen.getScreenHandler().getSlot(IntegerArgumentType.getInteger(context, "number")).getStack().getComponents();
                                                                    components.forEach((component) -> {
                                                                        context.getSource().sendFeedback(Text.of(PREFIX + " §3" + component.toString()));
                                                                    });
                                                                } catch (Exception e) {
                                                                    SkyblockTweaks.LOGGER.error("Error while sleeping", e);
                                                                }

                                                            }).start();
                                                            return 1;
                                                        })
                                        )
                                )
                                ).executes(context -> {
                                    var source = context.getSource();
                                    source.sendFeedback(Text.literal(PREFIX +  " §3Debug Information"));
                                    source.sendFeedback(getDebugText("Version", SkyblockTweaks.VERSION.getVersionString()));
                                    source.sendFeedback(getDebugText("In Skyblock", SkyblockTweaks.DATA.inSB));
                                    source.sendFeedback(getDebugText("Mode", SkyblockTweaks.DATA.mode));
                                    source.sendFeedback(getDebugText("On Alpha Network", SkyblockTweaks.DATA.alphaNetwork));
                                    source.sendFeedback(getDebugText("In Party", SkyblockTweaks.DATA.inParty));
                                    source.sendFeedback(getDebugText("Am I The Leader", SkyblockTweaks.DATA.amITheLeader));
                                    source.sendFeedback(getDebugText("Current Profile", SkyblockTweaks.DATA.currentProfile));
                                    source.sendFeedback(getDebugText("Unique Profile ID", SkyblockTweaks.DATA.getCurrentProfileUnique()));

                                    return 1;
                                })
                        )
                        .then(calc)
                                .executes(context -> {
                                    context.getSource().sendFeedback(Text.of(PREFIX + " §cInvalid arguments"));
                                    return 0;
                                })

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
        return switch (rarity) {
            case COMMON -> SkyblockConstants.PET_LEVELS_COMMON;
            case UNCOMMON -> SkyblockConstants.PET_LEVELS_UNCOMMON;
            case RARE -> SkyblockConstants.PET_LEVELS_RARE;
            case EPIC -> SkyblockConstants.PET_LEVELS_EPIC;
            case LEGENDARY, MYTHIC -> SkyblockConstants.PET_LEVELS_LEGENDARY;
            default -> null;
        };
    }

    private static int[] getCalcSlayerTable(SkyblockConstants.Slayers slayer) {
        return switch (slayer) {
            case ZOMBIE -> SkyblockConstants.SLAYER_LEVELS_ZOMBIE;
            case SPIDER -> SkyblockConstants.SLAYER_LEVELS_SPIDER;
            case WOLF, ENDERMAN, BLAZE -> SkyblockConstants.SLAYER_LEVELS_WEB;
            case VAMPIRE -> SkyblockConstants.SLAYER_LEVELS_VAMPIRE;
        };
    }

    private static int[] getCalcCropTable(SkyblockConstants.Crops crop) {
        return switch (crop) {
            case WHEAT, PUMPKIN, MUSHROOM -> SkyblockConstants.CROP_LEVELS_WPMS;
            case CARROT, POTATO -> SkyblockConstants.CROP_LEVELS_CP;
            case MELON -> SkyblockConstants.CROP_LEVELS_MELON;
            case SUGAR_CANE, CACTUS -> SkyblockConstants.CROP_LEVELS_SCC;
            case COCOA_BEANS, NETHER_WART -> SkyblockConstants.CROP_LEVELS_CBNW;
        };
    }

    private static Text getDebugText(String name, boolean value) {
        return Text.of("§3" + name + ": §" + (value ? "a" : "c") + value);
    }
    private static Text getDebugText(String name, String value) {
        return Text.of("§3" + name + ": §e" + value);
    }

}