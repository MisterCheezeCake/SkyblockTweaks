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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.network.chat.Component;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.config.SkyblockTweaksScreenMain;
import wtf.cheeze.sbt.features.mining.FetchurFeatures;
import wtf.cheeze.sbt.features.misc.MouseLock;
import wtf.cheeze.sbt.features.chat.PartyFeatures;
import wtf.cheeze.sbt.hud.HudManager;
import wtf.cheeze.sbt.hud.screen.HudScreen;
import wtf.cheeze.sbt.mixin.accessors.BossHealthOverlayAccessor;
import wtf.cheeze.sbt.utils.skyblock.ItemUtils;
import wtf.cheeze.sbt.utils.text.MessageManager;
import wtf.cheeze.sbt.utils.NumberUtils;
import wtf.cheeze.sbt.utils.text.TextUtils;
import wtf.cheeze.sbt.utils.timing.TimeUtils;
import wtf.cheeze.sbt.utils.constants.loader.ConstantLoader;
import wtf.cheeze.sbt.utils.constants.loader.Constants;
import wtf.cheeze.sbt.utils.enums.Rarity;
import wtf.cheeze.sbt.utils.enums.Slayer;
import wtf.cheeze.sbt.utils.errors.ErrorHandler;
import wtf.cheeze.sbt.utils.errors.ErrorLevel;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.skyblock.SkyblockData;
import wtf.cheeze.sbt.utils.skyblock.SkyblockData.Stats;
import wtf.cheeze.sbt.utils.skyblock.SkyblockUtils;
import wtf.cheeze.sbt.utils.tablist.TabListParser;
import wtf.cheeze.sbt.utils.version.UpdateChecker;
import wtf.cheeze.sbt.utils.version.Version;
import wtf.cheeze.sbt.utils.version.VersionComparison;

import java.util.Arrays;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static wtf.cheeze.sbt.command.CommandUtils.*;


public class SBTCommand {
    //public static String PREFIX = "§7[§aSkyblockTweaks§f§7]";
    private static final Component INVALID = TextUtils.withColor("Invalid arguments", Colors.RED);
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

