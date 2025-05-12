/*
 * Copyright (C) 2025 MisterCheezeCake
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
package wtf.cheeze.sbt.utils.errors;



import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import net.minecraft.text.Text;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wtf.cheeze.sbt.config.ConfigImpl;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.utils.text.MessageManager;
import wtf.cheeze.sbt.utils.text.NotificationHandler;
import wtf.cheeze.sbt.utils.text.TextUtils;
import wtf.cheeze.sbt.utils.timing.TimedSet;
import wtf.cheeze.sbt.utils.render.Colors;

import java.util.Arrays;


public class ErrorHandler {

    private static final int RETAIN_ERRORS_FOR_MS = 20000;

    private static final Logger LOGGER = LoggerFactory.getLogger("SkyblockTweaks Error Handler");

    private static final TimedSet<String> errorSet = new TimedSet<>(RETAIN_ERRORS_FOR_MS);

    public static void handleError(Exception e, String message, ErrorLevel level, boolean sendDelayedIfNotInWorld , Object... params) {
        Pair<String, String> messages = getMessages(message);
        String logMessage = messages.getLeft();
        String chatMessage = messages.getRight();
        if (params.length > 0) {
            LOGGER.error(logMessage, e, params);
        } else {
            LOGGER.error(logMessage, e);
        }
        if (!shouldChat(level)) return;
//        if (errorSet.contains(e)) return;
//        errorSet.add(e);
        if (errorSet.contains(message)) return;
        errorSet.add(message);
        //LOGGER.info(String.valueOf(e.hashCode()));
        var msg = Text.literal("Error: " + chatMessage + ". Click to copy the stack trace.").withColor(Colors.RED).styled(it -> it.withClickEvent(TextUtils.copyEvent(Arrays.toString(e.getStackTrace()))).withHoverEvent(TextUtils.showTextEvent(TextUtils.withColor("Click to copy the stack trace", Colors.CYAN))));
        if (MessageManager.checkPlayer()) {
            MessageManager.send(msg);
        } else {
            if (sendDelayedIfNotInWorld) {
                NotificationHandler.pushChat(msg);
            }
        }
    }

    public static void handleError(Exception e, String message, ErrorLevel level) {
        handleError(e, message, level, true);
    }

    private static boolean shouldChat (ErrorLevel level) {
        return level == ErrorLevel.CRITICAL || (level == ErrorLevel.WARNING && SBTConfig.get().chatAllErrors);
    }

//    //private static boolean shouldChat(Exception e) {
//        return !errorSet.contains(e);
//    }
//    private static void internalHandleError(Exception e, String message, ErrorLevel level, Object... params) {
//        Pair<String, String> messages = getMessages(message);
//        String logMessage = messages.getLeft();
//        String chatMessage = messages.getRight();
//
//    }


    /**
     * Parses a message to get the log message and the chat message
     * @return a Pair of Strings, with the log message being left and the chat message being right
     */
    private static Pair<String, String> getMessages(String message) {
        return Pair.of(message.replaceAll("/\\*LOGONLY (.*)\\*/", ""), message.replaceAll("/\\*LOGONLY (.*)\\*/", "$1"));
    }

    public static Option<Boolean> getChatAll(ConfigImpl defaults, ConfigImpl config) {
        return Option.<Boolean>createBuilder()
                .name(Text.translatable("sbt.config.general.errors.chatAll"))
                .description(OptionDescription.of(Text.translatable("sbt.config.general.errors.chatAll.desc")))
                .controller(SBTConfig::generateBooleanController)
                .binding(
                        defaults.chatAllErrors,
                        () -> config.chatAllErrors,
                        value -> config.chatAllErrors = (boolean) value
                )
                .build();
    }





}
