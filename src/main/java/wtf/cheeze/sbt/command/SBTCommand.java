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

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.MinecraftVersion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.text.Text;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.config.SkyblockTweaksScreenMain;
import wtf.cheeze.sbt.features.CalcPowder;
import wtf.cheeze.sbt.features.chat.PartyFeatures;
import wtf.cheeze.sbt.utils.MessageManager;
import wtf.cheeze.sbt.utils.NumberUtils;
import wtf.cheeze.sbt.utils.TextUtils;
import wtf.cheeze.sbt.utils.enums.Rarity;
import wtf.cheeze.sbt.utils.enums.Slayers;
import wtf.cheeze.sbt.utils.errors.ErrorHandler;
import wtf.cheeze.sbt.utils.errors.ErrorLevel;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.skyblock.SkyblockConstants;
import wtf.cheeze.sbt.utils.skyblock.SkyblockUtils;
import wtf.cheeze.sbt.hud.HudScreen;
import wtf.cheeze.sbt.utils.tablist.TabListParser;

import java.util.Arrays;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static wtf.cheeze.sbt.command.CommandUtils.calcSend;
import static wtf.cheeze.sbt.command.CommandUtils.send;


public class SBTCommand {

    //public static String PREFIX = "§7[§aSkyblockTweaks§f§7]";