                                                var newArr = Arrays.stream(Constants.skills().mainSkillLevels()).skip(levelStart).limit(levelEnd - levelStart).toArray();
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
                    .then(argument("type", StringArgumentType.string()).suggests(CommandUtils.getArrayAsSuggestions("zombie", "spider", "wolf", "enderman", "blaze", "vampire"))
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
                                                var cap = slayer == Slayer.VAMPIRE ? 5 : 9;

                                                if (levelStart > levelEnd || levelEnd < 0 || levelStart < 0 || levelStart > cap || levelEnd > cap) {
                                                    send(context, INVALID);
                                                    return 0;
                                                }

                                                var table = Constants.slayers().slayerTables().get(slayer);
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

                                                var newArr = Arrays.stream(Constants.skills().dungeonLevels()).skip(levelStart).limit(levelEnd - levelStart).toArray();
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
                    .then(argument("rarity", StringArgumentType.string()).suggests(CommandUtils.getArrayAsSuggestions("common", "uncommon", "rare", "epic", "legendary", "mythic"))
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

                                                var newArr = Arrays.stream(Constants.hotm().levels()).skip(levelStart).limit(levelEnd - levelStart).toArray();
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

                                                var newArr = Arrays.stream(Constants.garden().gardenLevels()).skip(levelStart).limit(levelEnd - levelStart).toArray();
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
                    .then(argument("crop", StringArgumentType.string()).suggests(CommandUtils.getArrayAsSuggestions("wheat", "pumpkin", "mushroom", "carrot", "potato", "melon", "cane", "cactus", "cocoa", "wart"))
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

                                                        var newArr = Arrays.stream(Constants.garden().getCropTable(crop)).skip(levelStart).limit(levelEnd - levelStart).toArray();
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
                    .then(argument("perk", StringArgumentType.string()).suggests(CommandUtils.getArrayAsSuggestions(Constants.hotm().perks().keySet().toArray(new String[0])))
                            .then(argument("level-start", IntegerArgumentType.integer())
                                    .then(argument("level-end", IntegerArgumentType.integer())
                                            .executes(context -> {
                                                        var perk = Constants.hotm().perks().get(StringArgumentType.getString(context, "perk"));
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
                                                        if (levelEnd > perk.max()) {
                                                            send(context, TextUtils.join(
                                                                    TextUtils.withColor("Targeted end level is higher than max (", Colors.RED),
                                                                    TextUtils.withColor(Integer.toString(perk.max()), Colors.YELLOW),
                                                                    TextUtils.withColor(")", Colors.RED)
                                                            ));
                                                            return 0;
                                                        }

                                                        var total = perk.costBetween(levelStart, levelEnd);
                                                        send(context, TextUtils.join(
                                                                TextUtils.withColor("Total " + perk.powder().getDisplayName() + " Powder Required: ", Colors.CYAN),
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
                                            Minecraft mc = context.getSource().getClient();
                                            Screen screen = SBTConfig.getGlobalSearchScreen(null);
                                            mc.schedule(() -> mc.setScreen(screen));
                                            return 1;
                                        })
                                )
                                .executes(context -> {
                                    Minecraft mc = context.getSource().getClient();
                                    Screen screen = SBTConfig.getScreen(null);
                                    mc.schedule(() -> mc.setScreen(screen));
                                    return 1;
                                }))
                        .then(CommandUtils.getScreenOpeningCommand("hud", () -> new HudScreen(Component.literal("SkyBlockTweaks"), HudManager.HUDS, null)))
                        .then(CommandUtils.getScreenOpeningCommand("gui", () -> new HudScreen(Component.literal("SkyBlockTweaks"), HudManager.HUDS, null)))
                        .then(CommandUtils.getScreenOpeningCommand("edit", () -> new HudScreen(Component.literal("SkyBlockTweaks"), HudManager.HUDS, null)))
                        .then(literal("mouselock").executes(context -> {
                            MouseLock.toggle();
                            return 1;
                        }))
                        .then(literal("debug")
                                        .then(literal("forcevalue")
                                                .then(argument("key", StringArgumentType.string())
                                                        .then(argument("value", StringArgumentType.string())
                                                                .executes(context -> {
                                                                    var value = StringArgumentType.getString(context, "value");
                                                                    if (value.equals("null")) value = null;

                                                                    switch (StringArgumentType.getString(context, "key")) {
                                                                        case "inSB" ->
                                                                                SkyblockData.inSB = Boolean.parseBoolean(value);
                                                                        case "alphaNetwork" ->
                                                                                SkyblockData.alphaNetwork = Boolean.parseBoolean(value);
                                                                        case "inParty" ->
                                                                                SkyblockData.Party.inParty = Boolean.parseBoolean(value);
                                                                        case "leader" ->
                                                                                SkyblockData.Party.leader = Boolean.parseBoolean(value);
                                                                        case "currentProfile" ->
                                                                                SkyblockData.currentProfile = value;
                                                                        case "mode" -> SkyblockData.mode = value;
                                                                        case "riftSeconds" ->
                                                                                Stats.riftSeconds = Integer.parseInt(value);
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
                                        .then(literal("dumpBossbars").executes(context -> {
                                            for (var bar : ((BossHealthOverlayAccessor) context.getSource().getClient().gui.getBossOverlay()).getEvents().values()) {
                                                SkyblockTweaks.LOGGER.info(bar.getName().getString());
                                            }
                                            send(context, TextUtils.withColor("Dumped Bossbar Text to Logs", Colors.CYAN));

                                            return 1;
                                        }))
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
                                            source.sendFeedback(CommandUtils.getDebugText("Minecraft Version", UpdateChecker.mcVersionName()));
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
                                                            var components = context.getSource().getClient().player.getMainHandItem().getComponents();
                                                            components.forEach((component) -> send(context, TextUtils.withColor(component.toString(), Colors.CYAN)));
                                                            return 1;
                                                        }))
                                                .then(literal("inventory")
                                                        .then(argument("number", IntegerArgumentType.integer())
                                                                .executes(context -> {

                                                                    var components = context.getSource().getClient().player.getInventory().getItem(IntegerArgumentType.getInteger(context, "number")).getComponents();
                                                                    components.forEach((component) -> send(context, TextUtils.withColor(component.toString(), Colors.CYAN)));
                                                                    return 1;
                                                                })

                                                        ))
                                                .then(literal("container")
                                                        .then(argument("number", IntegerArgumentType.integer())
                                                                .executes(context -> {
                                                                    new Thread(() -> {
                                                                        try {
                                                                            Thread.sleep(1500);
                                                                            var screen = (ContainerScreen) context.getSource().getClient().screen;
                                                                            var components = screen.getMenu().getSlot(IntegerArgumentType.getInteger(context, "number")).getItem().getComponents();
                                                                            components.forEach((component) -> send(context, TextUtils.withColor(component.toString(), Colors.CYAN)));
                                                                        } catch (Exception e) {
                                                                            ErrorHandler.handle(e, "Thread Sleep Error in Dump Components", ErrorLevel.WARNING);
                                                                        }

                                                                    }).start();
                                                                    return 1;
                                                                })
                                                        )
                                                ))
                                        .then(literal("intentionalError").then(argument("warningOnly", BoolArgumentType.bool())

                                                .executes(context -> {
                                                    ErrorHandler.handle(new RuntimeException("Intentional Error"), "This is an intentional error", BoolArgumentType.getBool(context, "warningOnly") ? ErrorLevel.WARNING : ErrorLevel.CRITICAL);
                                                    return 1;
                                                }))
                                        )
                                        .then(literal("pickaxe").executes(context -> {
                                            if (ItemUtils.isPickaxe(context.getSource().getClient().player.getMainHandItem().getItem())) {
                                                send(context, TextUtils.withColor("You are holding a pickaxe", Colors.LIME));
                                            } else {
                                                send(context, TextUtils.withColor("You are not holding a pickaxe", Colors.RED));
                                            }
                                            return 1;
                                        }))
                                        .then(literal("compareVersions").then(argument("a", StringArgumentType.string()).then(argument("b", StringArgumentType.string())
                                                .executes(context -> {
                                                     Version a = new Version(StringArgumentType.getString(context, "a"));
                                                     Version b = new Version(StringArgumentType.getString(context, "b"));
                                                     VersionComparison comparison = VersionComparison.compare(a, b);
                                                    MessageManager.send("The result of the comparison was: " + comparison.name(), Colors.LIME);
                                                    return 1;
                                                })

                                        )))
                                        .then(literal("fullData").executes(context -> {
                                            var source = context.getSource();
                                            send(context, TextUtils.withColor("Full Data Dump", Colors.CYAN));
                                            source.sendFeedback(CommandUtils.getDebugText("Defence", Stats.defense));
                                            source.sendFeedback(CommandUtils.getDebugText("Max Health", Stats.maxHealth));
                                            source.sendFeedback(CommandUtils.getDebugText("Health", Stats.health));
                                            source.sendFeedback(CommandUtils.getDebugText("Max Mana", Stats.maxMana));
                                            source.sendFeedback(CommandUtils.getDebugText("Mana", Stats.mana));
                                            source.sendFeedback(CommandUtils.getDebugText("Overflow Mana", Stats.overflowMana));
                                            source.sendFeedback(CommandUtils.getDebugText("Drill Fuel", Stats.drillFuel));
                                            source.sendFeedback(CommandUtils.getDebugText("Max Drill Fuel", Stats.maxDrillFuel));
                                            source.sendFeedback(CommandUtils.getDebugText("Rift Seconds", Stats.riftSeconds));
                                            source.sendFeedback(CommandUtils.getDebugText("Rift Ticking", Stats.riftTicking));
                                            source.sendFeedback(CommandUtils.getDebugText("Max Tickers", Stats.maxTickers));
                                            source.sendFeedback(CommandUtils.getDebugText("Tickers", Stats.tickers));
                                            source.sendFeedback(CommandUtils.getDebugText("Ticker Active", Stats.tickerActive));
                                            source.sendFeedback(CommandUtils.getDebugText("Armor Stack", Stats.armorStack));
                                            source.sendFeedback(CommandUtils.getDebugText("Stack String", Stats.stackString));

                                            return 1;
                                        }))
                                        .then(literal("repo")
                                                .then(literal("reload").executes(context -> {
                                                            try {
                                                                ConstantLoader.loadFromFiles();
                                                                send(context, TextUtils.withColor("Reloaded Repo", Colors.LIME));
                                                            } catch (Exception e) {
                                                                ErrorHandler.handle(e, "Error reloading repo", ErrorLevel.CRITICAL);
                                                            }
                                                            return 1;
                                                        }
                                                ))
                                                .executes(context -> {
                                                    var manifest = ConstantLoader.getLocalManifestSafe();
                                                    send(context, TextUtils.withColor("Repo Debug Information", Colors.CYAN));
                                                    sendRaw(context, getDebugText("User", SBTConfig.get().constantLoader.user));
                                                    sendRaw(context, getDebugText("Repo", SBTConfig.get().constantLoader.repo));
                                                    sendRaw(context, getDebugText("Branch", SBTConfig.get().constantLoader.branch));
                                                    if (manifest == null) {
                                                        sendRaw(context, getDebugText("Local Manifest", "null"));
                                                    } else {
                                                        sendRaw(context, getDebugText("Last Update", TimeUtils.epochToDate(manifest.lastUpdate)));
                                                        sendRaw(context, getDebugText("Commit Hash", manifest.commitHash));
                                                        sendRaw(context, getDebugText("Parent Repo", manifest.parentRepo));
                                                        sendRaw(context, getDebugText("Loaded From Fallback", manifest.loadedFromFallback));
                                                    }
                                                    return 0;
                                                })
                                        )
                                        .then(literal("fetchur").executes(context -> {
                                            MessageManager.send(TextUtils.join(TextUtils.withColor("Today Fetchur wants: ", Colors.CYAN), FetchurFeatures.FetchurItem.forToday().display));
                                            return 1;
                                        }))
                                        .then(literal("dumpTablist").executes(context -> {
                                                    SkyblockTweaks.LOGGER.info(TabListParser.parseTabList().serialize());
                                                    MessageManager.send("Tablist data dumped to logs", Colors.CYAN);
                                                    return 1;
                                                })
                                        ).executes(context -> {
                                            var source = context.getSource();
                                            // ModAPI.requestPartyInfo();
                                            send(context, TextUtils.withColor("Debug Information", Colors.CYAN));
                                            source.sendFeedback(CommandUtils.getDebugText("Version", SkyblockTweaks.VERSION.getVersionString()));
                                            source.sendFeedback(CommandUtils.getDebugText("In Skyblock", SkyblockData.inSB));
                                            // source.sendFeedback(CommandUtils.getDebugText("Mode", SkyblockData.Stats.mode));
                                            source.sendFeedback(CommandUtils.getDebugText("Location", SkyblockData.location.getName()));
                                            source.sendFeedback(CommandUtils.getDebugText("On Alpha Network", SkyblockData.alphaNetwork));
                                            source.sendFeedback(CommandUtils.getDebugText("In Party", SkyblockData.Party.inParty));
                                            source.sendFeedback(CommandUtils.getDebugText("Am I The Leader", SkyblockData.Party.leader));
                                            source.sendFeedback(CommandUtils.getDebugText("Current Profile", SkyblockData.currentProfile));
                                            source.sendFeedback(CommandUtils.getDebugText("Unique Profile ID", SkyblockData.getCurrentProfileUnique()));

                                            return 1;
                                        })
                        )
                        .then(calc)
                        .executes(context -> {
                            send(context, INVALID);
                            return 0;
                        })
                        .executes(context -> {
                                    Minecraft mc = context.getSource().getClient();
                                    Screen screen = new SkyblockTweaksScreenMain(null);
                                    mc.schedule(() -> mc.setScreen(screen));
                                    return 1;
                                }
                        )
        ));
    }
}
