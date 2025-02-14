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

import net.minecraft.text.*;

public class TextUtils {
    public static final String SECTION  = "§";
    //FIXME: Support uppercase letters in codes
    public static String removeFormatting(String text) {
        return text.replaceAll("§[a-f0-9k-oA-FK-O]", "");
    }
    public static final Text SPACE = Text.literal(" ");

    public static Text getTextThatLinksToURL(MutableText text, Text hovered, String url) {
        return text.styled(style -> {
            style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hovered));
            style = style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
            return style;
        });
    }

    public static Text getTextThatLinksToURL(String text, String hovered, String url) {
        return getTextThatLinksToURL(Text.literal(text), Text.literal(hovered), url);
    }

    public static Text getTextThatRunsCommand(MutableText text, Text hovered, String command) {
        return text.styled(style -> {
            style = style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hovered));
            style = style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
            return style;
        });
    }


    public static Text getTextThatRunsCommand(String text, String hovered, String command) {
        return getTextThatRunsCommand(Text.literal(text), Text.literal(hovered), command);
    }


    public static MutableText withColor(String text, int color) {
        return Text.literal(text).styled(style -> style.withColor(color));
    }
    public static MutableText withColor(MutableText text, int color) {
        return text.styled(style -> style.withColor(color));
    }
    public static MutableText withBold(MutableText text) {
        return text.styled(style -> style.withBold(true));
    }
    public static MutableText withItalic(MutableText text) {
        return text.styled(style -> style.withItalic(true));
    }
    public static MutableText withUnderlined(MutableText text) {
        return text.styled(style -> style.withUnderline(true));
    }
    public static MutableText withStrikethrough(MutableText text) {
        return text.styled(style -> style.withStrikethrough(true));
    }
    public static MutableText withObfuscated(MutableText text) {
        return text.styled(style -> style.withObfuscated(true));
    }

    public static String firstLetterUppercase(String text) {
        if (text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    public static String pascalCase(String text) {
        var split = text.toLowerCase().split(" ");
        var result = new StringBuilder();
        for (var s : split) {
            result.append(firstLetterUppercase(s)).append(" ");
        }
        return result.toString().trim();
    }

    public static MutableText join(Text... texts) {
        var result = Text.empty();
        for (var text : texts) {
            result = result.append(text);
        }
        return result;
    }

    public static ClickEvent copyEvent(String text) {
        return new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, text);
    }
    public static HoverEvent showText(Text text) {
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, text);
    }
}
