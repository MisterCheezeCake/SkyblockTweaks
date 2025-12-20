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
package wtf.cheeze.sbt.utils.text;

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

import java.util.Arrays;

public class TextUtils {
    public static final String SECTION  = "§";

    public static String removeFormatting(String text) {
        return text.replaceAll("§[a-f0-9k-oA-FK-O]", "");
    }
    public static final Component SPACE = Component.literal(" ");
    public static final Component NEW_LINE = Component.literal("\n");

    public static MutableComponent getTextThatLinksToURL(MutableComponent text, Component hovered, String url) {
        return text.withStyle(style -> {
            style = style.withHoverEvent(showTextEvent(hovered));
            style = style.withClickEvent(openURIEvent(url));
            return style;
        });
    }

    public static Component getTextThatLinksToURL(String text, String hovered, String url) {
        return getTextThatLinksToURL(Component.literal(text), Component.literal(hovered), url);
    }

    public static Component getTextThatRunsCommand(MutableComponent text, Component hovered, String command) {
        return text.withStyle(style -> {
            style = style.withHoverEvent(showTextEvent(hovered));
            style = style.withClickEvent(runCommandEvent(command));
            return style;
        });
    }

    public static Component getTextThatRunsCommand(String text, String hovered, String command) {
        return getTextThatRunsCommand(Component.literal(text), Component.literal(hovered), command);
    }

    public static MutableComponent withColor(String text, int color) {
        return withColor(Component.literal(text), color);
    }
    public static MutableComponent withColor(MutableComponent text, int color) {
        return text.withStyle(style -> style.withColor(color));
    }
    public static MutableComponent withBold(String text) {
        return withBold(Component.literal(text));
    }
    public static MutableComponent withBold(MutableComponent text) {
        return text.withStyle(style -> style.withBold(true));
    }
    public static MutableComponent withItalic(MutableComponent text) {
        return text.withStyle(style -> style.withItalic(true));
    }
    public static MutableComponent withUnderlined(MutableComponent text) {
        return text.withStyle(style -> style.withUnderlined(true));
    }
    public static MutableComponent withStrikethrough(MutableComponent text) {
        return text.withStyle(style -> style.withStrikethrough(true));
    }
    public static MutableComponent withObfuscated(MutableComponent text) {
        return text.withStyle(style -> style.withObfuscated(true));
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

    public static MutableComponent join(Component... texts) {
        var result = Component.empty();
        for (var text : texts) {
            result = result.append(text);
        }
        return result;
    }

    public static MutableComponent join(String... strings) {
        return join(Arrays.stream(strings).map(Component::literal).toArray(Component[]::new));
    }

    public static MutableComponent joinLines(Component... lines) {
        var result = Component.empty();
        boolean first = true;
        for (var line : lines) {
            if (!first) {
                result = result.append(NEW_LINE);
            } else {
                first = false;
            }
            result = result.append(line);
        }
        return result;
    }

    public static MutableComponent joinLines(String... lines) {
        return joinLines(Arrays.stream(lines).map(Component::literal).toArray(Component[]::new));
    }



    public static ClickEvent copyEvent(String text) {
        return new ClickEvent.CopyToClipboard(text);
    }

    public static ClickEvent openURIEvent(String uri) {
        return new ClickEvent.OpenUrl(java.net.URI.create(uri));
    }

    public static ClickEvent runCommandEvent(String command) {
        return new ClickEvent.RunCommand(command);
    }

    public static HoverEvent showTextEvent(Component text) {
        return new HoverEvent.ShowText(text);
    }
}
