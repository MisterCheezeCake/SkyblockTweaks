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
package wtf.cheeze.sbt.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.render.RenderUtils;
import wtf.cheeze.sbt.hud.HudScreen;

public class SkyblockTweaksScreenMain extends Screen {
    public static final Identifier ICON = Identifier.of("skyblocktweaks", "icon.png");
    private final Screen parent;
    public SkyblockTweaksScreenMain(Screen parent) {
        super(Text.literal("SkyBlockTweaks"));
        this.parent = parent;
    }
    @Override
    public void init() {
        MinecraftClient mc = MinecraftClient.getInstance();
        var centerx = mc.getWindow().getScaledWidth() / 2;
        var leftColumn = centerx - 100;
        var rightColumn = centerx + 5;
        ButtonWidget configButton = ButtonWidget.builder(Text.literal("Open Config"), button -> {
            mc.send(() -> mc.setScreen(SBTConfig.getScreen(this)));
        }).dimensions(centerx - 100, 55, 200, 20).build();
        ButtonWidget hudButton = ButtonWidget.builder(Text.literal("Edit HUD Positions"), button -> {
            mc.send(() -> mc.setScreen(new HudScreen(Text.literal("SkyBlockTweaks"), SkyblockTweaks.HUDS, this)));
        }).dimensions(centerx - 100, 85, 200, 20).build();
        ButtonWidget modrinthButton = ButtonWidget.builder(Text.literal("Modrinth"), button -> {
            ConfirmLinkScreen.open(this, "https://modrinth.com/mod/sbt", true);
        }).dimensions(leftColumn, 115, 95, 20).build();
        ButtonWidget githubButton = ButtonWidget.builder(Text.literal("GitHub"), button -> {
            ConfirmLinkScreen.open(this, "https://github.com/MisterCheezeCake/SkyblockTweaks", true);
        }).dimensions(rightColumn, 115, 95, 20).build();
        ButtonWidget discordButton = ButtonWidget.builder(Text.literal("Discord"), button -> {
            ConfirmLinkScreen.open(this, "https://discord.gg/YH3hw926hz", true);
        }).dimensions(leftColumn, 145, 95, 20).build();
        ButtonWidget legalButton = ButtonWidget.builder(Text.literal("Legal"), button -> {
            ConfirmLinkScreen.open(this, "https://github.com/MisterCheezeCake/SkyblockTweaks/blob/main/OPENSOURCE.md", true);
        }).dimensions(rightColumn, 145, 95, 20).build();
        ButtonWidget closeButton = ButtonWidget.builder(Text.literal("Close"), button -> {
            mc.send(() -> mc.setScreen(parent));
        }).dimensions(centerx - 100, 175, 200, 20).build();
        this.addDrawableChild(configButton);
        this.addDrawableChild(hudButton);
        this.addDrawableChild(modrinthButton);
        this.addDrawableChild(githubButton);
        this.addDrawableChild(discordButton);
        this.addDrawableChild(legalButton);
        this.addDrawableChild(closeButton);
        if (MinecraftClient.getInstance().world == null) {
            hudButton.active = false;
        }



    }
    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        var centerX = mc.getWindow().getScaledWidth() / 2;
        super.render(context, mouseX, mouseY, delta);
        RenderUtils.drawCenteredText(context, Text.literal("SkyblockTweaks"), centerX, 3, Colors.SBT_GREEN, true, 2.5f);
        RenderUtils.drawCenteredText(context, Text.literal("v" + SkyblockTweaks.VERSION.getVersionString()), centerX, 25, Colors.WHITE, true);
        RenderUtils.drawCenteredText(context, Text.literal("By MisterCheezeCake"), centerX, 36, Colors.LIGHT_RED, true);
    }
}