    private static final Text INVALID = TextUtils.withColor("Invalid arguments", Colors.RED);
    private static final LiteralArgumentBuilder<FabricClientCommandSource> calc = literal("calc")
            .then(literal("skill")
                    .then(argument("level-start", IntegerArgumentType.integer())
                            .then(argument("level-end", IntegerArgumentType.integer())
                                    .executes(context -> {
                                                var levelStart = IntegerArgumentType.getInteger(context, "level-start");
                                                var levelEnd = IntegerArgumentType.getInteger(context, "level-end");

                                                if (levelStart < 0 || levelEnd < 0 || levelStart > 60 || levelEnd > 60 || levelStart >= levelEnd) {
                                                    send(context, INVALID);
                                                    return 0;
                                                }

                                                var newArr = Arrays.stream(SkyblockConstants.SKILL_LEVELS).skip(levelStart).limit(levelEnd - levelStart).toArray();
                                                var total = Arrays.stream(newArr).sum();
                                                calcSend(context, "Skill", total);
                                                return 1;
                                            }
                                    )
                            )).executes(context -> {
                        send(context, INVALID);
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
                                                    send(context, TextUtils.withColor("Invalid slayer type", Colors.RED));
                                                    return 0;

                                                }
                                                var levelStart = IntegerArgumentType.getInteger(context, "level-start");
                                                var levelEnd = IntegerArgumentType.getInteger(context, "level-end");
                                                var cap = slayer == Slayers.VAMPIRE ? 5 : 9;

                                                if (levelStart > levelEnd || levelEnd < 0 || levelStart < 0 || levelStart > cap || levelEnd > cap) {
                                                    send(context, INVALID);
                                                    return 0;
                                                }

                                                var table = CommandUtils.getCalcSlayerTable(slayer);
                                                var needed = table[levelEnd] - table[levelStart];
                                                calcSend(context, "Slayer", needed);
                                                return 1;
                                            })

                                    ))).executes(context -> {
                        send(context, INVALID);
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
                                                    send(context, INVALID);
                                                    return 0;
                                                }

                                                var newArr = Arrays.stream(SkyblockConstants.DUNGEON_LEVELS).skip(levelStart).limit(levelEnd - levelStart).toArray();
                                                var total = Arrays.stream(newArr).sum();
                                                calcSend(context, "Dungeon", total);
                                                return 1;
                                            }
                                    )
                            ))
                    .executes(context -> {
                        send(context, INVALID);
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
                                                    send(context, TextUtils.withColor("Invalid rarity", Colors.RED));
                                                    return 0;
                                                }
                                                var levelStart = IntegerArgumentType.getInteger(context, "level-start");
                                                var levelEnd = IntegerArgumentType.getInteger(context, "level-end");

                                                var cap = (rarity == Rarity.LEGENDARY || rarity == Rarity.MYTHIC) ? 200 : 100;


                                                if (levelStart < 0 || levelEnd < 0 || levelStart > cap || levelEnd > cap || levelStart >= levelEnd) {
                                                    send(context, INVALID);
                                                    return 0;
                                                }

                                                var newArr = Arrays.stream(CommandUtils.getCalcPetTable(rarity)).skip(levelStart).limit(levelEnd - levelStart).toArray();
                                                var total = Arrays.stream(newArr).sum();
                                                calcSend(context, "Pet", total);
                                                return 1;
                                            })
                                    ).executes(context -> {
                                        send(context, INVALID);
                                        return 0;
                                    })))
                    .executes(context -> {
                        send(context, INVALID);
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
                                                    send(context, INVALID);
                                                    return 0;
                                                }

                                                var newArr = Arrays.stream(SkyblockConstants.HOTM_LEVELS).skip(levelStart).limit(levelEnd - levelStart).toArray();
                                                var total = Arrays.stream(newArr).sum();
                                                calcSend(context, "HOTM", total);
                                                return 1;
                                            }
                                    )


                            ))
                    .executes(context -> {
                        send(context, INVALID);
                        return 0;
                    }))
            .then(literal("garden")
                    .then(argument("level-start", IntegerArgumentType.integer())
                            .then(argument("level-end", IntegerArgumentType.integer())
                                    .executes(context -> {
                                                var levelStart = IntegerArgumentType.getInteger(context, "level-start");
                                                var levelEnd = IntegerArgumentType.getInteger(context, "level-end");

                                                if (levelStart < 0 || levelEnd < 0 || levelEnd > 15 || levelStart >= levelEnd) {
                                                    send(context, INVALID);
                                                    return 0;
                                                }

                                                var newArr = Arrays.stream(SkyblockConstants.GARDEN_LEVELS).skip(levelStart).limit(levelEnd - levelStart).toArray();
                                                var total = Arrays.stream(newArr).sum();
                                                calcSend(context, "Garden", total);
                                                return 1;
                                            }
                                    )
                            ))
                    .executes(context -> {
                        send(context, INVALID);
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
                                                            send(context, TextUtils.withColor("Invalid crop", Colors.RED));
                                                            return 0;
                                                        }
                                                        var levelStart = IntegerArgumentType.getInteger(context, "level-start");
                                                        var levelEnd = IntegerArgumentType.getInteger(context, "level-end");

                                                        if (levelStart < 0 || levelEnd < 0 || levelEnd > 46 || levelStart >= levelEnd) {
                                                            send(context, INVALID);
                                                            return 0;
                                                        }

                                                        var newArr = Arrays.stream(CommandUtils.getCalcCropTable(crop)).skip(levelStart).limit(levelEnd - levelStart).toArray();
                                                        var total = Arrays.stream(newArr).sum();
                                                        calcSend(context, "Crop", total);
                                                        return 1;
                                                    }
                                            )
                                    ))
                    ).executes(context -> {
                        send(context, INVALID);
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
                                                            send(context, TextUtils.withColor("Invalid perk", Colors.RED));
                                                            return 0;
                                                        }
                                                        var levelStart = IntegerArgumentType.getInteger(context, "level-start");
                                                        var levelEnd = IntegerArgumentType.getInteger(context, "level-end");

                                                        if (levelStart < 0 || levelEnd < 0 || levelStart >= levelEnd) {
                                                            send(context, INVALID);
                                                            return 0;
                                                        }
                                                        if (levelEnd > perk.max) {
                                                            send(context, TextUtils.join(
                                                                    TextUtils.withColor("Targeted end level is higher than max (", Colors.RED),
                                                                    TextUtils.withColor(Integer.toString(perk.max), Colors.YELLOW),
                                                                    TextUtils.withColor(")", Colors.RED)
                                                            ));
                                                            return 0;
                                                        }

                                                        var total = perk.costBetween(levelStart, levelEnd);
                                                        send(context, TextUtils.join(
                                                                TextUtils.withColor("Total " + perk.powder.getDisplayName() + " Powder Required: ", Colors.CYAN),
                                                                TextUtils.withColor(NumberUtils.formatNumber(total, ","), Colors.YELLOW)
                                                        ));
                                                        return 1;
                                                    }
                                            )
                                    ))
                    ).executes(context -> {
                        send(context, INVALID);
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
                        .then(CommandUtils.getScreenOpeningCommand("gui", () -> new HudScreen(Text.literal("SkyBlockTweaks"), SkyblockTweaks.HUDS, null)))
                        .then(CommandUtils.getScreenOpeningCommand("edit", () -> new HudScreen(Text.literal("SkyBlockTweaks"), SkyblockTweaks.HUDS, null)))
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
                                                                case "riftSeconds" -> SkyblockTweaks.DATA.riftSeconds = Integer.parseInt(value);
                                                                default -> {
                                                                    send(context, INVALID);
                                                                    return 0;
                                                                }
                                                            }
                                                            //context.getSource().sendFeedback(Text.of(PREFIX + " §3Set " + StringArgumentType.getString(context, "key") + " to " + value));
                                                            send(context, TextUtils.join(
                                                                    TextUtils.withColor("Set ", Colors.CYAN),
                                                                    TextUtils.withColor(StringArgumentType.getString(context, "key"), Colors.YELLOW),
                                                                    TextUtils.withColor(" to ", Colors.CYAN),
                                                                    TextUtils.withColor(value, Colors.YELLOW)
                                                            ));
                                                            return 1;
                                                        })
                                                )

                                        )

                                )
                                .then(literal("partycommands").executes(context -> {
                                    if (PartyFeatures.verboseDebug) {
                                        PartyFeatures.verboseDebug = false;
                                        send(context, TextUtils.withColor("Disabled party command debug", Colors.RED));
                                    } else {
                                        PartyFeatures.verboseDebug = true;
                                        send(context, TextUtils.withColor("Enabled party command debug", Colors.LIME));
                                    }
                                    return 1;
                                }))

                                .then(literal("sysInfo").executes(context -> {
                                    var source = context.getSource();
                                    send(context, TextUtils.withColor("System Information", Colors.CYAN));
                                    source.sendFeedback(CommandUtils.getDebugText("Minecraft Version", MinecraftVersion.CURRENT.getName()));
                                    source.sendFeedback(CommandUtils.getDebugText("Operating System", System.getProperty("os.name")));
                                    source.sendFeedback(CommandUtils.getDebugText("OS Version", System.getProperty("os.version")));
                                    source.sendFeedback(CommandUtils.getDebugText("Architecture", System.getProperty("os.arch")));
                                    source.sendFeedback(CommandUtils.getDebugText("Java Version", System.getProperty("java.version")));
                                    source.sendFeedback(CommandUtils.getDebugText("Java Vendor", System.getProperty("java.vendor")));
                                    return 1;

                                }))


                                .then(literal("dumpComponents")
                                        .then(literal("hand")
                                        .executes(context -> {
                                            var components =  SkyblockTweaks.mc.player.getMainHandStack().getComponents();
                                            components.forEach((component) -> {
                                                send(context, TextUtils.withColor(component.toString(), Colors.CYAN));
                                            });
                                            return 1;
                                        }))
                                        .then(literal("inventory")
                                                .then(argument("number" , IntegerArgumentType.integer())
                                                        .executes(context -> {
                                                            var components = SkyblockTweaks.mc.player.getInventory().getStack(IntegerArgumentType.getInteger(context, "number")).getComponents();
                                                            components.forEach((component) -> {
                                                                send(context, TextUtils.withColor(component.toString(), Colors.CYAN));

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
                                                                        send(context, TextUtils.withColor(component.toString(), Colors.CYAN));
                                                                    });
                                                                } catch (Exception e) {
                                                                    ErrorHandler.handleError(e, "Thread Sleep Error in Dump Components", ErrorLevel.WARNING);
                                                                }

                                                            }).start();
                                                            return 1;
                                                        })
                                        )
                                ))
                                .then(literal("intentionalError").then(argument("warningOnly", BoolArgumentType.bool())

                                        .executes(context -> {
                                            ErrorHandler.handleError(new RuntimeException("Intentional Error"), "This is an intentional error", BoolArgumentType.getBool(context, "warningOnly") ? ErrorLevel.WARNING : ErrorLevel.CRITICAL);
                                            return 1;
                                        }))
                                )
                                .then(literal("fullData").executes(context -> {
                                    var source = context.getSource();
                                    send(context, TextUtils.withColor("Full Data Dump", Colors.CYAN));
                                    source.sendFeedback(CommandUtils.getDebugText("Defence", SkyblockTweaks.DATA.defense));
                                    source.sendFeedback(CommandUtils.getDebugText("Max Health", SkyblockTweaks.DATA.maxHealth));
                                    source.sendFeedback(CommandUtils.getDebugText("Health", SkyblockTweaks.DATA.health));
                                    source.sendFeedback(CommandUtils.getDebugText("Max Mana", SkyblockTweaks.DATA.maxMana));
                                    source.sendFeedback(CommandUtils.getDebugText("Mana", SkyblockTweaks.DATA.mana));
                                    source.sendFeedback(CommandUtils.getDebugText("Overflow Mana", SkyblockTweaks.DATA.overflowMana));
                                    source.sendFeedback(CommandUtils.getDebugText("Drill Fuel", SkyblockTweaks.DATA.drillFuel));
                                    source.sendFeedback(CommandUtils.getDebugText("Max Drill Fuel", SkyblockTweaks.DATA.maxDrillFuel));
                                    source.sendFeedback(CommandUtils.getDebugText("Rift Seconds", SkyblockTweaks.DATA.riftSeconds));
                                    source.sendFeedback(CommandUtils.getDebugText("Rift Ticking", SkyblockTweaks.DATA.riftTicking));
                                    source.sendFeedback(CommandUtils.getDebugText("Max Tickers", SkyblockTweaks.DATA.maxTickers));
                                    source.sendFeedback(CommandUtils.getDebugText("Tickers", SkyblockTweaks.DATA.tickers));
                                    source.sendFeedback(CommandUtils.getDebugText("Ticker Active", SkyblockTweaks.DATA.tickerActive));
                                    source.sendFeedback(CommandUtils.getDebugText("Armor Stack", SkyblockTweaks.DATA.armorStack));
                                    source.sendFeedback(CommandUtils.getDebugText("Stack String", SkyblockTweaks.DATA.stackString));


                                    return 1;
                                })).then(literal("dumpTablist").executes(context -> {
                                    SkyblockTweaks.LOGGER.info(TabListParser.parseTabList().serialize());
                                    MessageManager.send("Tablist data dumped to logs", Colors.CYAN);
                                    return 1;
                                })
                                ).executes(context -> {
                                    var source = context.getSource();
                                    send(context, TextUtils.withColor("Debug Information", Colors.CYAN));
                                    source.sendFeedback(CommandUtils.getDebugText("Version", SkyblockTweaks.VERSION.getVersionString()));
                                    source.sendFeedback(CommandUtils.getDebugText("In Skyblock", SkyblockTweaks.DATA.inSB));
                                    //source.sendFeedback(CommandUtils.getDebugText("Mode", SkyblockTweaks.DATA.mode));
                                    source.sendFeedback(CommandUtils.getDebugText("Location", SkyblockTweaks.DATA.location.getName()));
                                    source.sendFeedback(CommandUtils.getDebugText("On Alpha Network", SkyblockTweaks.DATA.alphaNetwork));
                                    source.sendFeedback(CommandUtils.getDebugText("In Party", SkyblockTweaks.DATA.inParty));
                                    source.sendFeedback(CommandUtils.getDebugText("Am I The Leader", SkyblockTweaks.DATA.amITheLeader));
                                    source.sendFeedback(CommandUtils.getDebugText("Current Profile", SkyblockTweaks.DATA.currentProfile));
                                    source.sendFeedback(CommandUtils.getDebugText("Unique Profile ID", SkyblockTweaks.DATA.getCurrentProfileUnique()));

                                    return 1;
                                })
                        )
                        .then(calc)
                                .executes(context -> {
                                    send(context, INVALID);
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
}

